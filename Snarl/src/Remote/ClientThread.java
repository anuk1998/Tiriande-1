package Remote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Game.GameManager;
import Game.ICharacter;
import Game.MessageType;
import Game.Position;
import Game.Registration;
import User.RemoteUser;
import Manager.TestManager;
import Game.Player;

public class ClientThread extends Thread {
  private Socket socket;
  private PrintWriter output;
  private BufferedReader input;
  private GameManager manager;

  public ClientThread(Socket socket, GameManager manager) {
    this.socket = socket;
    this.manager = manager;
  }

  @Override
  public void run() {
  }

  public boolean registerClient() {
    try {
      input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      output = new PrintWriter(socket.getOutputStream(), true);
      output.println(serverWelcomeMessage());
      output.println("name");
      String message = input.readLine();
      Registration status = manager.registerPlayer(message, Registration.REMOTE);
      while (status.toString().equals("DUPLICATE_NAME")) {
        output.println("Name already in use.");
        output.println("name");
        message = input.readLine();
        status = manager.registerPlayer(message, Registration.REMOTE);
      }
      manager.passConnectionToRemoteUser(message, this);
    }
    catch (IOException | JSONException e) {
      e.printStackTrace();
    }
    return true;
  }

  private JSONObject serverWelcomeMessage() throws JSONException {
    JSONObject welcome = new JSONObject();
    welcome.put("type", "welcome");
    welcome.put("info", "Version: 1\nServer Group Owner: Tiriande\nTo Knows:\n- Players get maximum 3 chances to make a valid move, otherwise they lose their turn\n");
    return welcome;
  }

  public void sendToClient(String message, MessageType type) {
    if (type.equals(MessageType.NO_MOVE) ||
            type.equals(MessageType.RESULT)) {
      output.println(message);
    }
    else if (type.equals(MessageType.END_LEVEL)) {
      output.println(makeEndLevelMessage().toString());
    }
    else if (type.equals(MessageType.OBSERVER_VIEW)) {
      output.println("observe");
      output.println(message);
    }
    else if (type.equals(MessageType.LEVEL_START)) {
      output.println(makeStartLevelMessage(Integer.parseInt(message)).toString());
    }
    else if (type.equals(MessageType.END_GAME)){
      output.println(makeEndGameMessage());
    }
  }

  private JSONObject makeEndGameMessage() {
    JSONObject endGame = new JSONObject();
    try{
      endGame.put("type", "end-game");
      endGame.put("scores", playerScoresList());
    }
    catch (JSONException e) {
      return null;
    }
    return endGame;
  }

  private JSONArray playerScoresList() {
    JSONArray playerScoresList = new JSONArray();
    for (Player p : manager.getAllPlayers().values()) {
      JSONObject playerScore = new JSONObject();
      try {
        playerScore.put("type", "player-score");
        playerScore.put("name", p.getName());
        playerScore.put("exits", p.getNumOfTimesExited());
        playerScore.put("ejects", p.getNumOfTimesExpelled());
        playerScore.put("key", p.getNumOfKeysFound());
        playerScoresList.put(playerScore);
      } catch (JSONException ignored) {
      }
    }
    return playerScoresList;
  }

  private JSONObject makeEndLevelMessage() {
    JSONObject endLevel = new JSONObject();
    try {
      for (Player p : manager.getExitedPlayers()) {
        exitedPlayersJSONArray.put(p.getName());
      }
      for (Player p : manager.getExpelledPlayers()) {
        ejectedPlayersJSONArray.put(p.getName());
      }
      endLevel.put("type", "end-level");
      endLevel.put("key", manager.getPlayerWhoFoundKey());
      endLevel.put("exits", exitedPlayersJSONArray);
      endLevel.put("ejects", ejectedPlayersJSONArray);
    }
    catch (JSONException e) {
      return null;
    }
    return endLevel;
  }

  public JSONObject makeStartLevelMessage(int levelNum) {
    JSONArray names = new JSONArray();
    for (String p : this.manager.getAllPlayers().keySet()) {
      names.put(p);
    }
    JSONObject startLevel = new JSONObject();
    try {
      startLevel.put("type", "start-level");
      startLevel.put("level", levelNum);
      startLevel.put("players", names);
    }
    catch (JSONException e) {
      e.getStackTrace();
    }
    return startLevel;
  }

  public void sendInitialUpdateToClient(ICharacter usersCharacter, RemoteUser remoteUser) {
    try {
      TestManager tm = new TestManager();
      JSONObject playerUpdate = tm.formPlayerUpdate(this.manager, this.manager.getCurrentLevel(), usersCharacter, remoteUser);
      playerUpdate.put("message", "Your avatar for the game is: " + usersCharacter.getAvatar());
      output.println(playerUpdate.toString());
    }
    catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public void sendPlayerUpdateMessage(String moveStatus, ICharacter movedCharacter, ICharacter thisCharacter, RemoteUser user) {
    try {
      TestManager tm = new TestManager();
      JSONObject playerUpdate = tm.formPlayerUpdate(this.manager, this.manager.getCurrentLevel(), thisCharacter, user);
      if (moveStatus.equals("PLAYER_EXPELLED")) {
        playerUpdate.put("message", movedCharacter.getName() + " got expelled from the level.");
      }
      else {
        playerUpdate.put("message", movedCharacter.getName() + " made a move and the status of that move was: " + moveStatus);
      }
      output.println(playerUpdate.toString());
    }
    catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public Position getMoveFromClient(String message, MessageType type) throws IOException {
    Position movePos = null;
    if (type.equals(MessageType.MOVE)) {
      output.println(message);
      String move = input.readLine();
      try {
        JSONObject moveObject = new JSONObject(move);
        if (moveObject.isNull("to")) return null;
        JSONArray pos = moveObject.getJSONArray("to");
        movePos = new Position(pos.getInt(0), pos.getInt(1));
      }
      catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return movePos;
  }

  public void close() throws IOException {
    this.input.close();
    this.output.close();
  }
}
