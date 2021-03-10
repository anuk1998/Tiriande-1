package State;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

import Game.Level;
import Game.Position;
import Game.IAdversary;
import Game.Ghost;
import Game.Zombie;
import Game.Player;
import Level.TestLevel;

public class TestState {
  public static void main(String[] args) throws JSONException {
    StringBuilder input_as_string = new StringBuilder();
    String text;
    Scanner scanner = new Scanner(System.in);
    JSONObject output = new JSONObject();

    while (scanner.hasNextLine()) {
      text = scanner.nextLine();
      input_as_string.append(text);
    }
    scanner.close();

    try {
      JSONArray jsonArrayInput = new JSONArray(input_as_string.toString());
      JSONObject stateObject = jsonArrayInput.getJSONObject(0);
      String name = jsonArrayInput.getString(1);
      JSONArray pointObject = jsonArrayInput.getJSONArray(2);
      Position point = new Position(pointObject.getInt(0), pointObject.getInt(1));

      JSONObject levelObject = stateObject.getJSONObject("level");
      JSONArray playersArray = stateObject.getJSONArray("players");
      JSONArray adversariesArray = stateObject.getJSONArray("adversaries");

      Level level = new Level();
      TestLevel.constructLevel(levelObject, level);
      System.out.println(level.renderLevel());

      addPlayersToLevel(playersArray, level);
      addAdversariesToLevel(adversariesArray, level);
      //constructOutput(output, point, level);
    }
    catch (JSONException e) {
      System.out.println("Invalid input rendered: []");
    }
  }

  private static void addPlayersToLevel(JSONArray playersArray, Level level) throws JSONException {
    for (int i=0; i<playersArray.length(); i++) {
      JSONObject playerObj = playersArray.getJSONObject(i);
      String playerName = playerObj.getString("name");
      JSONArray playerPosJSON = playerObj.getJSONArray("position");
      Position playerPos = new Position(playerPosJSON.getInt(0), playerPosJSON.getInt(1));
      Player newPlayer = new Player(playerName);
      level.addPlayer(newPlayer, playerPos);
    }
  }

  private static void addAdversariesToLevel(JSONArray adversariesArray, Level level) {

  }

  /*
  {
  "type": (actor-type),
  "name": (string),
  "position": (point)
}
   */




}
