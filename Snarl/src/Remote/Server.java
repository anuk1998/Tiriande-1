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
import Game.MessageType;
import Level.TestLevel;

import static java.lang.System.exit;

public class Server {
  static String filename = "snarl.levels";
  static int numOfClients = 4;
  static int waitTimeSeconds = 60;
  static boolean observerView = false;
  static String IPAddress = "127.0.0.1";
  static int portNum = 45678;
  static int startLevelNum = 1;
  static GameManager manager;

  public static void main(String[] args) throws ParseException, JSONException, IOException {
    ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
    parseCommandLine(argsList);

    try {
      System.out.println("DEBUG: Reading in JSON file...");
      ArrayList<JSONObject> levels = readFile(filename);
      System.out.println("DEBUG: Initializing level and game manager...");
      manager = initializeLevelAndRegister(levels, startLevelNum, observerView);
    }
    catch (FileNotFoundException e) {
      System.out.println("File not found.");
    }

    ArrayList<ClientThread> clients = new ArrayList<>();
    int clientCount = 0;

    try {
      ServerSocket serverSocket = new ServerSocket(portNum);
      serverSocket.setSoTimeout(waitTimeSeconds * 1000);
      System.out.println("DEBUG: Set up socket connection");
      while (clientCount < numOfClients) {
        try {
          System.out.println("DEBUG: Waiting for connections...");
          Socket acceptSocket = serverSocket.accept();
          System.out.println("DEBUG: Got a connection!");
          ClientThread clientThread = new ClientThread(acceptSocket, manager);
          clients.add(clientThread);
          clientThread.start();
          clientCount++;
        }
        catch (InterruptedIOException e) {
          System.out.println("DEBUG: Timer ran out. Starting game with who we have.");
          break;
        }
      }

      registerAutomatedAdversaries();
      System.out.println("observerView is: " + observerView);
      manager.setObserverView(observerView);

      System.out.println("DEBUG: Sending start level message to all clients.");
      for (ClientThread client : clients) {
        client.sendToClient("start-level", MessageType.LEVEL_START);
      }

      manager.sendInitialUpdateToUsers();
      manager.runGame();
      System.out.println("DEBUG: Game's over. Closing.");
      for (ClientThread c : clients) {
        c.close();
      }
      serverSocket.close();
    }
    catch (IOException var3) {
      System.out.println("There was a problem. Goodbye.");
      exit(1);
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

  private static GameManager initializeLevelAndRegister(
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

  private static void registerAutomatedAdversaries() {
    int numOfZombies = (int) (Math.floor(startLevelNum / 2) + 1);
    int numOfGhosts = (int) Math.floor((startLevelNum - 1) / 2);

    for (int z=1; z<numOfZombies+1; z++) {
      manager.registerAdversary("zombie" + z, "zombie");
    }
    for (int g=1; g<numOfGhosts+1; g++) {
      manager.registerAdversary("ghost" + g, "ghost");
    }
  }

  private static void parseCommandLine(ArrayList<String> argsList) {
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