/*
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.util.Scanner;
import static java.lang.System.exit;

public class travellerClient {
    private TownNetwork townNetwork = new TownNetwork(new ArrayList<Edge>());

    public static void main(String[] args) throws ParseException, org.json.JSONException {

        StringBuilder input_as_string = new StringBuilder();
        String text;
        Scanner scanner = new Scanner(System.in);

        while( scanner.hasNextLine() )
        {
            text = scanner.nextLine();
            input_as_string.append( text );
        }
        scanner.close();

        parseInput(input_as_string);


    }

    private static void parseInput(StringBuilder stdnInput) throws JSONException {
        String input = stdnInput.toString().replaceAll("[\\n\\t\\s]", "");
        String[] parsedObjects = input.replaceAll("}\\{", "}#{").split("#");
        //{"command": "roads"
        if (!(parsedObjects[0].substring(13,18).equals("roads"))) {
            exit(0);
        }

        for (String object : parsedObjects) {
            parseJson(object);
        }

    }

    private static void parseJson(String json_object) throws org.json.JSONException {

        JSONObject object = new JSONObject(json_object);

        try {
            JSONObject command = object.getJSONObject("command");
            JSONObject params = object.getJSONObject("params");

            if(command.toString().equals("roads")) {
                // assuming "roads" is valid and is the first JSON object given.. **check for that**
                roads(params);

            }
            else if(command.toString().equals("place")) {
                //define object to be something else, change it
                place(params);
            }
            else if (command.toString().equals("passage-safe?")){
                //define object to be something else, change it
                passageSafe(params);
            }
        }
        catch (Exception e) {
            exit(0);
        }

    }

    public static void roads(JSONObject params) throws JSONException {
        JSONArray roadsParam = new JSONArray(params);
        for (int i=0; i<roadsParam.length(); i++) {
            JSONObject road = new JSONObject(roadsParam.getJSONObject(i));
            Edge newRoad = new Edge(new Town(road.getString("from"), true, null),
                    new Town(road.getString("to"), true, null));
            townNetwork.addEdge(newRoad);
        }
    }

    public static void place(JSONObject params) throws JSONException {
        //JSONObject placeParam = params.getJSONObject("place");
        String character = params.getString("character");
        String town = params.getString("town");

        for (Edge e : townNetwork.getEdges()) {
            if (e.getSource().getName().equals(town)) {
                Character newCharacter = new Character(character, true);
                Town tObject = e.getSource();
                tObject.setCharacter(newCharacter); //figure out how to access town string Object
                townNetwork.addCharactersAndTown(character, tObject);

            }
            else if (e.getDest().getName().equals(town)) {
                Character newCharacter = new Character(character, true);
                Town tObject = e.getDest();
                tObject.setCharacter(newCharacter); //figure out how to access town string Object
                townNetwork.addCharactersAndTown(character, tObject);

            }

        }
    }

    public static void passageSafe(JSONObject params) throws JSONException {
        //JSONObject passageSafeParam = object.getJSONObject("passage-safe?");
        String character = params.getString("character");
        String town = params.getString("town");

        for (Edge e : townNetwork.getEdges()) {
            if (e.getSource().getName().equals(town)) {
                if (townNetwork.getCharacterAndTown().containsKey(character)) {
                    Town destTown = e.getSource();
                    Town startTown = townNetwork.getCharacterAndTown().get(character);
                    townNetwork.query(startTown, destTown);
                }
            }

            else if(e.getDest().getName().equals(town))  {
                if (townNetwork.getCharacterAndTown().containsKey(character)) {
                    Town destTown = e.getDest();
                    Town startTown = townNetwork.getCharacterAndTown().get(character);
                    townNetwork.query(startTown, destTown);
                }

            }
        }
    }


}
*/
