package State;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

import Game.GameManager;
import Game.GameStatus;
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

    while (scanner.hasNextLine()) {
      text = scanner.nextLine();
      input_as_string.append(text);
    }
    scanner.close();

    try {
      JSONArray jsonArrayInput = new JSONArray(input_as_string.toString());
      parseJSONAndConvertToLevel(jsonArrayInput);
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

  /***
   * Iterates through the list of adversary objects and adds each of them to the level and creates
   * them either as Zombie or Ghost objects, depending on their given type.
   *
   * @param adversariesArray list of adversary JSON objects
   * @param level the level instance that will be added to
   * @throws JSONException thrown if there's malformed JSON
   */
  private static void addAdversariesToLevel(JSONArray adversariesArray, Level level) throws JSONException {
    for (int i=0; i<adversariesArray.length(); i++) {
      JSONObject adversaryObj = adversariesArray.getJSONObject(i);
      String type = adversaryObj.getString("type");
      String adversaryName = adversaryObj.getString("name");
      JSONArray adversaryPosJSON = adversaryObj.getJSONArray("position");
      Position adversaryPos = new Position(adversaryPosJSON.getInt(0), adversaryPosJSON.getInt(1));
      if (type.equals("zombie")) {
        IAdversary newZombie = new Zombie(adversaryName);
        level.addAdversary(newZombie, adversaryPos);
      }
      else if (type.equals("ghost")) {
        IAdversary newGhost = new Ghost(adversaryName);
        level.addAdversary(newGhost, adversaryPos);
      }
    }
  }

  private static void outputPlayerDoesNotExistMessage(JSONArray outputArray, String name) {
    outputArray.put("Failure");
    outputArray.put("Player");
    outputArray.put(name);
    outputArray.put("is not a part of the game.");
    System.out.println(outputArray);
  }

  private static void outputInvalidMoveMessage(JSONArray outputArray, JSONArray pointObj) {
    outputArray.put("Failure");
    outputArray.put("The destination position ");
    outputArray.put(pointObj);
    outputArray.put(" is invalid.");
    System.out.println(outputArray);
  }

  private static void outputPlayerEjectedOrExitedMessage(JSONArray outputArray, Player player, Level level, JSONObject stateObject, String status) throws JSONException {
    boolean isLocked = true;
    String reason = status.equals("ejected") ? " was ejected." : " exited.";
    if (status.equals("exited")) {
      isLocked = false;
    }
    JSONObject updatedState = updateStateObject(level, stateObject, isLocked);
    outputArray.put("Success");
    outputArray.put("Player");
    outputArray.put(player.getName());

    outputArray.put(reason);
    outputArray.put(updatedState);
    System.out.println(outputArray);
  }

  private static void outputRegularMoveMessage(JSONArray outputArray, Level level, JSONObject stateObject, boolean isExitLocked) throws JSONException {
    JSONObject updatedState = updateStateObject(level, stateObject, isExitLocked);
    outputArray.put("Success");
    outputArray.put(updatedState);
    System.out.println(outputArray);
  }

  private static JSONObject updateStateObject(Level level, JSONObject stateObject, boolean isExitLocked) throws JSONException {
    JSONArray newPlayersList = new JSONArray();
    // make a new list of players based on the list of active players in Level
    for (Player activePlayer : level.getActivePlayers()) {
      JSONObject newPlayerObj = new JSONObject();
      // create and add to the player object
      newPlayerObj.put("type", "player");
      newPlayerObj.put("name", activePlayer.getName());
      // make an inner JSON array and add to it to represent a position and place in new object
      JSONArray newPosition = new JSONArray();
      newPosition.put(activePlayer.getCharacterPosition().getRow());
      newPosition.put(activePlayer.getCharacterPosition().getCol());
      newPlayerObj.put("position", newPosition);
      // add newly created player JSON object to final list of players
      newPlayersList.put(newPlayerObj);
    }
    stateObject.put("players", newPlayersList);
    stateObject.put("exit-locked", isExitLocked);

    return stateObject;
  }

}
