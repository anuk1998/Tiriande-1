import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class a2 {
  private static JSONArray output_array = new JSONArray();
  private static boolean sum;
  private static String temp_json_object;
  private static ArrayList<String> list_of_njsons = new ArrayList<>();

  public static void main(String[] args) throws ParseException, JSONException {
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
    parse(input_as_string.toString().replaceAll("[\\n\\t]", ""), 0);
    System.out.println(output_array);
  }

  private static void parse(String input, int index) throws ParseException, JSONException {
    int inc_value = 1;
    int total;
    String str;

    for (int i=index; i<input.length(); i+=inc_value) {
      inc_value = 1;
      if (Character.isDigit(input.charAt(i))) {
        str = parse_ints(input, i).toString();
        inc_value = str.length();

        list_of_njsons.add(str);
        total = compute(str);
        add_to_jsonarray(str, total);
      }
      else if (Character.toString(input.charAt(i)).equals("[")) {
        StringBuilder object2 = new StringBuilder();
        object2.append(Character.toString(input.charAt(i)));

        str = parse_array(input, object2, i).replaceAll("[\\n\\t ]", "");
        inc_value = str.length();

        list_of_njsons.add(str);
        total = compute(str);
        add_to_jsonarray(str, total);
      }
      else if (Character.toString(input.charAt(i)).equals("{")) {
        StringBuilder object = new StringBuilder();
        object.append(Character.toString(input.charAt(i)));

        str = parse_object(input, object, i).replaceAll("[\\n\\t ]", "");
        list_of_njsons.add(str);
        inc_value = str.length();

        parse_json(str);
        list_of_njsons.add(str);
        total = compute(str);
        add_to_jsonarray(str, total);
        break;
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
  }

  private static String parse_array(String input, StringBuilder object, int index) {
    int inc_value = 1;
    for (int i=index+1; i<input.length(); i+=inc_value) {
      inc_value = 1;
      if (Character.toString(input.charAt(i)).equals("]")) {
        object.append(Character.toString(input.charAt(i)));
        break;
      }
      else if (Character.toString(input.charAt(i)).equals("[")) {
        object.append(Character.toString(input.charAt(i)));
        String recur = parse_array(input, new StringBuilder(), i);
        object.append(recur);
        inc_value = recur.length() + 1;
      }
      else {
        object.append(Character.toString(input.charAt(i)));
      }
    }
    return object.toString();
  }

  private static String parse_object(String input, StringBuilder object, int index) {
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
    return object.toString();
  }

  private static void parse_json(String json_object) throws JSONException {
    temp_json_object = json_object;
    JSONObject object = new JSONObject(json_object);

    try {
      JSONObject payload = object.getJSONObject("payload");
      parse(payload.toString(), 0);
    }
    catch (Exception e) {
      compute(json_object);
    }

  }

  private static int compute(String num_json) throws JSONException {
    ArrayList<Integer> output = new ArrayList<>();

    for (String njson : list_of_njsons) {
      if (Character.toString(njson.charAt(0)).equals("{")) {
        String new_njson = njson.replaceAll("[\\n\\t ]\" \"", "");
        System.out.println(new_njson);
        JSONObject object = new JSONObject(new_njson);
        try {
          String payload = object.get("payload").toString();
          output = get_list_to_compute(payload);
        }
        catch (Exception e) {
          continue;
        }
      }
      else {
        output = get_list_to_compute(num_json);
      }
    }

    if (sum) {
      int sum_total = 0;
      for (int num : output) {
        sum_total += num;
      }

      return sum_total;
    }
    else {
      int product_total = 1;
      for (int num : output) {
        product_total *= num;
      }
      return product_total;
    }

  }

  private static ArrayList<Integer> get_list_to_compute(String num_json) {
    ArrayList<Integer> output = new ArrayList<>();
    int size_of_inc = 1;
    int integer;

    for (int i=0; i<num_json.length(); i+=size_of_inc) {
      size_of_inc = 1;
      char c = num_json.charAt(i);
      if (Character.isDigit(c)) {
        String result = parse_ints(num_json, i).toString();
        size_of_inc = result.length();
        integer = Integer.parseInt(result);
        output.add(integer);
      }
    }

    return output;
  }

  private static void add_to_jsonarray(String input, int total) throws JSONException {
    JSONObject obj = new JSONObject();
    String first = Character.toString(input.charAt(0));

    if (first.equals("{")) {
      obj.put("object", temp_json_object);
    }
    else {
      obj.put("object", input);
    }
    obj.put("total", total);

    output_array.put(obj);
  }

}
