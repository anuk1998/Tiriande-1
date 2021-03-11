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

  /**
   * Takes the input from the scanner as a JSON Object and begins parsing it and converting it to
   * a Level representation, following our data structures.
   *
   * @param jsonArrayInput the scanner input
   * @throws JSONException thrown if the input is not valid JSON
   */
  private static void parseJSONAndConvertToLevel(JSONArray jsonArrayInput) throws JSONException {
    JSONObject stateObject = jsonArrayInput.getJSONObject(0);
    String name = jsonArrayInput.getString(1);
    JSONArray pointObject = jsonArrayInput.getJSONArray(2);

    JSONObject levelObject = stateObject.getJSONObject("level");
    JSONArray playersArray = stateObject.getJSONArray("players");
    JSONArray adversariesArray = stateObject.getJSONArray("adversaries");
    boolean exitLocked = stateObject.getBoolean("exit-locked");

    Level level = new Level();
    TestLevel.constructLevel(levelObject, level);
    if (!exitLocked) {
      level.openExitTile();
    }

    addPlayersToLevel(playersArray, level);
    addAdversariesToLevel(adversariesArray, level);
    System.out.println(level.renderLevel());

    // check if name exists in players
    if (checkIfPlayerExists(name, level)) {
      Player playerToBeMoved = getPlayerObjectFromName(name, level);
      checkMoveValidity(playerToBeMoved, pointObject, level, stateObject);
    }
    else {
      JSONArray outputArray = new JSONArray();
      outputPlayerDoesNotExistMessage(outputArray, name);
    }
  }

  private static void checkMoveValidity(Player player, JSONArray pointObject, Level level, JSONObject stateObject) throws JSONException {
    // create a GameManager instance to be able to conduct various actions based on the type of move
    ArrayList<Level> listOfOneLevel = new ArrayList<>();
    listOfOneLevel.add(level);
    GameManager gameManager = new GameManager(listOfOneLevel);
    // convert given point destination to our Position data representation for parsing
    Position point = new Position(pointObject.getInt(0), pointObject.getInt(1));
    // initialize the resulting output array that will be passed through functions below to be added to
    JSONArray outputArray = new JSONArray();

    if (level.isTileTraversable(point)) {
      String tile = level.getTileInLevel(point);
      switch (tile) {
        case "A":
          gameManager.parseMoveStatusAndDoAction(GameStatus.PLAYER_SELF_ELIMINATES, point, player);
          outputPlayerEjectedOrExitedMessage(outputArray, player, level, stateObject, "ejected");
          break;
        case "O":
          gameManager.parseMoveStatusAndDoAction(GameStatus.PLAYER_EXITED, point, player);
          outputPlayerEjectedOrExitedMessage(outputArray, player, level, stateObject, "exited");
          break;
        case "*":
          gameManager.parseMoveStatusAndDoAction(GameStatus.KEY_FOUND, point, player);
          outputRegularMoveMessage(outputArray, level, stateObject, false);
          break;
        default:
          gameManager.parseMoveStatusAndDoAction(GameStatus.VALID, point, player);
          outputRegularMoveMessage(outputArray, level, stateObject, true);
      }
    }
    else {
      outputInvalidMoveMessage(outputArray, pointObject);
    }
  }

  // TODO: see if this function and the one below can be abstracted -- lots of duplicate code
  private static Player getPlayerObjectFromName(String name, Level level) {
    for (Player p: level.getActivePlayers()) {
      if (p.getName().equals(name)) {
        return p;
      }
    }
    return null;
  }


  private static boolean checkIfPlayerExists(String name, Level level) {
    for (Player p: level.getActivePlayers()) {
      if (p.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Iterates through the list of player objects and adds each of them to the level and creates
   * them as Player objects.
   *
   * @param playersArray list of player JSON objects
   * @param level the level instance that will be added to
   * @throws JSONException thrown if there's malformed JSON
   */
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
