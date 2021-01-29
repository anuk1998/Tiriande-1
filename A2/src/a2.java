import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;


public class a2 {
  private static JSONArray output_array = new JSONArray();
  private static boolean sum;
  private static String temp_json_object;

  public static void main(String[] args) throws ParseException, IOException, JSONException {

    //BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
    //StringBuilder stdout = new StringBuilder();

    if (args[0].equals("--sum")) {
      sum = true;
    }
    else if (args[0].equals("--product")) {
      sum = false;
    }

    StringBuilder input_as_string = new StringBuilder();
    String text;
//    Scanner scanner = new Scanner( System.in );
//    while( scanner.hasNextLine() )
//    {
//     text = scanner.nextLine();
//      input_as_string.append( text );
//    }
//    scanner.close();
    System.out.println();
    System.out.println("INPUT AS STRING-BUILDER: " + input_as_string);


    //parse(input_as_string.toString(), 0);
    String mock_input = "12 { \"name\" : \"SwDev\", \"payload\" : \n" +
            "  [12, 33]     , \n" +
            "        \"other\" : { \"payload\" : [ 4, 7 ] } }";
    parse(mock_input, 0);
    System.out.println("OUTPUT JSONARRAY:");
    System.out.println(output_array);
  }

  private static void parse(String input, int index) throws ParseException, JSONException {
    int inc_value = 1;
    for (int i=index; i<input.length(); i+=inc_value) {
      inc_value = 1;
      char current_character = input.charAt(i);
      if (Character.isDigit(input.charAt(i))) {
        String whole_number = parse_ints(input, i).toString();
        inc_value = whole_number.length();
        System.out.println(whole_number);

        compute(whole_number);
      }
      else if (Character.toString(input.charAt(i)).equals("[")) {
        StringBuilder object2 = new StringBuilder();
        object2.append(Character.toString(input.charAt(index)));

        String array_numjson = parse_array(input, object2, i).toString().replaceAll("[\\n\\t ]", "");
        inc_value = array_numjson.length();
        System.out.println(array_numjson);
        //parse_array(input, i+1);

        compute(array_numjson);
      }
      else if (Character.toString(input.charAt(i)).equals("{")) {
        StringBuilder object = new StringBuilder();
        object.append(Character.toString(input.charAt(i)));

        System.out.println();
        System.out.println();

        String json_object = parse_object(input, object, i).replaceAll("[\\n\\t ]", "");
        inc_value = json_object.length();
        System.out.println(json_object);
        //exit(0);
        //System.out.println(object.toString().replaceAll("[\\n\\t ]", ""));

        //compute(json_object);
        parse_json(json_object);
        compute(json_object);

      }
    }
  }


  private static StringBuilder parse_ints(String input, int index) {
    StringBuilder build_int = new StringBuilder();
    for (int i=index; i<input.length(); i++) {
      char c = input.charAt(i);
      if (Character.isDigit(c)) {
        build_int.append(c);
      }
      else {
        return build_int;
      }
    }

    return build_int;

    //System.out.println(build_int);

    //compute(list_of_ints);
    //add_to_jsonarray(build_int);
  }

  private static String parse_array(String input, StringBuilder object, int index) throws ParseException, JSONException {
    for (int i=index+1; i<input.length(); i++) {
      if (Character.toString(input.charAt(i)).equals("]")) {
        object.append(Character.toString(input.charAt(i)));
        //add break here
        // do what you do in parse_objects bug
      }
      else if (Character.toString(input.charAt(i)).equals("[")) {
        object.append(Character.toString(input.charAt(i)));
        parse_array(input, new StringBuilder(), i);
      }
      else {
        object.append(Character.toString(input.charAt(i)));
        //parse(input, i+1);
      }
    }

    return object.toString();
  }

  private static String parse_object(String input, StringBuilder object, int index) throws ParseException {
    int inc_value = 1;
    for (int j=index+1; j<input.length(); j+=inc_value) {
      inc_value = 1;
      if (Character.toString(input.charAt(j)).equals("}")) {
        object.append(Character.toString(input.charAt(j)));
        break;
      }
      else if (Character.toString(input.charAt(j)).equals("{")) {
        object.append(Character.toString(input.charAt(j)));
        String recur = parse_object(input, new StringBuilder(), j);
        object.append(recur);
        inc_value = recur.length();
      }
      else {
        object.append(Character.toString(input.charAt(j)));
      }
    }

    //System.out.println(object.toString());
    return object.toString();

  }

  private static void parse_json(String json_object) throws ParseException, JSONException {
    temp_json_object = json_object;

    JSONObject object = new JSONObject(json_object);
    JSONObject payload = object.getJSONObject("payload");
    //JSONArray jsonArray = object.getJSONArray("payload");

    System.out.println("JSONARRAY: " + payload);
    System.out.println();

    parse(payload.toString(), 0);
    //compute(payload.toString());
    //exit(0);

  }

  // num_json = [294858, "food", [4, 5, 6, "hi"], 9]
  // num_json = {"name":"SwDev","payload":[12,33],"other":{"payload":[4,7]}
  // num_json = 12
  // num_json = {"name":"SwDev","payload":{"payload":[4,7]}}
  // num_json = ["foo", "fee", "faa"]
  // {"name":"SwDev","payload":{"digit": 12,"payload":[12,33]},"other":{"payload":[4,7]},"other":{"payload":[4,7]}

  private static void compute(String num_json) throws JSONException {
    ArrayList<Integer> output = new ArrayList<>();
    int size_of_inc = 1;
    int integer;

//    if (Character.toString(num_json.charAt(0)).equals("{")) {
//      JSONObject object = new JSONObject(num_json);
//      JSONObject payload = object.getJSONObject("payload");
//      String payload_string = payload.toString();
//    }

    for (int i=0; i<num_json.length(); i+=size_of_inc) {
      char c = num_json.charAt(i);
      if (Character.isDigit(c)) {
        String result = parse_ints(num_json, i).toString();
        size_of_inc = result.length();
        integer = Integer.parseInt(result);
        output.add(integer);
      }
    }

    if (sum) {
      int sum_total = 0;
      for (int num : output) {
        sum_total += num;
      }
      add_to_jsonarray(num_json, sum_total);
    }
    else {
      int product_total = 1;
      for (int num : output) {
        product_total *= num;
      }
      add_to_jsonarray(num_json, product_total);
    }

  }

  private static void add_to_jsonarray(String input, int total) throws JSONException {
    JSONObject obj = new JSONObject();

    //String first = Character.toString(input.charAt(0));

//    if (first.equals("[")) {
//      // is array
//      JSONArray input_new = (JSONArray) input;
//      obj.put("object", input_new);
//    }
//    else if (first.equals("{")) {
//      // is object
//      JSONObject input_new =;
//      obj.put("object", input_new);
//    }
//    else if (Character.isDigit(input.charAt(0))) {
//      // is digit
//      int input_new = Integer.getInteger(input.toString());
//      obj.put("object", input_new);
//    }

    obj.put("object", input);
    obj.put("total", total);

    output_array.put(obj);
  }

  /*
  public static String output_json() throws JSONException {


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
