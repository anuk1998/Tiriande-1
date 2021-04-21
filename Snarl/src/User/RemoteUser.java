package User;

import java.io.IOException;
import java.util.ArrayList;

import Common.IUser;
import Game.GameStatus;
import Game.ICharacter;
import Game.Level;
import Game.MessageType;
import Game.Position;
import Game.IAdversary;
import Remote.ClientThread;

public class RemoteUser implements IUser {
  String MOVE_MESSAGE = "move";
  String END_LEVEL_MESSAGE = "end-level";
  String END_GAME_MESSAGE = "end-game";
  String UPDATE_MESSAGE = "update";
  String OK_MESSAGE = "OK";
  String INVALID_MESSAGE = "Invalid";
  String KEY_MESSAGE = "Key";
  String EJECT_MESSAGE = "Eject";
  String KILLED_MESSAGE = "Killed";
  String EXIT_MESSAGE = "Exit";

  String userName;
  ClientThread clientConnection;

  public RemoteUser(String userName) {
    this.userName = userName;
  }

  public void setRemoteUserConnection(ClientThread conn) {
    this.clientConnection = conn;
  }

  @Override
  public String getUserName() {
    return this.userName;
  }

  @Override
  public void sendMoveUpdate(GameStatus moveStatus, Position destination, ICharacter c){
    if (clientConnection != null) {
      switch (moveStatus) {
        case GHOST_TRANSPORTS:
        case VALID:
          clientConnection.sendToClient(OK_MESSAGE, MessageType.RESULT);
          break;
        case INVALID:
          clientConnection.sendToClient(INVALID_MESSAGE, MessageType.RESULT);
          break;
        case KEY_FOUND:
          clientConnection.sendToClient(KEY_MESSAGE, MessageType.RESULT);
          break;
        case PLAYER_EXPELLED:
          if (c instanceof IAdversary) clientConnection.sendToClient(KILLED_MESSAGE, MessageType.RESULT);
          else clientConnection.sendToClient(EJECT_MESSAGE, MessageType.RESULT);
          break;
        case PLAYER_SELF_ELIMINATES:
          clientConnection.sendToClient(EJECT_MESSAGE, MessageType.RESULT);
          break;
        case PLAYER_EXITED:
          clientConnection.sendToClient(EXIT_MESSAGE, MessageType.RESULT);
          break;
      }
    }
  }

  @Override
  public void broadcastUpdate(Level currentLevel, ICharacter character, boolean isPlayerActive) {
    if (clientConnection != null) {
      clientConnection.sendToClient(UPDATE_MESSAGE, null);
    }
  }

  public ArrayList<Position> getAllAdversaryLocations(Level currentLevel) {
    ArrayList<Position> adversaryLocations = new ArrayList<>();
    for(IAdversary a: currentLevel.getAdversaries()) {
      adversaryLocations.add(a.getCharacterPosition());
    }
    return adversaryLocations;
  }

  public void sendEndLevelMessage() {
    if (clientConnection != null) {
      clientConnection.sendToClient(END_LEVEL_MESSAGE, MessageType.END_LEVEL);
    }
  }

  public void sendEndGameMessage() {
    if (clientConnection != null) {
      clientConnection.sendToClient(END_GAME_MESSAGE, MessageType.END_GAME);
    }
  }

  public void sendStartLevelMessage(int levelNum) {
    if (clientConnection != null) {
      clientConnection.sendToClient(Integer.toString(levelNum), MessageType.LEVEL_START);
    }
  }

  public void sendPlayerUpdateMessage(GameStatus moveStatus, ICharacter movedCharacter, ICharacter thisCharacter) {
    if (clientConnection != null) {
      clientConnection.sendPlayerUpdateMessage(moveStatus, movedCharacter, thisCharacter, this);
    }
  }

  @Override
  public String[][] makeView(Level currentLevel, ICharacter character) {
    Position charPos = character.getCharacterPosition();
    int viewArrayRowCount = 0;
    int viewArrayColCount = 0;

    ArrayList<Integer> rowIndexList = new ArrayList<>();
    for (int r=charPos.getRow()-2; r<=charPos.getRow()+2; r++) {
      if (r > currentLevel.getLevelNumOfRows()-1 || r < 0) continue;
      viewArrayRowCount++;
      rowIndexList.add(r);
    }

    ArrayList<Integer> colIndexList = new ArrayList<>();
    for (int c=charPos.getCol()-2; c<=charPos.getCol()+2; c++) {
      if (c > currentLevel.getLevelNumOfCols()-1 || c < 0) continue;
      viewArrayColCount++;
      colIndexList.add(c);
    }

    String[][] view = new String[viewArrayRowCount][viewArrayColCount];

    int viewArrayRowIndex = 0;
    for (int row : rowIndexList) {
      int viewArrayColIndex = 0;
      for (int col : colIndexList) {
        view[viewArrayRowIndex][viewArrayColIndex] = currentLevel.getTileInLevel(new Position(row, col));
        viewArrayColIndex++;
      }
      viewArrayRowIndex++;
    }

    return view;
  }

  @Override
  public String renderView(Level currentLevel, ICharacter character) {
    String[][] view = makeView(currentLevel, character);
    return outputView(view);
  }

  @Override
  public void renderObserverView(Level currentLevel) {
    if (clientConnection != null) {
      clientConnection.sendToClient("Here is the level observer view:\n" + currentLevel.renderLevel(), MessageType.OBSERVER_VIEW);
    }
  }

  @Override
  public void sendNoMoveUpdate() {
    if (clientConnection != null) {
      clientConnection.sendToClient("You've run out of chances. No move for you this turn." +
                      " You will remain in your current position", MessageType.NO_MOVE);
    }
  }

  public String outputView(String[][] view) {
    StringBuilder viewASCII = new StringBuilder();
    for (String[] strings : view) {
      for (int j = 0; j < strings.length; j++) {
        if (j == strings.length - 1) {
          viewASCII.append(strings[j]).append("\n");
        } else {
          viewASCII.append(strings[j]).append(" ");
        }
      }
    }
    return viewASCII.toString();
  }

  @Override
  public Position getUserMove(ICharacter character) {
    Position newPos = null;
    if (clientConnection == null) {
      return null;
    }
    try {
      Position move = clientConnection.getMoveFromClient(MOVE_MESSAGE, MessageType.MOVE);
      if (move == null) {
        newPos = character.getCharacterPosition();
      }
      else {
        newPos = move;
      }
    }
    catch (IOException ignored) {
    }

    return newPos;
  }

  public void sendInitialUpdate(ICharacter usersCharacter) {
    if (clientConnection != null) {
      clientConnection.sendInitialUpdateToClient(usersCharacter, this);
    }
  }
}
