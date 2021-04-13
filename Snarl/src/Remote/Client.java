package Remote;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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
          //output.flush();
        }
      }

    }
    catch (Exception var3) {
      var3.printStackTrace();
    }
  }

  private boolean parseServerMessage(String serverMessage) throws IOException {
    switch (serverMessage) {
      case "name":
        System.out.println("Please enter your name for the game:");
        return true;
      case "observe":
        String str;
        while ((str = input.readLine()) != null && str.length() != 0) {
          System.out.println(str);
        }
        return false;
      case "move":
        System.out.println("~~~YOUR MOVE:~~~");
        System.out.println("Please supply a position for your next move. Enter with this exact format: [<row>, <column>]. If you don't want to move your position, type 'null'.");
        moveTime = true;
        return true;
      case "OK":
        System.out.println("The requested move was valid.");
        return false;
      case "Key":
        System.out.println("You found the key!");
        return false;
      case "Exit":
        System.out.println("You have successfully passed through the exit!");
        return false;
      case "Eject":
        System.out.println("You have been ejected :(");
        return false;
      case "Invalid":
        System.out.println("Sorry, that is an invalid move. Try another one.");
        invalid = true;
        return true;
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
          System.out.println(type + " to Snarl! Here is information about the game: \n" + info);
          break;
        case "start-level":
          int startLevelNum = serverMessageAsJSON.getInt("level");
          System.out.println("You are starting on level: " + startLevelNum);
          System.out.println("Here is a list of all the players in the game:");
          System.out.println(serverMessageAsJSON.getJSONArray("players"));
          break;
        case "player-update":
          System.out.println("~~~GAME UPDATE:~~~");
          JSONArray layout = serverMessageAsJSON.getJSONArray("layout");
          JSONArray objects = serverMessageAsJSON.getJSONArray("objects");
          JSONArray actors = serverMessageAsJSON.getJSONArray("actors");
          JSONArray position = serverMessageAsJSON.getJSONArray("position");
          System.out.println("Your view layout: " + layout.toString());
          System.out.println("Objects near you: " + objects.toString());
          System.out.println("Actors near you: " + actors.toString());
          System.out.println("Your Position: " + position.toString());
          if (serverMessageAsJSON.isNull("message")) {
            System.out.println("Message: " + JSONObject.NULL);
          }
          else {
            String message = serverMessageAsJSON.getString("message");
            System.out.println("Message: " + message);
          }

          break;
        case "end-level":
          String keyFinder = serverMessageAsJSON.getString("key");
          JSONArray exitedPlayers = serverMessageAsJSON.getJSONArray("exits");
          JSONArray ejectedPlayers = serverMessageAsJSON.getJSONArray("ejects");
          System.out.println(keyFinder + " found the key.");
          System.out.println("Here is a list of the players who were expelled by an adversary:\n" +
                  ejectedPlayers);
          System.out.println("Here is a list of the players who exited successfully:\n" +
                  exitedPlayers);
          break;
        case "end-game":
          JSONArray scores = serverMessageAsJSON.getJSONArray("scores");
          System.out.println("Here is a list of each player's scores: ");
          for (int i = 0; i < scores.length(); i++) {
            System.out.println(scores.getJSONObject(i).toString());
          }
          break;
      }
    } catch (JSONException jsonException) {
      jsonException.printStackTrace();
    }
    return false;
  }

  private String parsePlayerMoveMessageAsJSON(String playerMove) throws JSONException {
    JSONObject playerMoveJSON = new JSONObject();
    playerMoveJSON.put("type", "move");
    if (playerMove.equals("null")) {
      try {
        playerMoveJSON.put("to", "null");
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