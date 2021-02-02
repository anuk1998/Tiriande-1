import com.oracle.javafx.jmx.json.JSONException;

import java.text.ParseException;
import java.util.Scanner;

public class traveller {

    public static void main(String[] args) throws ParseException, JSONException {

        StringBuilder input_as_string = new StringBuilder();
        String text;
        Scanner scanner = new Scanner( System.in );
        while( scanner.hasNextLine() )
        {
            text = scanner.nextLine();
            input_as_string.append( text );
        }
        scanner.close();

        if (args[1].equals("roads")) {
            //create network
        }
        else if (args[1].equals("people")) {
            //place character
        }
        else (args[1].equals("passage-safe?")) {
            //query()
        }

    }

    public void createNetworkHelper() {

    }

    public void placeCharacterHelper() {

    }

    public void queryHelper() {

    }
}