package Manager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import Common.IUser;
import Game.GameManager;
import Game.GameStatus;
import Game.IAdversary;
import Game.ICharacter;
import Game.Level;
import Game.Player;
import Game.Position;
import Level.TestLevel;
import User.LocalUser;

public class TestManager {
  private static JSONArray managerUpdates = new JSONArray();

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
      parseManagerJSONInput(jsonArrayInput);
    }
    catch (JSONException e) {
      System.out.println("Invalid input rendered: " + e);
    }
  }

  /**
   * Function that parses the given JSONArray from the Scanner and sends each parsed item to
   * corresponding functions for further action.
   *
   * @param jsonArrayInput the input JSONArray from the user
   */
  private static void parseManagerJSONInput(JSONArray jsonArrayInput) throws JSONException {
    JSONArray nameListArr = jsonArrayInput.getJSONArray(0);
    JSONObject levelObj = jsonArrayInput.getJSONObject(1);
    int turnLimit = jsonArrayInput.getInt(2);
    JSONArray pointListArr = jsonArrayInput.getJSONArray(3);
    JSONArray actorMoveListListArr = jsonArrayInput.getJSONArray(4);

    Level level = buildLevelFromJSONObject(levelObj);
    GameManager manager = new GameManager(new ArrayList<>(Arrays.asList(level)));

    registerCharactersFromJSON(manager, level, nameListArr, pointListArr);
    playGame(manager, level, turnLimit, nameListArr, actorMoveListListArr);
    makeStateOutput(manager, level, levelObj);
  }

  /**
   * Constructs the output State JSONObject and then forms the resulting JSONArray to be outputted.
   *
   * @param manager the GameManager instance
   * @param level the Level data object being played
   * @param levelObj the Level JSONObject given as input
   */
  private static void makeStateOutput(GameManager manager, Level level, JSONObject levelObj) throws JSONException {
    JSONArray outputArray = new JSONArray();

    JSONObject state = new JSONObject();
    state.put("type", "state");
    // Check if key has been found, if so, remove it from level object
    if (!level.getTileInLevel(level.getKeyPositionInLevel()).equals("*")) {
      JSONArray objects = levelObj.getJSONArray("objects");
      int keyIndex = 0;
      for (int i=0; i<objects.length(); i++) {
        if (objects.getJSONObject(i).get("type").equals("key")) {
          keyIndex = i;
        }
      }
      objects.remove(keyIndex);
    }
    state.put("level", levelObj);

    // add players list
    JSONArray newPlayersList = new JSONArray();
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
    state.put("players", newPlayersList);

    // add adversaries list
    JSONArray adversariesList = new JSONArray();
    for (IAdversary adversary : level.getAdversaries()) {
      JSONObject newAdversaryObj = new JSONObject();
      // create and add to the adversary object
      newAdversaryObj.put("type", adversary.getType());
      newAdversaryObj.put("name", adversary.getName());
      // make an inner JSON array and add to it to represent a position and place in new object
      JSONArray newPosition = new JSONArray();
      newPosition.put(adversary.getCharacterPosition().getRow());
      newPosition.put(adversary.getCharacterPosition().getCol());
      newAdversaryObj.put("position", newPosition);
      // add newly created adversary JSON object to final list of adversaries
      adversariesList.put(newAdversaryObj);
    }
    state.put("adversaries", adversariesList);

    // add the exit-locked field
    boolean isExitLocked = level.getTileInLevel(level.getExitPositionInLevel()).equals("●");
    state.put("exit-locked", isExitLocked);

    outputArray.put(state);
    outputArray.put(managerUpdates);

    System.out.println(outputArray.toString(2));
    System.out.println(level.renderLevel());
  }

  /**
   * Builds the given level JSONObject into our level representation via a previous testing harness.
   *
   * @param levelJSONObject the parsed Level object
   * @return the resulting Level representation
   * @throws JSONException when invalid JSON
   */
  private static Level buildLevelFromJSONObject(JSONObject levelJSONObject) throws JSONException {
    Level level = new Level();
    TestLevel.constructLevel(levelJSONObject, level);
    return level;
  }

  /**
   * Registers the given players into the game.
   *
   * @param manager the GameManager instance
   * @param level the Level object being played
   * @param nameListArr JSONArray of names of players to be registered
   * @param pointListArr JSONArray of points to register all characters at
   * @throws JSONException when invalid JSON is given
   */
  private static void registerCharactersFromJSON(GameManager manager, Level level, JSONArray nameListArr, JSONArray pointListArr) throws JSONException {
    ArrayList<Position> positionList = new ArrayList<>();
    // make a list of Positions from Point-List
    for (int i=0; i<pointListArr.length(); i++) {
      positionList.add(new Position(pointListArr.getJSONArray(i).getInt(0), pointListArr.getJSONArray(i).getInt(1)));
    }

    // register all given players via the GameManager
    for (int i=0; i<nameListArr.length(); i++) {
      String name = nameListArr.getString(i);
      manager.registerPlayer(name);
    }

    // moves each player (and potentially adversaries) to their corresponding start positions in the level
    int type = 1;
    for (int i=0; i< positionList.size(); i++) {
      if (i >= nameListArr.length()) {
        manager.registerAdversary("adv" + type, "zombie");
        level.moveCharacter(level.getAdversaryObjectFromName("adv" + type), positionList.get(i));
        type++;
        continue;
      }
      level.moveCharacter(level.getPlayerObjectFromName(nameListArr.getString(i)), positionList.get(i));
    }

    // TODO: remove this before submitting
    System.out.println(level.renderLevel());
  }

  /**
   * Starts the actual game of Snarl and goes through all the character moves until they run out, the game
   * turn limit is reached, or until the game is over.
   *
   * @param manager the GameManager instance
   * @param level the Level object being played
   * @param turnLimit the turn limit
   * @param nameListArr the list of all players
   * @param actorMoveListListArr the list of lists of actor moves to make during the game
   */
  private static void playGame(GameManager manager, Level level, int turnLimit, JSONArray nameListArr, JSONArray actorMoveListListArr) throws JSONException {
    ArrayList<ArrayList<Position>> listOfMovesForPlayers = convertFromJSONArrayToListOfMoves(actorMoveListListArr);
    boolean gameStillGoing = true;
    int characterIndex = 0;
    int turn = 1;
    while (turn <= turnLimit && gameStillGoing) {
      String name = nameListArr.getString(characterIndex);
      ICharacter player = level.getPlayerObjectFromName(name);
      ArrayList<Position> playerListOfMoves = listOfMovesForPlayers.get(characterIndex);

      if (player != null) {
        makeUpdates(nameListArr, level, manager);

        // TODO: ABSTRACT TO DIFFERENT METHOD
        try {
          Position newMove = playerListOfMoves.get(0);
          GameStatus result;
          if (newMove == null) {
            result = GameStatus.VALID;
            makeMoveUpdate(player, null, result);
            playerListOfMoves.remove(null);
          }
          else {
            result = manager.callRuleChecker(player, newMove);
            makeMoveUpdate(player, newMove, result);
            playerListOfMoves.remove(newMove);
            while (result == GameStatus.INVALID) {
              newMove = playerListOfMoves.get(0);
              result = manager.callRuleChecker(player, newMove);
              makeMoveUpdate(player, newMove, result);
              playerListOfMoves.remove(newMove);
            }
            gameStillGoing = manager.parseMoveStatusAndDoAction(result, newMove, player);
          }
        }
        catch (IndexOutOfBoundsException e) {
          break;
        }
      }

      if (characterIndex == nameListArr.length() - 1) {
        characterIndex = 0;
        turn++;
      }
      else {
        characterIndex++;
      }
    }
  }

  /**
   * Takes the given JSONArray and converts it to a regular Java list of Positions.
   *
   * @param actorMoveListListArr a JSONArray of JSONArrays
   * @return an ArrayList of Positions
   */
  private static ArrayList<ArrayList<Position>> convertFromJSONArrayToListOfMoves(JSONArray actorMoveListListArr) throws JSONException {
    ArrayList<ArrayList<Position>> convertedList = new ArrayList<>();

    for (int i=0; i<actorMoveListListArr.length(); i++) {
      JSONArray innerMoveList = actorMoveListListArr.getJSONArray(i);
      ArrayList<Position> playerListOfMoves = new ArrayList<>();
      for (int j=0; j< innerMoveList.length(); j++) {
        JSONObject moveObj = innerMoveList.getJSONObject(j);
        if (moveObj.isNull("to")) {
          playerListOfMoves.add(null);
        }
        else {
          JSONArray move = moveObj.getJSONArray("to");
          playerListOfMoves.add(new Position(move.getInt(0), move.getInt(1)));
        }
      }
      convertedList.add(playerListOfMoves);
    }

    return convertedList;
  }

  /**
   * Makes an update for each active player in the game.
   *
   * @param nameListArr list of players in the game
   * @param level the Level oject being played
   * @param manager the GameManager instance
   */
  private static void makeUpdates(JSONArray nameListArr, Level level, GameManager manager) throws JSONException {
    for (int i=0; i< nameListArr.length(); i++) {
      String name = nameListArr.getString(i);
      Player player = level.getPlayerObjectFromName(name);
      IUser user = manager.getUserFromName(name);

      if (player != null) {
        JSONArray playerUpdate = new JSONArray();
        playerUpdate.put(name);
        playerUpdate.put(formPlayerUpdate(manager, level, player, user));

        managerUpdates.put(playerUpdate);
      }
    }
  }

  /**
   * Forms the JSONObject that goes into the second field of a manager trace entry.
   *
   * @param manager the GameManager instance
   * @param level the Level object being played
   * @param player the player whose update we're making
   * @param user the current player's corresponding user object
   * @return a JSONObject of a player update
   */
  private static JSONObject formPlayerUpdate(GameManager manager, Level level, ICharacter player, IUser user) throws JSONException {
    JSONObject playerUpdate = new JSONObject();
    // add the type field
    playerUpdate.put("type", "player-update");

    // add the layout field
    LocalUser localUser = (LocalUser) user;
    String[][] layout = localUser.makeView(level, player);
    JSONArray layoutArray = convertToLayoutArray(layout);
    playerUpdate.put("layout", layoutArray);

    // add the absolute position field
    JSONArray absolutePos = new JSONArray();
    absolutePos.put(player.getCharacterPosition().getRow());
    absolutePos.put(player.getCharacterPosition().getCol());
    playerUpdate.put("position", absolutePos);

    // add objects field
    JSONArray objectsList = detectObjectsInView(layout, level);
    playerUpdate.put("objects", objectsList);

    // add actors field
    JSONArray actorsList = detectActorsInView(layout, level, manager);
    playerUpdate.put("actors", actorsList);

    return playerUpdate;
  }

  /**
   * Converts the given String[][] into a JSONArray representation of the layout.
   *
   * @param layout the ASCII representation of the player's view
   * @return a JSONArray version of the view
   */
  private static JSONArray convertToLayoutArray(String[][] layout) {
    JSONArray layoutArray = new JSONArray();
    for (String[] row : layout) {
      JSONArray rowArray = new JSONArray();
      for (String colTile : row) {
        if (colTile.equals("|")) {
          rowArray.put(2);
        }
        else if (colTile.equals("■") || colTile.equals(" ")){
          rowArray.put(0);
        }
        else {
          rowArray.put(1);
        }
      }
      layoutArray.put(rowArray);
    }
    return layoutArray;
  }

  /**
   * Determines if there are objects in any position in the given layout.
   *
   * @param layout String[][] of the player's view
   * @param level the current Level being played
   * @return a JSONArray representing the objects in view
   */
  private static JSONArray detectObjectsInView(String[][] layout, Level level) throws JSONException {
    JSONArray objectsArray = new JSONArray();
    for (String[] row : layout) {
      for (String colTile : row) {
        if (colTile.equals("●") || colTile.equals("O")) {
          JSONObject objectObj = new JSONObject();
          objectObj.put("type", "exit");

          JSONArray objectPos = new JSONArray();
          objectPos.put(level.getExitPositionInLevel().getRow());
          objectPos.put(level.getExitPositionInLevel().getCol());
          objectObj.put("position", objectPos);
          objectsArray.put(objectObj);
        }
        else if (colTile.equals("*")) {
          JSONObject objectObj = new JSONObject();
          objectObj.put("type", "key");

          JSONArray objectPos = new JSONArray();
          objectPos.put(level.getKeyPositionInLevel().getRow());
          objectPos.put(level.getKeyPositionInLevel().getCol());
          objectObj.put("position", objectPos);
          objectsArray.put(objectObj);
        }
      }
    }
    return objectsArray;
  }

  /**
   * Determines if there are actors in any position in the given layout.
   *
   * @param layout String[][] of the player's view
   * @param level the current Level being played
   * @param manager the GameManager instance
   * @return a JSONArray representing the actors in view
   */
  private static JSONArray detectActorsInView(String[][] layout, Level level, GameManager manager) throws JSONException {
    JSONArray actorsArray = new JSONArray();
    for (String[] row : layout) {
      for (String colTile : row) {
        if (colTile.equals("Z") || colTile.equals("G")) {
          //TODO: FIND A WAY TO GET AN ADVERSARY OBJECT FROM THE ASCII AVATAR
          /*
          IAdversary adv = level.
          JSONObject actorObj = new JSONObject();
          actorObj.put("type", "key");

          JSONArray actorPos = new JSONArray();
          actorPos.put(.getRow());
          actorPos.put(level.getKeyPositionInLevel().getCol());
          actorObj.put("position", objectPos);
           */
        }
        else if (colTile.equals("@") || colTile.equals("¤") || colTile.equals("$") || colTile.equals("~")) {
          JSONObject actorObj = new JSONObject();
          Player player = manager.getPlayerFromAvatar(colTile);
          actorObj.put("name", player.getName());
          actorObj.put("type", "player");

          JSONArray actorPos = new JSONArray();
          actorPos.put(player.getCharacterPosition().getRow());
          actorPos.put(player.getCharacterPosition().getCol());
          actorObj.put("position", actorPos);
        }
      }
    }
    return actorsArray;
  }

  /**
   * Constructs a move Manager update to be added to the manager trace.
   *
   * @param player player object whose move it is
   * @param newMove the player's requested move
   */
  private static void makeMoveUpdate(ICharacter player, Position newMove, GameStatus result) throws JSONException {
    JSONArray moveUpdate = new JSONArray();
    // add name field
    moveUpdate.put(player.getName());

    // add move object
    JSONObject moveObj = new JSONObject();
    moveObj.put("type", "move");
    if (newMove == null) {
      moveObj.put("to", JSONObject.NULL);
    }
    else {
      JSONArray toArray = new JSONArray();
      toArray.put(newMove.getRow());
      toArray.put(newMove.getCol());
      moveObj.put("to", toArray);
    }
    moveUpdate.put(moveObj);

    // add result field
    moveUpdate.put(parseGameStatus(result));

    // add to manager trace
    managerUpdates.put(moveUpdate);
  }

  /**
   * Parses the given GameStatus type into ASCII String.
   *
   * @param result given move status
   * @return a String representation of the given enum
   */
  private static String parseGameStatus(GameStatus result) {
    switch (result) {
      case VALID:
        return "OK";
      case KEY_FOUND:
        return "Key";
      case PLAYER_EXITED:
      case LEVEL_WON:
      case GAME_WON:
        return "Exit";
      case PLAYER_SELF_ELIMINATES:
      case PLAYER_EXPELLED:
      case GAME_LOST:
        return "Eject";
      case INVALID:
        return "Invalid";
      default:
        return "Should never get here";
    }
  }
}
