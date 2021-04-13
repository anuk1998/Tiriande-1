package Local;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import Game.GameManager;
import Game.Level;
import Game.Registration;
import Level.TestLevel;

public class LocalSnarl {
  private static GameManager manager;
  private static String filename = "snarl.levels";
  private static int numOfPlayers = 1;
  private static int startLevelNum = 1;
  private static boolean observerView = false;

  public static void main(String[] args) throws JSONException, IOException {
    ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));

    parseCommandLine(argsList);

    try {
      ArrayList<JSONObject> levels = readFile(filename);
      initializeLevel(levels, startLevelNum, observerView);
      registerPlayersToGame(numOfPlayers);
      runLocalSnarlGame();
    }
    catch (FileNotFoundException e) {
      System.out.println(filename);
      System.out.println("File not found.");
    }
  }

  /**
   * Reads in the given file and parses its contents as a series of JSON Objects and places each found
   * object in a list.
   */
  private static ArrayList<JSONObject> readFile(String filename) throws IOException, JSONException {
    ArrayList<JSONObject> jsonLevels = new ArrayList<>();
    BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
    int numOfLevels = Integer.parseInt(bufferedReader.readLine());

    StringBuilder allLevelObjs = new StringBuilder();
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      if (line.length() > 0) {
        allLevelObjs.append(line);
      }
    }

    JSONTokener jsonTokener = new JSONTokener(allLevelObjs.toString());
    while (numOfLevels > 0) {
      JSONObject levelObj = (JSONObject) jsonTokener.nextValue();
      jsonLevels.add(levelObj);
      numOfLevels--;
    }

    return jsonLevels;
  }

  /**
   * Initializes each level in the given list of levels to be represented as our Level type, then passes
   * that resulting list to GameManager and initializes a GameManager instance for the game.
   */
  private static void initializeLevel(
          ArrayList<JSONObject> levels, int startLevelNum,
          boolean observerView) throws JSONException {
    ArrayList<Level> listOfLevels = new ArrayList<>();
    for (JSONObject jsonLevel : levels) {
      Level level = new Level();
      TestLevel.constructLevel(jsonLevel, level);
      listOfLevels.add(level);
    }

    if (startLevelNum > listOfLevels.size()) {
      System.out.println("Given starting level not valid. Will be starting at Level 1.");
      startLevelNum = 1;
    }
    manager = new GameManager(listOfLevels, startLevelNum);
    manager.setObserverView(observerView);
  }

  /**
   * Registers the given number of players to the game by asking user for their name then formally
   * registering them via the game manager.
   */
  private static void registerPlayersToGame(int numOfPlayers) {
    Scanner scanner = new Scanner(System.in);

    while (numOfPlayers > 0) {
      System.out.println("Please enter a username for your player:");
      String playerName = scanner.nextLine();
      Registration registrationStatus = manager.registerPlayer(playerName, Registration.LOCAL);
      if (registrationStatus.toString().equals("AT_CAPACITY")) {
        System.out.println("Sorry. You have already registered 4 players.");
        break;
      }
      while (registrationStatus.toString().equals("DUPLICATE_NAME")) {
        System.out.println("Sorry. That name has already been chosen. Please pick again:");
        playerName = scanner.nextLine();
        registrationStatus = manager.registerPlayer(playerName, Registration.LOCAL);
      }
      numOfPlayers--;
    }
    System.out.println("--------------");
    System.out.println("\nAll players have been registered. Game is starting!");
    //registers automated adversaries
    manager.registerAutomatedAdversaries();
  }

  /**
   * Runs the localSnarl game by calling the runGame() method in game manager
   * and prints out the player rankings once the game has ended
   */
  private static void runLocalSnarlGame() {
    manager.runGame();
    System.out.println("\nGame has ended.\n");

    //rank player exited numbers
    System.out.println("\nPlayers Ranked By Number Of Times Successfully Exited in the Game:");
    String exitRankings = manager.printPlayerRankings(manager.getAllPlayers(), "exited");
    System.out.println(exitRankings);

    //rank players based on keys found
    System.out.println("\nPlayers Ranked By Number Of Keys Found in the Game:");
    String keyRankings = manager.printPlayerRankings(manager.getAllPlayers(), "key");
    System.out.println(keyRankings);
  }

  /**
   * Parses the command line inputs and re-assigns variables as fit.
   */
  private static void parseCommandLine(ArrayList<String> argsList) {
    if (argsList.contains("--levels")) {
      int index = argsList.indexOf("--levels");
      filename = argsList.get(index + 1);
    }
    if (argsList.contains("--players")) {
      int index = argsList.indexOf("--players");
      numOfPlayers = Integer.parseInt(argsList.get(index + 1));
    }
    if (argsList.contains("--start")) {
      int index = argsList.indexOf("--start");
      startLevelNum = Integer.parseInt(argsList.get(index + 1));
    }
    if (argsList.contains("--observe")) {
      observerView = true;
      numOfPlayers = 1;
    }
  }

}
