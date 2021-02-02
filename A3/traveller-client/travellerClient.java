import com.oracle.javafx.jmx.json.JSONException;

import java.text.ParseException;
import java.util.Scanner;

public class traveller {

    public static void main(String[] args) throws ParseException, JSONException {

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

    private static void parse_json(String json_object) throws JSONException {
        temp_json_object = json_object;
        JSONObject object = new JSONObject(json_object);

        try {
            JSONObject command = object.getJSONObject("command");

            if(command.toString().equals("roads")) {

            }
            else if(command.toString().equals("place")) {
                Server serv = new Server();
                serv.query(...place);

            }
            else if (command.toString().equals("passage-safe?")){

            }


        }
        catch (Exception e) {
            exit(0);
        }

    }


}