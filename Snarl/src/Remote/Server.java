package Remote;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import Game.GameManager;
import Game.Level;
import Level.TestLevel;

import static java.lang.System.exit;

public class Server {
  String filename = "snarl.levels";
  int numOfClients = 4;
  int waitTimeSeconds = 60;
  boolean observerView = false;
  String IPAddress = "127.0.0.1";
  int portNum = 45678;
  int startLevelNum = 1;
  GameManager manager;

  public void main(String[] args) throws ParseException, JSONException, IOException {
    ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
    parseCommandLine(argsList);

    try {
      ArrayList<JSONObject> levels = readFile(filename);
      initializeLevelAndRegister(levels, numOfClients, observerView);
    }
    catch (FileNotFoundException e) {
      System.out.println("File not found.");
    }

    ArrayList<ClientThread> clients = new ArrayList<>();
    int clientCount = 0;

    try {
      ServerSocket serverSocket = new ServerSocket(portNum);
      while (clientCount < numOfClients) {
        try {
          serverSocket.setSoTimeout(waitTimeSeconds * 100);
          Socket acceptSocket = serverSocket.accept();
          ClientThread clientThread = new ClientThread(acceptSocket, manager);
          clients.add(clientThread);
          clientThread.start();
          clientCount++;
        }
        catch (InterruptedIOException e) {
          break;
        }
      }

      /////
      // loop through all client connections and play the game

      //close sockets
      for (ClientThread conn : clients) {
        conn.close();
      }
      serverSocket.close();
    }
    catch (IOException var3) {
      System.out.println("There was a problem. Goodbye.");
      exit(1);
    }

  }

  private ArrayList<JSONObject> readFile(String filename) throws IOException, JSONException {
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

  private GameManager initializeLevelAndRegister(
          ArrayList<JSONObject> levels, int startLevelNum, boolean observerView) throws JSONException {
    ArrayList<Level> listOfLevels = new ArrayList<>();
    for (JSONObject jsonLevel : levels) {
      Level level = new Level();
      TestLevel.constructLevel(jsonLevel, level);
      listOfLevels.add(level);
    }

    if (startLevelNum > listOfLevels.size()) {
      startLevelNum = 1;
    }

    GameManager manager = new GameManager(listOfLevels, startLevelNum);
    manager.setObserverView(observerView);
    return manager;
  }

  private void registerAutomatedAdversaries(GameManager manager) {
    int numOfZombies = (int) (Math.floor(startLevelNum / 2) + 1);
    int numOfGhosts = (int) Math.floor((startLevelNum - 1) / 2);

    for (int z=1; z<numOfZombies+1; z++) {
      manager.registerAdversary("zombie" + z, "zombie");
    }
    for (int g=1; g<numOfGhosts+1; g++) {
      manager.registerAdversary("ghost" + g, "ghost");
    }
  }

  private static void runRemoteSnarlGame(GameManager manager) {
    manager.runGame();
    System.out.println("\nGame has ended.\n");

    //rank player exited numbers
    System.out.println("Players Ranked By Number Of Times Successfully Exited in the Game:");
    System.out.println(manager.printPlayerExitedRankings());

    //rank players based on keys found
    System.out.println("\nPlayers Ranked By Number Of Keys Found in the Game:");
    System.out.println(manager.printPlayerKeyFoundRankings());

  }

  private void parseCommandLine(ArrayList<String> argsList) {
    if (argsList.contains("--levels")) {
      int index = argsList.indexOf("--levels");
      filename = argsList.get(index + 1);
    }
    if (argsList.contains("--clients")) {
      int index = argsList.indexOf("--clients");
      numOfClients = Integer.parseInt(argsList.get(index + 1));
    }
    if (argsList.contains("--wait")) {
      int index = argsList.indexOf("--wait");
      waitTimeSeconds = Integer.parseInt(argsList.get(index + 1));
    }
    if (argsList.contains("--observe")) {
      observerView = true;
      numOfClients = 1;
    }
    if (argsList.contains("--address")) {
      int index = argsList.indexOf("--address");
      IPAddress = argsList.get(index + 1);
    }
    if (argsList.contains("--port")) {
      int index = argsList.indexOf("--port");
      portNum = Integer.parseInt(argsList.get(index + 1));
    }
  }
}