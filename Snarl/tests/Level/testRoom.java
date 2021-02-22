package Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

import Game.Level;
import Game.Position;
import Game.Room;


public class testRoom {

    public static void main(String[] args) throws JSONException {
        StringBuilder input_as_string = new StringBuilder();
        String text;
        Scanner scanner = new Scanner(System.in);
        JSONArray output = new JSONArray();

        while (scanner.hasNextLine()) {
            text = scanner.nextLine();
            input_as_string.append(text);
        }
        scanner.close();

        JSONArray jsonArrayInput = new JSONArray(input_as_string.toString());
        JSONObject roomObject = jsonArrayInput.getJSONObject(0);
        JSONArray pointObject = jsonArrayInput.getJSONArray(1);

        String type = roomObject.getString("type");
        if (type.equals("room")) {
            output = typeIsRoom(roomObject, pointObject);
        }

        System.out.println(output);

    }

    public static JSONArray typeIsRoom(JSONObject roomToMake, JSONArray JSONpoint) throws JSONException {
        Level level = new Level();

        Position point = new Position(JSONpoint.getInt(0), JSONpoint.getInt(1));
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

        ArrayList<Position> adjacentTiles = level.getAllAdjacentTiles(point, roomObj);

        JSONArray outputArray = new JSONArray();
        if (adjacentTiles == null) {
            outputArray.put("Failure: Point ");
            outputArray.put(JSONpoint);
            outputArray.put(" is not in room at ");
            outputArray.put(origin);
        }
        else {
            JSONArray innerArray = new JSONArray();
            for (Position tile : adjacentTiles) {
                JSONArray tempArray = new JSONArray();
                tempArray.put(tile.getRow());
                tempArray.put(tile.getCol());
                innerArray.put(tempArray);
            }
            outputArray.put("Success: Traversable points from ");
            outputArray.put(JSONpoint);
            outputArray.put(" in room at ");
            outputArray.put(origin);
            outputArray.put(" are ");
            outputArray.put(innerArray);
        }
        return outputArray;
    }

}
