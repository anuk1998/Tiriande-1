package Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

import Game.Hallway;
import Game.Level;
import Game.Position;
import Game.Room;

public class TestLevel {
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
      JSONObject levelObject = jsonArrayInput.getJSONObject(0);
      JSONArray pointObject = jsonArrayInput.getJSONArray(1);
      Position point = new Position(pointObject.getInt(0), pointObject.getInt(1));

      Level level = new Level();
      constructLevel(levelObject, level);
      constructOutput(output, point, level);
    }
    catch (JSONException e) {
      System.out.println("Invalid input rendered: []");
    }
  }

  // builds the level image based on the given JSON input
  public static void constructLevel(JSONObject levelObject, Level level) throws JSONException {
    // iterates through the JSONArray of rooms and sends each one to constructRoom method to be translated and added
    JSONArray rooms = levelObject.getJSONArray("rooms");
    for (int i=0; i<rooms.length(); i++) {
      constructRoom(rooms.getJSONObject(i), level);
    }

    // iterates through the JSONArray of hallways and sends each one to constructHallway to be translated and added
    JSONArray hallways = levelObject.getJSONArray("hallways");
    for (int i=0; i<hallways.length(); i++) {
      constructHallway(hallways.getJSONObject(i), level);
    }
    // places the key and exit at their respective given positions
    JSONArray objects = levelObject.getJSONArray("objects");
    JSONArray keyPositionObject = objects.getJSONObject(0).getJSONArray("position");
    Position keyPosition = new Position(keyPositionObject.getInt(0), keyPositionObject.getInt(1));
    level.addKey(keyPosition);

    JSONArray exitPositionObject = objects.getJSONObject(1).getJSONArray("position");
    Position exitPosition = new Position(exitPositionObject.getInt(0), exitPositionObject.getInt(1));
    level.addExit(exitPosition);
  }

  // builds each room JSON object into our Room representation
  public static void constructRoom(JSONObject roomToMake, Level level) throws JSONException {
    // parses JSON and extracts the necessary values from the object
    JSONArray origin = roomToMake.getJSONArray("origin");
    JSONObject bounds = roomToMake.getJSONObject("bounds");
    int rows = bounds.getInt("rows");
    int columns = bounds.getInt("columns");
    JSONArray layout = roomToMake.getJSONArray("layout");

    int originRow = origin.getInt(0);
    int originCol = origin.getInt(1);
    Position extractedOrigin = new Position(originRow, originCol);

    // makes a Room object of our data representation type from the extracted values and sends the room's
    //   board to be instantiated in the Room class and then adds the room to the level plane
    Room roomObj = new Room(extractedOrigin, rows, columns);
    roomObj.createRoomFromJSON(layout);
    level.addRoom(roomObj);
  }

  // builds each hallway JSON object into our Hallway representation
  private static void constructHallway(JSONObject hallwayToMake, Level level) throws JSONException {
    // parses the JSON object and extracts the necessary values
    JSONArray from = hallwayToMake.getJSONArray("from");
    JSONArray to = hallwayToMake.getJSONArray("to");
    JSONArray waypoints = hallwayToMake.getJSONArray("waypoints");

    Position fromPos = new Position(from.getInt(0), from.getInt(1));
    Position toPos = new Position(to.getInt(0), to.getInt(1));

    // makes a Hallway object of our data representation
    Hallway newHallway = new Hallway(fromPos, toPos);

    // parses through all the listed waypoints and adds them to the hallway's list of waypoints
    for (int i=0; i<waypoints.length(); i++) {
      int waypointRow = waypoints.getJSONArray(i).getInt(0);
      int waypointCol = waypoints.getJSONArray(i).getInt(1);
      Position waypoint = new Position(waypointRow, waypointCol);
      newHallway.addAWaypoint(waypoint);
    }
    // sends hallway to be fully connected/constructed on the board and added to the level plane
    newHallway.connectHallwayWaypoints();
    level.addHallway(newHallway);
  }

  // builds the JSON output for the program
  private static void constructOutput(JSONObject output, Position point, Level level) throws JSONException {
    boolean isTraversable = level.isTileTraversable(point);
    output.put("traversable", isTraversable); // adds first field

    String tile = level.getTileInLevel(point);

    putObjectField(tile, output); // adds second field
    String type = putTypeField(tile, output); // adds third field
    putReachableField(type, point, level, output); // adds fourth and final field of output

    System.out.println(output);
  }

  private static void putReachableField(String type, Position point, Level level, JSONObject output) throws JSONException {
    JSONArray reachableArray = new JSONArray();
    switch (type) {
      case "hallway":
        Hallway hallway = level.getHallwayFromPoint(point);
        ArrayList<Room> attachedRooms = level.startAndEndRooms(hallway);
        for (Room r : attachedRooms) {
          JSONArray innerArray = new JSONArray();
          innerArray.put(r.getRoomOriginInLevel().getRow());
          innerArray.put(r.getRoomOriginInLevel().getCol());
          reachableArray.put(innerArray);
        }
        break;
      case "room":
        Room room = level.getBelongingRoom(point);
        ArrayList<Hallway> connectedHallways = level.getConnectedHallways(room);
        for (Hallway h : connectedHallways) {
          JSONArray innerArray = new JSONArray();
          Room start = level.startAndEndRooms(h).get(0);
          Room end = level.startAndEndRooms(h).get(1);
          // only add the other room to the reachable list, make sure a room isn't added to its own reachable list
          //   (unless a hallway connects it to itself)
          if (start.getRoomOriginInLevel().equals(room.getRoomOriginInLevel())) {
            innerArray.put(end.getRoomOriginInLevel().getRow());
            innerArray.put(end.getRoomOriginInLevel().getCol());
          } else {
            innerArray.put(start.getRoomOriginInLevel().getRow());
            innerArray.put(start.getRoomOriginInLevel().getCol());
          }
          reachableArray.put(innerArray);
        }
        break;
    }
    output.put("reachable", reachableArray);
  }

  private static String putTypeField(String tile, JSONObject output) throws JSONException {
    String type = "";
    switch (tile) {
      case "X":
        type = "hallway";
        break;
      case "|":
      case "*":
      case "●":
      case "O":
      case "#":
      case "■":
        type = "room";
        break;
      default:
        type = "void";
    }
    output.put("type", type);
    return type;
  }

  private static void putObjectField(String tile, JSONObject output) throws JSONException {
    String object = "";
    switch (tile) {
      case "*":
        object = "key";
        break;
      case "●":
      case "O":
        object = "exit";
        break;
      default:
        object = null;
    }

    if (object == null) {
      output.put("object", JSONObject.NULL);
    }
    else {
      output.put("object", object);
    }
  }

}
