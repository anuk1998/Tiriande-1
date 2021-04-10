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
  String host = "localhost";
  int port = 45678;

  public void main(String[] args) {
    ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
    parseCommandLine(argsList);
    Client client = new Client();
    client.run();
  }

  private void parseCommandLine(ArrayList<String> argsList) {
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
      BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
      Scanner sc = new Scanner(System.in);

      while (true) {
        String serverMessage = input.readLine();
        boolean isResponseNeeded = parseServerMessage(serverMessage);
        if (isResponseNeeded) {
          String reply = sc.nextLine();
          output.println(reply);
          output.flush();
        }
      }

    } catch (Exception var3) {
      var3.printStackTrace();
    }
  }

  private boolean parseServerMessage(String serverMessage) {
    switch (serverMessage) {
      case "name":
        System.out.println("Please enter your name for the game:");
        return true;
      case "move":
        System.out.println("Please supply a position for your next move. Enter with this exact format: [row, column].");
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
          int startLevelNum = Integer.parseInt(serverMessageAsJSON.getString("level"));
          System.out.println("You are starting on level: " + startLevelNum);
          System.out.println("Here is a list of all the players in the game:");
          System.out.println(serverMessageAsJSON.getJSONArray("players"));
          break;
        case "player-update":
          JSONArray layout = serverMessageAsJSON.getJSONArray("layout");
          JSONArray objects = serverMessageAsJSON.getJSONArray("objects");
          JSONArray actors = serverMessageAsJSON.getJSONArray("actors");
          JSONArray position = serverMessageAsJSON.getJSONArray("point");
          String message = serverMessageAsJSON.getString("point");
          System.out.println("Update on the game:");
          System.out.println("Layout: " + layout);
          System.out.println("Objects: " + objects);
          System.out.println("Actors: " + actors);
          System.out.println("Your Position: " + position);
          System.out.println("Message: " + message);
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
}
