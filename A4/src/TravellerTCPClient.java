import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import static java.lang.System.exit;
import org.json.JSONArray;
import org.json.JSONObject;

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
    String host = "localhost";
    int port = 8000;
    signUpName = "Glorifrir Flintshoulder";

    if (args.length == 1) {
      host = args[0];
    }
    else if (args.length == 2) {
      host = args[0];
      port = Integer.parseInt(args[1]);
    }
    else if (args.length == 3) {
      host = args[0];
      port = Integer.parseInt(args[1]);
      signUpName = args[2];
    }

    clientSocket = new Socket(host, port);
    TravellerTCPClient client = new TravellerTCPClient();
    client.run();
  }

  // Begins collecting user-input from STDIN (until Ctrl+D) and sends input to
  // be parsed.
  public void run() {
    try {
      output = clientSocket.getOutputStream();
      writer = new PrintWriter(output, true);
      input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      writer.println(signUpName);
      String sessionId = input.readLine();
      String serverWillCallMe = "[\"the server will call me\", " + signUpName + "]";
      System.out.println(serverWillCallMe);
      int commandCounter = 1;

      while(s.hasNextLine()) {
        String reply = s.nextLine();
        parseInput(reply, commandCounter);
        commandCounter++;
      }
      clientSocket.close();
    }
    catch (Exception var3) {
      var3.printStackTrace();
    }
  }

  // Determines if user input is a roads creation command, place character command,
  // or query command, and acts accordingly.
  public void parseInput(String command, int commandCounter) throws org.json.JSONException, IOException {
    String serverResponse;
    // checking that the FIRST given command is a proper roads creation command
    if (commandCounter == 1) {
      checkRoadsCommand(command);
      JSONObject serverRoadsJSON = rebuildRoadsJSON(command);
      if (serverRoadsJSON != null) {
        writer.println(serverRoadsJSON);
      }
    }
    else { // check if batch command
      if (isCharacterCommand(command)) {
        JSONObject object = getCommandAsJSON(command);
        if (object != null) {
          batchRequests.put(object);
        }
      }
      else if (isQueryCommand(command)) {
        JSONObject object = getCommandAsJSON(command);
        if (object != null) {
          batchRequests.put(object);
          JSONObject batchRequest = rebuildBatchJSON(batchRequests);
          writer.println(batchRequest);
          serverResponse = input.readLine();
          parseServerResponse(serverResponse);
          batchRequests = new JSONArray();
        }
      }
    }
  }

  // Parses the JSON reponse that the server sends back to client after a batch request
  private void parseServerResponse(String serverResponse) {
    try {
      JSONObject serverResponseJSON = new JSONObject(serverResponse);
      JSONArray invalidArray = serverResponseJSON.getJSONArray("invalid");
      if (invalidArray.length() != 0) {
        for (int i=0; i<invalidArray.length(); i++) {
          String invalidMessage = "[\"invalid placement\", " + invalidArray.getJSONObject(i).toString() + "]";
          System.out.println(invalidMessage);
        }
      }
      JSONObject mostRecentQueryRequest = batchRequests.getJSONObject(batchRequests.length() - 1);
      boolean queryAnswer = serverResponseJSON.getBoolean("response");
      String queryResponse = "[\"the response for\", " + mostRecentQueryRequest + " , \"is\", " + queryAnswer + "]";
      System.out.println(queryResponse);
    }
    catch (Exception e) {
      System.out.println("Invalid response from server.");
    }
  }

  // Checks to see if the given command is a roads creation command. If it isn't,
  // the client will give the user an error message and close the connection.
  // If it is a valid roads creation command, the program will do nothing.
  public void checkRoadsCommand(String command) throws org.json.JSONException, IOException {
    try {
      JSONObject roadsObject = new JSONObject(command);
      String commandValue = roadsObject.getString("command");
      if (!(commandValue.equals("roads"))) {
        clientSocket.close();
        String errorMessage = "{ \"error\" : \"not a roads creation command\", \"object\" : " + command + "}";
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

  // Checks to see if given command is a place character command.
  // Returns true if it is, false if it isn't.
  public boolean isCharacterCommand(String command) throws org.json.JSONException {
    try {
      JSONObject characterObject = new JSONObject(command);
      String commandValue = characterObject.getString("command");
      if (!(commandValue.equals("place"))) {
        return false;
      }
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  // Checks to see if the given command is a query command.
  // Returns true if it is, false if it isn't.
  // If it isn't, it is determined that it is not a valid request at all (since
  // this is the last function a user input goes through to check for validity)
  // and the function also throws an error (does not terminate, though).
  public boolean isQueryCommand(String command) throws org.json.JSONException {
    try {
      JSONObject queryObject = new JSONObject(command);
      String commandValue = queryObject.getString("command");
      if (!(commandValue.equals("passage-safe?"))) {
        String errorMessage = "{ \"error\" : \"not a valid request\", \"object\" : " + queryObject + "}";
        System.out.println(errorMessage);
        return false;
      }
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  // Returns a string command as a JSONObject
  public JSONObject getCommandAsJSON(String command) throws org.json.JSONException {
    try {
      return new JSONObject(command);
    }
    catch (Exception e) {
      return null;
    }
  }

  // Builds the JSONObject that the client will send to the server representing
  // the roads creation command the user gives to the client via STDIN.
  public JSONObject rebuildRoadsJSON(String command) throws org.json.JSONException {
    JSONObject sendToServer = new JSONObject();
    JSONArray towns = new JSONArray();
    JSONArray roads = new JSONArray();

    try {
      JSONObject commandAsObject = new JSONObject(command);
      JSONArray paramsArray = commandAsObject.getJSONArray("params");

      for (int i = 0; i < paramsArray.length(); i++) {
        getTownsInObject(paramsArray.getJSONObject(i), towns);
        roads.put(paramsArray.getJSONObject(i));
      }

      sendToServer.put("towns", towns);
      sendToServer.put("roads", roads);

      return sendToServer;

    }
    catch (Exception e) {
      System.out.println(e);
      String errorMessage = "{ \"error\" : \"malformed create roads request\", \"object\" : " + command + "}";
      JSONObject error = new JSONObject(errorMessage);
      System.out.println(error);
      return null;
    }

  }

  // Adds towns that appear in JSONObjects inputted by the user
  // to a list of towns (that will eventually be sent to the server)
  public void getTownsInObject(JSONObject object, JSONArray towns) throws org.json.JSONException {
    try {
      String fromTown = object.getString("from");
      String toTown = object.getString("to");
      towns.put(fromTown);
      towns.put(toTown);
    }
    catch (Exception e) {
      String errorMessage = "{ \"error\" : \"malformed create roads to-from object\", \"object\" : " + object + "}";
      System.out.println(errorMessage);
    }
  }

  // Rebuilds a batch request from server format to user format using rebuildQuery
  // & rebuildCharacterPlacement helper functions
  public JSONObject rebuildBatchJSON(JSONArray listOfCommands) throws org.json.JSONException {
    JSONObject sendToServer = new JSONObject();
    JSONArray characters = new JSONArray();
    JSONObject tempObject = new JSONObject();

    try {
      for (int i=0; i<listOfCommands.length(); i++) {
        tempObject = listOfCommands.getJSONObject(i);
        if (i == listOfCommands.length() - 1 && isQueryCommand(tempObject.toString())) {
          sendToServer.put("characters", characters);
          rebuildQuery(tempObject, sendToServer);
          return sendToServer;
        }
        else {
          rebuildCharacterPlacement(tempObject, characters);
        }
      }
    }
    catch (Exception e) {
      String errorMessage = "{ \"error\" : \"invalid batch request\", \"object\" : " + tempObject.toString() + "}";
      System.out.println(errorMessage);
    }
    return null;
  }

  // Rebuilds a place character command from user format to server format
  public void rebuildCharacterPlacement(JSONObject placeCharacter, JSONArray characters) throws org.json.JSONException {
    try {
      JSONObject characterObject = new JSONObject();

      JSONObject getParams = placeCharacter.getJSONObject("params");
      String character = getParams.getString("character");
      String town = getParams.getString("town");

      characterObject.put("name", character);
      characterObject.put("town", town);

      characters.put(characterObject);
    }
    catch (Exception e) {
      String errorMessage = "{ \"error\" : \"invalid character placement request\", \"object\" : " + placeCharacter + "}";
      System.out.println(errorMessage);
    }
  }

  // Rebuilds a query command from user format to server format
  public void rebuildQuery(JSONObject query, JSONObject sendToServer) throws org.json.JSONException {
    try {
      JSONObject getParams = query.getJSONObject("params");
      String character = getParams.getString("character");
      String town = getParams.getString("town");

      JSONObject queryToServer = new JSONObject();
      queryToServer.put("character", character);
      queryToServer.put("destination", town);

      sendToServer.put("query", queryToServer);
    }
    catch (Exception e){
      String errorMessage = "{ \"error\" : \"invalid passage-safe? command\", \"object\" : " + query + "}";
      System.out.println(errorMessage);
    }
  }

}
