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
  private static void constructLevel(JSONObject levelObject, Level level) throws JSONException {
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
    JSONArray origin = roomToMake.getJSONArray("origin");
    JSONObject bounds = roomToMake.getJSONObject("bounds");
    int rows = bounds.getInt("rows");
    int columns = bounds.getInt("columns");
    JSONArray layout = roomToMake.getJSONArray("layout");

    int originRow = origin.getInt(0);
    int originCol = origin.getInt(1);

    Position extractedOrigin = new Position(originRow, originCol);
    Room roomObj = new Room(extractedOrigin, rows, columns);
    roomObj.createRoomFromJSON(layout);
    level.addRoom(roomObj);
  }

  // builds each hallway JSON object into our Hallway representation
  private static void constructHallway(JSONObject hallwayToMake, Level level) throws JSONException {
    JSONArray from = hallwayToMake.getJSONArray("from");
    JSONArray to = hallwayToMake.getJSONArray("to");
    JSONArray waypoints = hallwayToMake.getJSONArray("waypoints");

    Position fromPos = new Position(from.getInt(0), from.getInt(1));
    Position toPos = new Position(to.getInt(0), to.getInt(1));
    Hallway newHallway = new Hallway(fromPos, toPos);

    for (int i=0; i<waypoints.length(); i++) {
      int waypointRow = waypoints.getJSONArray(i).getInt(0);
      int waypointCol = waypoints.getJSONArray(i).getInt(1);
      Position waypoint = new Position(waypointRow, waypointCol);
      newHallway.addAWaypoint(waypoint);
    }
    newHallway.connectHallwayWaypoints();
    level.addHallway(newHallway);
  }

  // builds the JSON output for the program
  private static void constructOutput(JSONObject output, Position point, Level level) throws JSONException {
    output.put("traversable", level.isTileTraversable(point));

    String object = "";
    String tile = level.getTileInLevel(point);
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
    output.put("object", object);

    String type = "";
    switch (tile) {
      case "X":
        type = "hallway";
        break;
      case "|":
      case "■":
        type = "room";
        break;
      default:
        type = "void";
    }
    output.put("type", type);

    JSONArray reachableArray = new JSONArray();
    if (type.equals("hallway")) {
      Hallway hallway = level.getHallwayFromPoint(point);
      ArrayList<Room> attachedRooms = level.startAndEndRooms(hallway);
      for (Room r : attachedRooms) {
        JSONArray innerArray = new JSONArray();
        innerArray.put(r.getRoomOriginInLevel().getRow());
        innerArray.put(r.getRoomOriginInLevel().getCol());
        reachableArray.put(innerArray);
      }
    }
    else if (type.equals("room")) {
      Room room = level.getBelongingRoom(point);
      ArrayList<Hallway> connectedHallways = level.getConnectedHallways(room);
      for (Hallway h : connectedHallways) {
        JSONArray innerArray = new JSONArray();
        Room start = level.startAndEndRooms(h).get(0);
        Room end = level.startAndEndRooms(h).get(1);
        if (start.getRoomOriginInLevel().equals(room.getRoomOriginInLevel())) {
          innerArray.put(end.getRoomOriginInLevel().getRow());
          innerArray.put(end.getRoomOriginInLevel().getCol());
        } else {
          innerArray.put(start.getRoomOriginInLevel().getRow());
          innerArray.put(start.getRoomOriginInLevel().getCol());
        }
        reachableArray.put(innerArray);
      }
    }
    output.put("reachable", reachableArray);
    System.out.println(output);
  }

}

/*
{
  "rooms":
        [
                { "type" : "room",
                  "origin" : [0, 1],
                  "bounds" : { "rows" : 3,
                               "columns" : 5 },
                  "layout" : [ [0, 0, 2, 0, 0],
                               [0, 1, 1, 1, 0],
                               [0, 0, 2, 0, 0] ]
                },
                { "type" : "room",
                  "origin" : [0, 1],
                  "bounds" : { "rows" : 3,
                               "columns" : 5 },
                  "layout" : [ [0, 0, 2, 0, 0],
                               [0, 1, 1, 1, 0],
                               [0, 0, 2, 0, 0]]
                }
        ],
        "hallways": [
                { "from": (point), "to": (point),"waypoints": (point-list)},
                { "from": (point), "to": (point),"waypoints": (point-list)}
                    ],
        "objects": [ { "type": "key", "position": (point) },
                     { "type": "exit", "position": (point) } ]
        }
 */

  /*
{
  "traversable": (either true or false),
  "object": (either "key", "exit", or null),
  "type": (either "room", "hallway", or "void"),
  "reachable": [[row, col], [row,col], ...] or []
}
 */
