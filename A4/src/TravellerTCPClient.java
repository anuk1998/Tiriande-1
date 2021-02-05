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
      clientSocket = new Socket(args[0], Integer.parseInt(args[1]));
    }
    else if (args.length == 3) {
      clientSocket = new Socket(args[0], Integer.parseInt(args[1]));
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
      String serverWillCallMe = "[\"the server will call me\", " + signUpName + "]";
      System.out.println(serverWillCallMe);
      int commandCounter = 1;

      while(s.hasNextLine()) {
        String reply = s.nextLine();
        String response = parseInput(reply, commandCounter);
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

  public String parseInput(String command, int commandCounter) throws org.json.JSONException, IOException {
    String serverResponse = null;
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
        JSONObject object = getCommandAsJSON(command);
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
        String errorMessage = "{ \"error\" : \"not a valid passage-safe? command\", \"object\" : " + queryObject + "}";
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
      String errorMessage = "{ \"error\" : \"malformed create roads request\", \"object\" : " + command + "}";
      JSONObject error = new JSONObject(errorMessage);
      System.out.println(error);
      return null;
    }

  }

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

  public JSONObject rebuildBatchJSON(JSONArray listOfCommands) throws org.json.JSONException {
    JSONObject sendToServer = new JSONObject();
    JSONArray characters = new JSONArray();
    JSONObject tempObject = new JSONObject();

    try {
      for (int i=0; i<listOfCommands.length(); i++) {
        tempObject = listOfCommands.getJSONObject(i);
        if (i == listOfCommands.length() - 1 && isQueryCommand(listOfCommands.getJSONObject(i).toString())) {
          sendToServer.put("characters", characters);
          rebuildQuery(listOfCommands.getJSONObject(i), sendToServer);
          return sendToServer;
        }
        else {
          rebuildCharacterPlacement(listOfCommands.getJSONObject(i), characters);
        }
      }
    }
    catch (Exception e) {
      String errorMessage = "{ \"error\" : \"invalid batch request\", \"object\" : " + tempObject.toString() + "}";
      System.out.println(errorMessage);
    }
    return null;
  }

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

}
