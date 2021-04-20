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

import Game.Avatars;
import Game.GameManager;
import Game.GameStatus;
import Game.Level;
import Game.MessageType;
import Game.Registration;
import Game.UpdateType;
import Level.TestLevel;

import static java.lang.System.exit;

public class Server {
  static String filename = "snarl.levels";
  static int numOfPlayers = 4;
  static int numOfAdversaries = 0;
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
      System.out.println("INFO: Reading in JSON file...");
      ArrayList<JSONObject> levels = readFile(filename);
      System.out.println("INFO: Initializing level and game manager...");
      manager = initializeLevelAndRegister(levels, startLevelNum, observerView);
    }
    catch (FileNotFoundException e) {
      System.out.println("File not found.");
    }

    ArrayList<ClientThread> clients = new ArrayList<>();
    int clientCount = 0;

    try {
      ServerSocket serverSocket = new ServerSocket(portNum);
      getConnections(serverSocket, clients, clientCount);
      if (clients.size() == 0) {
        serverSocket.close();
        System.out.println("No one registered. Time has ran out. Goodbye.");
        exit(1);
      }
      playTheGame(clients);
      System.out.println("INFO: Game's over. Closing.");
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

  private static void getConnections(ServerSocket serverSocket, ArrayList<ClientThread> clients, int clientCount) throws IOException {
    System.out.println("INFO: Setting up socket connection");
    serverSocket.setSoTimeout(waitTimeSeconds * 1000);
    while (clientCount < numOfPlayers + numOfAdversaries) {
      try {
        System.out.println("INFO: Waiting for connections...");
        Socket acceptSocket = serverSocket.accept();
        System.out.println("INFO: Got a connection!");
        ClientThread clientThread = new ClientThread(acceptSocket, manager);
        clients.add(clientThread);
        clientThread.start();
        clientCount++;
      }
      catch (InterruptedIOException e) {
        System.out.println("INFO: Timer ran out. Starting game with who we have.");
        break;
      }
    }
  }

  private static void playTheGame(ArrayList<ClientThread> clients) {
    for (ClientThread client : clients) {
      boolean isRegistered = client.registerClient();
      System.out.println("INFO: Registered client: " + isRegistered);
    }
    System.out.println("INFO: Done registering all clients.");

    if (manager.getAllPlayers().size() == 0) {
      for (ClientThread client : clients) {
        client.sendToClient("No players registered. Goodbye.", MessageType.TERMINATE);
      }
      exit(1);
    }
    if (manager.getRemoteAdversaries().size() == 0) {
      registerAutomatedAdversaries();
    }
    manager.setObserverView(observerView);

    System.out.println("INFO: Sending start level message to all clients.");
    for (ClientThread client : clients) {
      client.sendToClient("1", MessageType.LEVEL_START);
    }

    manager.sendUpdateToUsers(UpdateType.START_ROUND, GameStatus.NONE, null);
    manager.runGame();
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
      manager.registerAdversary("zombie" + z, Avatars.ZOMBIE, Registration.LOCAL);
    }
    for (int g=1; g<numOfGhosts+1; g++) {
      manager.registerAdversary("ghost" + g, Avatars.GHOST, Registration.LOCAL);
    }
  }

  private static void parseCommandLine(ArrayList<String> argsList) {
    if (argsList.contains("--levels")) {
      int index = argsList.indexOf("--levels");
      filename = argsList.get(index + 1);
    }
    if (argsList.contains("--players")) {
      int index = argsList.indexOf("--players");
      numOfPlayers = Integer.parseInt(argsList.get(index + 1));
    }
    if (argsList.contains("--adversaries")) {
      int index = argsList.indexOf("--adversaries");
      numOfAdversaries = Integer.parseInt(argsList.get(index + 1));
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