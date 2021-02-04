import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import static java.lang.System.exit;
//test comment

public class TravellerTCPClient {
  private Socket clientSocket;
  private BufferedReader input;
  private OutputStream output;
  private Scanner s;
  private String signUpName;

  public TravellerTCPClient() {
    this.s = new Scanner(System.in);
  }

  public static void main(String[] args) {
    this.signUpName = "Glorifrir Flintshoulder";
    this.clientSocket = new Socket("localhost", 8000);

    if (args.length() == 1) { // ./a4 10.0.0.1
      this.clientSocket = new Socket(args[0], 8000);
    }
    else if (args.length() == 2) { // ./a4 127.0.0.1 8001
      this.clientSocket = new Socket(args[0], args[1]);
    }
    else if (args.length() == 3) { // ./a4 1.0.0.1 8120
      this.clientSocket = new Socket(args[0], args[1]);
      this.signUpName = args[2];
    }

    TravellerTCPClient client = new TravellerTCPClient();
    client.run();
  }

  public void run() {

    try {
      this.output = this.clientSocket.getOutputStream();
      PrintWriter writer = new PrintWriter(this.output, true);
      this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

      //send out signup name to server
      output.println(signUpName);
      //get session ID from server
      String sessionId = input.readLine();
      String serverWillCallMe = "[\"the server will call me\", " + sessionId + "]";
      System.out.println(serverWillCallMe);
      int commandCounter = 1;

      while(s.hasNextLine()) {
        String reply = s.nextLine();
        System.out.println(parseInput(reply, commandCounter));
        /*
        //client to server communication
        String reply = this.s.nextLine();
        writer.println(reply);
        writer.flush();
        if(reply.equals("END")){
        String message = this.input.readLine();
        System.out.println(message);
        break;
      }
      */
      commandCounter++;
    }

    clientSocket.close();

  }
  catch (Exception var3) {
    var3.printStackTrace();
  }
}

public String parseInput(String command, int commandCounter) throws org.json.JSONException {
  if (commandCounter == 1) {
    // *TODO*: maybe put all this if statement into separate helper checkRoads function?
    try {
      JSONObject roadsObject = new JSONObject(command);
      String commandValue = roadsObject.getJSONObject("command");
      if (!(commandValue.equals("roads"))) {
        clientSocket.close();
        String errorMessage = "{ \"error\" : \"not a request\", \"object\" : " + command + "}";
        System.out.println(errorMessage);
        exit(0);
      }
      //*TODO*: do NOT send 'command' alone to server, rebuild it to match the
      //        JSONObject request from WarmUp4 THEN send to server
      //*TODO*: make a helper function that rebuilds the JSON command and feed
      //        that into output.println(...)
      output.println(command);
      String serverResponse = input.readline();
      return serverResponse;
    }
    catch (Exception e) {
      clientSocket.close();
      System.out.println("First command wasn't roads creation. Terminating.");
      exit(0);
    }
  }


  // now we have to check if command is a batch request (place character/query)
  // start collecting commands into a batch

}

}

////////////////////////////////////////////////////////////////////////////////
//////////////////  CODE HERE FOR REFERENCE, DELETE LATER //////////////////////
////////////////////////////////////////////////////////////////////////////////

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
