package Remote;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import Game.GameManager;
import Game.Level;
import Level.TestLevel;

public class Server {
  public Server() {
  }

  public void main(String[] args) throws ParseException, JSONException, IOException {
    ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
    String filename = "snarl.levels";
    int numOfClients = 4;
    int waitTimeSeconds = 0;
    boolean observerView = false;
    int IPAddress = 0;
    int portNum = 0;

    // TODO ABSTRACT THIS
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
      IPAddress = Integer.parseInt(argsList.get(index + 1));
    }
    if (argsList.contains("--port")) {
      int index = argsList.indexOf("--port");
      portNum = Integer.parseInt(argsList.get(index + 1));
    }

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
      //Socket timeout after 60 seconds
      //long startTime = System.currentTimeMillis();
      //long elapsedTimeSecs = (System.currentTimeMillis() - startTime) / 1000;

      while (clientCount < numOfClients) {
        try {
          serverSocket.setSoTimeout(6000);
          Socket acceptSocket = serverSocket.accept();
          ClientThread clientThread = new ClientThread(acceptSocket);
          clients.add(clientThread);
          clientThread.start();
          clientCount++;
        } catch (InterruptedIOException e) {
          e.printStackTrace();
        }

        //elapsedTimeSecs = (System.currentTimeMillis() - startTime) / 1000;
      }
      //start game

      //close sockets
      for (ClientThread conn : clients) {
        conn.close();
      }
      serverSocket.close();
    } catch (IOException var3) {
      System.out.println(var3);
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

  private static void initializeLevelAndRegister(ArrayList<JSONObject> levels, int numOfPlayers, boolean observerView) throws JSONException {
    ArrayList<Level> listOfLevels = new ArrayList<>();
    for (JSONObject jsonLevel : levels) {
      Level level = new Level();
      TestLevel.constructLevel(jsonLevel, level);
      listOfLevels.add(level);
    }

    GameManager manager = new GameManager(listOfLevels, 1);

    Scanner scanner = new Scanner(System.in);
    manager.registerPlayers(numOfPlayers, scanner);

    int numOfZombies = (int) (Math.floor(1 / 2) + 1);
    int numOfGhosts = (int) Math.floor((1 - 1) / 2);

    for (int z=1; z<numOfZombies+1; z++) {
      manager.registerAdversary("zombie" + z, "zombie");
    }
    for (int g=1; g<numOfGhosts+1; g++) {
      manager.registerAdversary("ghost" + g, "ghost");
    }

    manager.setObserverView(observerView);

    // TODO: DON'T CALL THIS RIGHT AWAY. WE WANT TO ACCEPT *ALL* CLIENT CONNS BEFORE STARTING THE GAME
    // TODO: CALL THIS RUNGAME() LATER ON AFTER CLIENTS HAVE CONNECTED, AFTER WHILE LOOP IN MAIN()
    manager.runGame();
    scanner.close();
  }

}

class ClientThread extends Thread {
  private Socket socket;
  private PrintWriter output;

  public ClientThread(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try {
      BufferedReader input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      output = new PrintWriter(socket.getOutputStream(), true);
      StringBuilder clientInput = new StringBuilder();

      // TODO: change this to not be 'true', break when the player hits ctl+d or ctl+c or enters or something
      while (true) {
        String message = input.readLine();
        clientInput.append(message);
      }

      //parse the json
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sendToAllPlayerClients(ArrayList<ClientThread> clients, JSONObject update) {
    for (ClientThread client : clients) {
      client.output.println(update);
    }
  }

  public void close() {
  }
}

