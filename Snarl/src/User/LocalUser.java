package User;

import java.util.ArrayList;
import java.util.Scanner;

import Common.IUser;
import Game.ICharacter;
import Game.Level;
import Game.Position;
import Game.IAdversary;
import Game.Player;
import Game.*;

public class LocalUser implements IUser {
  String userName;

  public LocalUser(String userName) {
    this.userName = userName;
  }


  @Override
  public String getUserName() {
    return this.userName;
  }

  @Override
  public void sendMoveUpdate(String moveStatus, Position destination, ICharacter c){
   switch(moveStatus) {
     case "INVALID":
       System.out.println("Sorry, that is an invalid move. Please enter a different one.");
       break;
     case "KEY_FOUND":
       System.out.println("Player " + c.getName() + " found the key.");
       break;
     case "PLAYER_EXPELLED":
     case "PLAYER_SELF_ELIMINATES":
       System.out.println("Player " + c.getName() + " was expelled.");
       break;
     case "PLAYER_EXITED":
       System.out.println("Player " + c.getName() + " exited.");
       break;
     case "LEVEL_WON":
       System.out.println("Congrats!! Players have won the level!");
       break;
     case "GAME_WON":
       System.out.print("Congrats! Players have won the game!");
       break;
     case "GAME_LOST":
       System.out.println("Sorry :( Players have lost the game!");
       break;
     case "DEFAULT":
       System.out.println("Default case: will never get here");
   }
  }

  @Override
  public void broadcastUpdate(Level currentLevel, ICharacter character, boolean isPlayerActive) {
    if (character instanceof IAdversary) {
      System.out.println("List of player locations: " + getAllPlayerLocations((currentLevel)));
      System.out.println("List of adversary locations: " + getAllAdversaryLocations(currentLevel));
    }
    else {
      if (isPlayerActive) {
        System.out.println("\n--------------\n");
        System.out.println(character.getName() + ", it is your turn to make a move. You are currently at position " +
                character.getCharacterPosition().toString() + " in the level. Your avatar is: " + character.getAvatar());
        System.out.println("Here is your view:\n");
      } else {
        System.out.println("Sorry, " + character.getName() + ", you're no longer active in the game. No move for you! Here's the view of your last position:");
      }
      System.out.println(renderView(currentLevel, character));
    }
  }

  public ArrayList<Position> getAllAdversaryLocations(Level currentLevel) {
    ArrayList<Position> adversaryLocations = new ArrayList<>();
    for(IAdversary a: currentLevel.getAdversaries()) {
      adversaryLocations.add(a.getCharacterPosition());
    }
    return adversaryLocations;
  }

  public ArrayList<Position> getAllPlayerLocations(Level currentLevel) {
    ArrayList<Position> playerLocations = new ArrayList<>();
    for(Player p: currentLevel.getActivePlayers()) {
      playerLocations.add(p.getCharacterPosition());
    }
    return playerLocations;
  }

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
    System.out.println("Here is the observer view of the current level:");
    System.out.println(currentLevel.renderLevel());
  }

  @Override
  public void sendNoMoveUpdate() {
    System.out.println("You've run out of chances. No move for you this turn.");
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
    Scanner scanner = new Scanner(System.in);
    int rowPos = 0;
    int colPos = 0;

    System.out.println("Would you like to move your position? Please enter 'YES' or 'NO'.");
    String response = scanner.nextLine();

    while (!response.equalsIgnoreCase("no") && !response.equalsIgnoreCase("yes")) {
      System.out.println("Invalid answer.");
      System.out.println("Would you like to move your position? Please enter 'YES' or 'NO'.");
      response = scanner.nextLine();
    }

    //player remains where they are and scanner connection is closed
    if (response.equalsIgnoreCase("NO")) {
      System.out.println("Okay, you will remain where you are.");
      return character.getCharacterPosition();
    }

    System.out.print("Please enter your desired row: ");
    rowPos = getMoveCoordinate(scanner, rowPos);
    System.out.print("Please enter your desired column: ");
    colPos = getMoveCoordinate(scanner, colPos);
    Position move = new Position(rowPos, colPos);

    return move;
  }

  /**
   * Asks the user for a move for their turn via STDin.
   *
   * @param sc the Scanner instance
   * @param pos the type of pos we want (either row or column)
   * @return an integer representing a coordinate
   */
  private int getMoveCoordinate(Scanner sc, int pos) {
    boolean isNotNumber = true;

    while (isNotNumber) {
      String posStr = sc.nextLine();
      String posNoSpace = posStr.replaceAll("\\s+", "");
      try {
        pos = Integer.parseInt(posNoSpace);
        isNotNumber = false;
      }
      catch (NumberFormatException e) {
        System.out.println("Sorry, you entered an invalid value. Please enter a number.");
      }
    }
    return pos;
  }
}
