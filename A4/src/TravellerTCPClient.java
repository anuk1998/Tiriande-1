import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import static java.lang.System.exit;

public class TravellerTCPClient {
  private static Socket clientSocket;
  private BufferedReader input;
  private Scanner s;
  private static String signUpName;
  private JSONArray batchRequests = new JSONArray();
  private OutputStream output;
  private PrintWriter writer;

  public TravellerTCPClient() {
    this.s = new Scanner(System.in);
  }

  public static void main(String[] args) throws IOException {
    signUpName = "Glorifrir Flintshoulder";
    clientSocket = new Socket("localhost", 8000);

    if (args.length == 1) {
      clientSocket = new Socket(args[0], 8000);
    }
    else if (args.length == 2) {
      clientSocket = new Socket(args[0], Integer.parseInt(args[1]);
    }
    else if (args.length == 3) {
      clientSocket = new Socket(args[0], Integer.parseInt(args[1]);
      signUpName = args[2];
    }

    TravellerTCPClient client = new TravellerTCPClient();
    client.run();
  }

  public void run() {
    try {
      output = clientSocket.getOutputStream();
      writer = new PrintWriter(output, true);
      input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      //send out signup name to server
      writer.println(signUpName);
      //get session ID from server
      String sessionId = input.readLine();
      String serverWillCallMe = "[\"the server will call me\", " + sessionId + "]";
      System.out.println(serverWillCallMe);
      int commandCounter = 1;

      while(s.hasNextLine()) {
        String reply = s.nextLine();
        JSONObject response = parseInput(reply, commandCounter);
        if (response != null) {
          System.out.println(response);
        }
        commandCounter++;
      }
      clientSocket.close();
    }
    catch (Exception var3) {
      var3.printStackTrace();
    }
  }

  public JSONObject parseInput(String command, int commandCounter) throws org.json.JSONException, IOException {
    JSONObject serverResponse = null;
    // only checking that the FIRST given command is a proper roads creation command
    if (commandCounter == 1) {
      checkRoadsCommand(command);
      JSONObject serverRoadsJSON = rebuildRoadsJSON(command);
      if (serverRoadsJSON != null) {
        writer.println(serverRoadsJSON);
        serverResponse = input.readLine();

      }
    }
    else { // check if batch command
      if (isCharacterCommand(command)) {
        JSONObject object = getCommandAsJSON(command)
        if (object != null) {
          batchRequests.put(object);
        }
      }
      if (isQueryCommand(command)) {
        JSONObject object = getCommandAsJSON(command);
        if (object != null) {
          batchRequests.put(object);
          writer.println(rebuildBatchJSON(batchRequests));
          batchRequests = new JSONArray();
          serverResponse = input.readLine();
        }
      }
    }
    return serverResponse;
  }

  public void checkRoadsCommand(String command) throws org.json.JSONException, IOException {
    try {
      JSONObject roadsObject = new JSONObject(command);
      JSONObject commandValue = roadsObject.getJSONObject("command");
      if (!(commandValue.equals("roads"))) {
        clientSocket.close();
        String errorMessage = "{ \"error\" : \"not a request\", \"object\" : " + command + "}";
        System.out.println(errorMessage);
        exit(0);
      }
    }
    catch (Exception e) {
      clientSocket.close();
      System.out.println("First command wasn't roads creation. Terminating.");
      exit(0);
    }
  }

  public boolean isCharacterCommand(String command) throws org.json.JSONException {
    try {
      JSONObject characterObject = new JSONObject(command);
      JSONObject commandValue = characterObject.getJSONObject("command");
      if (!(commandValue.equals("place"))) {
        return false;
      }
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  public boolean isQueryCommand(String command) throws org.json.JSONException {
    try {
      JSONObject queryObject = new JSONObject(command);
      JSONObject commandValue = queryObject.getJSONObject("command");
      if (!(commandValue.equals("passage-safe?"))) {
        String errorMessage = "{ \"error\" : \"not a request\", \"object\" : " + queryObject + "}";
        System.out.println(errorMessage);
        return false;
      }
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  public JSONObject getCommandAsJSON(String command) throws org.json.JSONException {
    try {
      JSONObject queryOrCharacterCommand = new JSONObject(command);
      return queryOrCharacterCommand;
    }
    catch (Exception e) {
      return null;
    }
  }

  public JSONObject rebuildRoadsJSON(String command) throws org.json.JSONException {
    JSONObject sendToServer = new JSONObject();
    JSONArray towns = new JSONArray();
    JSONArray roads = new JSONArray();

    try {
      JSONObject commandAsObject = new JSONObject(command);
      JSONObject params = commandAsObject.getJSONObject("params");
      JSONArray paramsArray = new JSONArray(params);

      for (int i = 0; i < paramsArray.length(); i++) {
        getTownsInObject(paramsArray.getJSONObject(i), towns);
        roads.put(paramsArray.getJSONObject(i));
      }

      sendToServer.put("towns", towns);
      sendToServer.put("roads", roads);

      return sendToServer;

    }
    catch (Exception e) {
      String errorMessage = "{ \"error\" : \"not a request\", \"object\" : " + command + "}";
      JSONObject error = new JSONObject(errorMessage);
      System.out.println(error);
      return null;
    }

  }

  public void getTownsInObject(JSONObject object, JSONArray towns) {
    try {
      String fromTown = object.getString("from");
      String toTown = object.getString("to");
      towns.put(fromTown);
      towns.put(toTown);
    }
    catch (Exception e) {
      String errorMessage = "{ \"error\" : \"not a request\", \"object\" : " + object + "}";
      System.out.println(errorMessage);
    }
  }

  public JSONObject rebuildBatchJSON(JSONArray listOfCommands) throws org.json.JSONException {

  }

}

/*
Task 3 example input:
{ "command" : "roads",
  "params" : [ {"from" : String, "to" : String }, ...] }

Task 4 example input:
{ "towns" : [ String, String, ...],
  "roads" : [ {"from" : String, "to" : String }, ... ] }

*/

/*
Task 3 example input:
{ "command" : "place",
  "params" : { "character" : String, "town" : String } }
{ "command" : "passage-safe?",
  "params" : { "character" : String, "town" : String } }


Task 4 example input:
{ "characters" : [ { "name" : String, "town" : String }, ... ],
  "query" : { "character" : String, "destination" : String } }

*/


////////////////////////////////////////////////////////////////////////////////
//////////////////  CODE HERE FOR REFERENCE, DELETE LATER //////////////////////
////////////////////////////////////////////////////////////////////////////////

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

private static void parseJson(String json_object) throws org.json.JSONException {

  JSONObject object = new JSONObject(json_object);

  try {
    JSONObject command = object.getJSONObject("command");
    JSONObject params = object.getJSONObject("params");
    // {"command": ........}

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
