package User;

import java.util.ArrayList;
import java.util.Scanner;

import Common.IUser;
import Game.ICharacter;
import Game.Level;
import Game.Position;
import Game.IAdversary;
import Game.Player;

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

  public void broadcastUpdate(Level currentLevel, ICharacter character, boolean isPlayerActive ){
    //boolean beginningOfLevel
    if(character instanceof IAdversary) {
      System.out.println("List of player locations: " + getAllPlayerLocations((currentLevel)));
//      if(beginningOfLevel) {
//        System.out.println("Here is the initial level view: " + currentLevel.renderLevel());
//        beginningOfLevel = false;
//      }
    }
    if (isPlayerActive) {
      System.out.println(character.getName() + ", it is your turn to make a move. You are currently at position " +
              character.getCharacterPosition().toString() + " in the level. Here is your view:");
    } else {
      System.out.println("Sorry, " + character.getName() + ", you're no longer active in the game. No move for you! Here's the view of your last position:");
    }
    System.out.println(renderView(currentLevel, character));
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
  public Position getUserMove(Scanner scanner, ICharacter character) {
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
