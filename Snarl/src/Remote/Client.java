package Remote;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Client {
  static BufferedReader input;
  static String host = "localhost";
  static int port = 45678;
  boolean moveTime = false;
  boolean invalid = false;
  boolean actorType = false;
  boolean adversaryType = false;

  public static void main(String[] args) {
    ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
    parseCommandLine(argsList);
    Client client = new Client();
    client.run();
  }

  private static void parseCommandLine(ArrayList<String> argsList) {
    if (argsList.contains("--address")) {
      int index = argsList.indexOf("--address");
      host = argsList.get(index + 1);
    }
    if (argsList.contains("--port")) {
      int index = argsList.indexOf("--port");
      port = Integer.parseInt(argsList.get(index + 1));
    }
  }

  public void run() {
    try {
      Socket clientSocket = new Socket(host, port);
      input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
      Scanner sc = new Scanner(System.in);

      while (true) {
        String serverMessage = input.readLine();
        boolean isResponseNeeded = parseServerMessage(serverMessage);
        if (isResponseNeeded) {
          String reply = sc.nextLine();
          if (actorType) {
            while (!reply.equalsIgnoreCase("P") && !reply.equalsIgnoreCase("A")) {
              System.out.println("Invalid type. Try again.");
              reply = sc.nextLine();
            }
            actorType = false;
          }
          if (adversaryType) {
            while (!reply.equalsIgnoreCase("G") && !reply.equalsIgnoreCase("Z")) {
              System.out.println("Invalid type. Try again.");
              reply = sc.nextLine();
            }
            adversaryType = false;
          }
          if (moveTime || invalid) {
            reply = parsePlayerMoveMessageAsJSON(reply);
            while (reply.equals("invalid")) {
              reply = sc.nextLine();
              reply = parsePlayerMoveMessageAsJSON(reply);
            }
            moveTime = false;
            invalid = false;
          }
          output.println(reply);
        }
      }
    }
    catch (Exception var3) {
      System.out.println("Server closed the connection. Game over.");
    }
  }

  private boolean parseServerMessage(String serverMessage) throws IOException {
    switch (serverMessage) {
      case "name":
        System.out.println("Please enter your name for the game:");
        return true;
      case "actor-type":
        System.out.println("Would you like to register as a player or an adversary? " +
                "Type 'P' for player or 'A' for adversary");
        actorType = true;
        return true;
      case "adversary-type":
        System.out.println("Would you like to register as a ghost or a zombie? " +
                "Enter 'G' for ghost or 'Z' for zombie");
        adversaryType = true;
        return true;
      case "observe":
        String str;
        while ((str = input.readLine()) != null && str.length() != 0) {
          System.out.println(str);
        }
        return false;
      case "move":
        System.out.println("~~~YOUR MOVE:~~~");
        System.out.println("Please supply a position for your next move. Enter with this exact format: [<row>, <column>].");
        System.out.println("If you are a player, type 'null' if you don't wish to move your position.");
        moveTime = true;
        return true;
      case "OK":
        System.out.println("~~~MOVE STATUS:~~~");
        System.out.println("The requested move was valid.");
        return false;
      case "Key":
        System.out.println("~~~MOVE STATUS:~~~");
        System.out.println("You found the key!");
        return false;
      case "Exit":
        System.out.println("~~~MOVE STATUS:~~~");
        System.out.println("You have successfully passed through the exit!");
        return false;
      case "Eject":
        System.out.println("~~~PLAYER STATUS:~~~");
        System.out.println("You have been ejected :(");
        return false;
      case "Killed":
        System.out.println("~~~PLAYER STATUS:~~~");
        System.out.println("You have ejected a player.");
        return false;
      case "Invalid":
        System.out.println("~~~MOVE STATUS:~~~");
        System.out.println("Sorry, that is an invalid move.");
        return false;
    }
    return parsedServerMessageAsJSON(serverMessage);
  }

  private boolean parsedServerMessageAsJSON(String serverMessage) {
    try {
      JSONObject serverMessageAsJSON = new JSONObject(serverMessage);
      String type = serverMessageAsJSON.getString("type");

      switch (type) {
        case "welcome":
          String info = serverMessageAsJSON.getString("info");
          System.out.println("Welcome to Snarl! Here is information about the game:\n" + info);
          System.out.println();
          break;
        case "start-level":
          System.out.println("~~~LEVEL STARTING:~~~");
          startLevelMessageHelper(serverMessageAsJSON);
          break;
        case "player-update":
          System.out.println("~~~GAME UPDATE:~~~");
          playerUpdateMessageHelper(serverMessageAsJSON);
          break;
        case "end-level":
          System.out.println("~~~END OF LEVEL:~~~");
          endLevelMessageHelper(serverMessageAsJSON);
          break;
        case "end-game":
          System.out.println("~~~GAME OVER:~~~");
          endGameMessageHelper(serverMessageAsJSON);
          break;
      }
    } catch (JSONException jsonException) {
      System.out.println(serverMessage);
    }
    return false;
  }

  private void endGameMessageHelper(JSONObject serverMessageAsJSON) {
    try {
      JSONArray scores = serverMessageAsJSON.getJSONArray("scores");
      System.out.println("Here is a list of each player's scores: ");
      for (int i = 0; i < scores.length(); i++) {
        String name = scores.getJSONObject(i).getString("name");
        int exits = scores.getJSONObject(i).getInt("exits");
        int ejects = scores.getJSONObject(i).getInt("ejects");
        int keys = scores.getJSONObject(i).getInt("keys");
        System.out.println("Player: " + name);
        System.out.println("Number of times exited: " + exits);
        System.out.println("Number of times ejected: " + ejects);
        System.out.println("Number of keys found: " + keys);
        System.out.println("---------------");
      }
    }
    catch (JSONException jsonException) {
      jsonException.printStackTrace();
    }
  }

  private void endLevelMessageHelper(JSONObject serverMessageAsJSON) {
    try {
      String keyFinder = serverMessageAsJSON.getString("key");
      JSONArray exitedPlayers = serverMessageAsJSON.getJSONArray("exits");
      JSONArray ejectedPlayers = serverMessageAsJSON.getJSONArray("ejects");
      System.out.println(keyFinder + " found the key.");
      System.out.println("Here is a list of the players who were expelled by an adversary:\n" +
              ejectedPlayers.toString());
      System.out.println("Here is a list of the players who exited successfully:\n" +
              exitedPlayers.toString());
    }
    catch (JSONException jsonException) {
      jsonException.printStackTrace();
    }
  }

  private void playerUpdateMessageHelper(JSONObject serverMessageAsJSON) {
    try {
      JSONArray layout = serverMessageAsJSON.getJSONArray("layout");
      JSONArray objects = serverMessageAsJSON.getJSONArray("objects");
      JSONArray position = serverMessageAsJSON.getJSONArray("position");

      System.out.println("Your view layout: " + layout.toString());
      System.out.println("Objects near you: " + objects.toString());
      if (serverMessageAsJSON.isNull("actors")) {
        System.out.println("Actors near you: []");
      } else {
        JSONArray actors = serverMessageAsJSON.getJSONArray("actors");
        System.out.println("Actors near you: " + actors.toString());
      }
      System.out.println("Your Position: " + position.toString());
      if (serverMessageAsJSON.isNull("message")) {
        System.out.println("Message: " + JSONObject.NULL);
      } else {
        String message = serverMessageAsJSON.getString("message");
        System.out.println("Message: " + message);
      }
    }
    catch(JSONException jsonException ){
      jsonException.printStackTrace();
    }
  }

  private void startLevelMessageHelper(JSONObject serverMessageAsJSON) {
    try {
      int startLevelNum = serverMessageAsJSON.getInt("level");
      System.out.println("You are starting on level: " + startLevelNum);
      System.out.println("Here is a list of all the players in the game:");
      System.out.println(serverMessageAsJSON.getJSONArray("players"));
    }
    catch(JSONException jsonException ){
      jsonException.printStackTrace();
    }
  }

  private String parsePlayerMoveMessageAsJSON(String playerMove) throws JSONException {
    JSONObject playerMoveJSON = new JSONObject();
    playerMoveJSON.put("type", "move");
    if (playerMove.equals("null")) {
      try {
        playerMoveJSON.put("to", JSONObject.NULL);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    else {
      try {
        JSONArray move = new JSONArray(playerMove);
        playerMoveJSON.put("to", move);
      }
      catch (JSONException e) {
        System.out.print("Sorry, that was an invalid format. Please enter your move in this format: [<row>, <column>]");
        return "invalid";
      }
    }
    return playerMoveJSON.toString();
  }
}
