
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.util.Scanner;
import static java.lang.System.exit;

public class travellerClient {

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

        parse_json(input_as_string.toString().replaceAll("[\\n\\t]", ""));

    }

    private static void parse_json(String json_object) throws org.json.JSONException {
        TownNetwork townNetwork = new TownNetwork(new ArrayList<Edge>());
        JSONObject object = new JSONObject(json_object);

        try {
            JSONObject command = object.getJSONObject("command");

            // start for loop here

            if(command.toString().equals("roads")) {
                // assuming "roads" is valid and is the first JSON object given.. **check for that**

                JSONArray roadsParam = new JSONArray(command.getJSONArray("params"));
                for (int i=0; i<roadsParam.length(); i++) {
                    JSONObject road = new JSONObject(roadsParam.getJSONObject(i));
                    Edge newRoad = new Edge(new Town(road.getString("from"), true, null),
                            new Town(road.getString("to"), true, null));
                    townNetwork.addEdge(newRoad);
                }
            }
            else if(command.toString().equals("place")) {
                //define object to be something else, change it
                JSONObject placeParam = object.getJSONObject("place");
                String character = placeParam.getString("character");
                String town = placeParam.getString("town");

                for (Edge e : townNetwork.getEdges()) {
                    if (e.getSource().getName().equals(town) || e.getDest().getName().equals(town)) {
                        Character newCharacter = new Character(character, true);
                        town.setCharacter(newCharacter); //figure out how to access town string Object
                        townNetwork.addCharactersAndTown(character, town);

                    }
                }
            }
            else if (command.toString().equals("passage-safe?")){
                //define object to be something else, change it
                JSONObject passageSafeParam = object.getJSONObject("passage-safe?");
                String character = passageSafeParam.getString("character");
                String town = passageSafeParam.getString("town");

                for (Edge e : townNetwork.getEdges()) {
                    if (e.getSource().getName().equals(town) || e.getDest().getName().equals(town)) {
                        if () {
                            townNetwork.query();
                        }
                    }
                }
            }


        }
        catch (Exception e) {
            exit(0);
        }

    }


}