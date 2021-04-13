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
  public static void main(String[] args) throws JSONException, IOException {
    ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
    String filename = "snarl.levels";
    int numOfPlayers = 1;
    int startLevelNum = 1;
    boolean observerView = false;

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

    try {
      ArrayList<JSONObject> levels = readFile(filename);
      initializeLevelAndRegister(levels, numOfPlayers, startLevelNum, observerView);
    }
    catch (FileNotFoundException e) {
      System.out.println("File not found.");
    }
  }

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

  private static void initializeLevelAndRegister(ArrayList<JSONObject> levels, int numOfPlayers, int startLevelNum, boolean observerView) throws JSONException {
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

    GameManager manager = new GameManager(listOfLevels, startLevelNum);
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

    int numOfZombies = (int) (Math.floor(startLevelNum / 2) + 1);
    int numOfGhosts = (int) Math.floor((startLevelNum - 1) / 2);

    for (int z=1; z<numOfZombies+1; z++) {
      manager.registerAdversary("zombie" + z, "zombie");
    }
    for (int g=1; g<numOfGhosts+1; g++) {
      manager.registerAdversary("ghost" + g, "ghost");
    }
    manager.setObserverView(observerView);
    runLocalSnarlGame(manager);

    scanner.close();
    
  }

  private static void runLocalSnarlGame(GameManager manager) {

    manager.runGame();
    System.out.println("\nGame has ended.\n");

    //rank player exited numbers
    System.out.println("Players Ranked By Number Of Times Successfully Exited in the Game:");
    System.out.println(manager.printPlayerExitedOrKeyRankings(manager.getAllPlayers(), "exited"));

    //rank players based on keys found
    System.out.println("\nPlayers Ranked By Number Of Keys Found in the Game:");
    System.out.println(manager.printPlayerExitedOrKeyRankings(manager.getAllPlayers(), "key"));

  }


}
