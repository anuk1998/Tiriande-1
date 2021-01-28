import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.HashMap;

import static java.lang.System.exit;

// ./a2 --sum < text.json

public class a2 {
  private static JSONArray output_array = new JSONArray();
  static boolean sum;
  private static ArrayList<Integer> list_of_ints = new ArrayList<>();
  static HashMap<Integer, ArrayList<Integer>> numjsons = new HashMap<Integer, ArrayList<Integer>>();

  public static void main(String[] args) throws ParseException, IOException {

    //Scanner scan = new Scanner(System.in);
    //BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
    //StringBuilder stdout = new StringBuilder();

    //while ((input = stdin.readLine()) != null) {
    //  System.out.println(input);
    //}
    if (args[0].equals("--sum")) {
      sum = true;
    }
    else if (args[0].equals("--product")) {
      sum = false;
    }

    StringBuilder input_as_string = new StringBuilder();
    String text;
    Scanner scanner = new Scanner( System.in );
    while( scanner.hasNextLine() )
    {
      text = scanner.nextLine();
      input_as_string.append( text );
    }
    scanner.close();
    System.out.println();
    System.out.println("INPUT AS STRING-BUILDER: " + input_as_string);



      //System.out.println(i + ": " + c);
    parse(input_as_string, 0);
    //}


    /*
    while (true) {
      try {
        int c = stdin.read();
        if (c != 4 || c != -1)  // ASCII 4 04 EOT (end of transmission) ctrl D, I may be wrong here
          stdout.append (c);
        else
          break;
      } catch (IOException e) {
        System.err.println ("Error reading input");
      }
    }
    */



    //String tokens[] = scan.nextLine().split(" ");

    //System.out.println(Arrays.asList(tokens));
    //scan.close();

    //while (scan.hasNext()) {
    //  System.out.println();
    //}
    //String input = scan.nextLine();

    //scan.close();

    //System.out.println(input);
    //parsing

    //System.out.println(args[2]);
    //System.out.println(parsed);

    // ./a2 --sum "12 [4, "foo", 2] {cioargoirgeoierg}"

    //if (args[1].equals("--sum")) {
      //do the adding
      //build output JSON
    //}
    //else if (args[1].equals("--product")) {
      //do the multiplcation
      //build output JSON
    //}
  }

  private static void parse(StringBuilder input, int index) throws ParseException {

    for (int i=index; i<input.length(); i++) {
      if (Character.isDigit(input.charAt(i))) {
        parse_ints(input, i+1);
      }
      else if (Character.toString(input.charAt(i)).equals("[")) {
        parse_array(input, i+1);
      }
      else if (Character.toString(input.charAt(i)).equals("{")) {
        StringBuilder object = new StringBuilder();
        object.append(Character.toString(input.charAt(index)));

        System.out.println();
        System.out.println();

        String json_object = parse_object(input, object, i).toString().replaceAll("[\\n\\t ]", "");
        System.out.println(json_object);
        //System.out.println(object.toString().replaceAll("[\\n\\t ]", ""));

        parse_json(json_object);
        //parse_json(object.toString().replaceAll("[\\n\\t ]", ""));
        //parse_object(input, i+1);
      }
    }
  }

  private static void parse_json(String json_object) throws ParseException {
    JSONParser parser = new JSONParser();
    JSONObject parsed = (JSONObject) parser.parse(json_object);

    try {
      JSONObject payload = (JSONObject) parsed.get("payload");
      System.out.println("PAYLOAD IS: " + payload);
      exit(0);
    }
    catch (Exception e) {
      //this should never happen, but still here just in case
      e.printStackTrace();
    }

  }

  private static StringBuilder parse_ints(StringBuilder input, int index) {
    StringBuilder build_int = new StringBuilder();
    for (int i=index; i<input.length(); i++) {
      char c = input.charAt(i);
      if (Character.isDigit(c)) {
        build_int.append(c);
      }
      else {
        break;
        // do other stuff
      }
    }

    System.out.println(build_int);
    return build_int;
    //compute(list_of_ints);
    //add_to_jsonarray(build_int);
  }

  private static void parse_array(StringBuilder input, int index) throws ParseException {
    StringBuilder build_int = new StringBuilder();
    for (int i=index; i<input.length(); i++) {
      if (Character.toString(input.charAt(i)).equals("]")) {
        break;
      }
      else {
        parse(input, i+1);
      }
    }
  }

  private static StringBuilder parse_object(StringBuilder input, StringBuilder object, int index) throws ParseException {
    for (int j=index+1; j<input.length(); j++) {
      if (Character.toString(input.charAt(j)).equals("{")) {
        parse_object(input, object, j);
      }
      if (Character.toString(input.charAt(j)).equals("}")) {
        System.out.println(input.charAt(j));
        object.append(Character.toString(input.charAt(j)));
        break;
      }
      else {
        object.append(Character.toString(input.charAt(j)));
      }
    }
    return object;
//    StringBuilder build_int = new StringBuilder();
//    for (int i=index; i<input.length(); i++) {
//      if (Character.toString(input.charAt(i)).equals("}")) {
//        break;
//      }
//      else {
//        parse(input, i+1);
//      }
//    }
  }

  private static void compute(ArrayList<Integer> list_of_ints) {

  }

  private static void add_to_jsonarray(StringBuilder input) {

  }

  /*
  public static String output_json() throws JSONException {
    JSONArray ja = new JSONArray();

    ja.put(Boolean.TRUE);
    ja.put("lorem ipsum");

    JSONObject jo = new JSONObject();
    jo.put("name", "jon doe");
    jo.put("age", "22");
    jo.put("city", "chicago");

    ja.put(jo);

    return "hi";
  }
  */



}
