package Remote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Game.GameManager;
import Game.MessageType;
import Game.Position;
import Game.Registration;
import Game.*;
import User.RemoteUser;
import Manager.TestManager;

public class ClientThread extends Thread {
  private Socket socket;
  private PrintWriter output;
  private BufferedReader input;
  private GameManager manager;

  public ClientThread(Socket socket, GameManager manager) {
    this.socket = socket;
    this.manager = manager;
  }

  @Override
  public void run() {
    try {
      input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      output = new PrintWriter(socket.getOutputStream(), true);
      output.println(serverWelcomeMessage());
      output.println("name");
      String message = input.readLine();
      Registration status = manager.registerPlayer(message, Registration.REMOTE);
      while (status.toString().equals("DUPLICATE_NAME")) {
        output.println("That name is already in use. Please give another name:");
        message = input.readLine();
        status = manager.registerPlayer(message, Registration.REMOTE);
      }
      manager.passConnectionToRemoveUser(message, this);
    }
    catch (IOException | JSONException e) {
      e.printStackTrace();
    }
  }

  private JSONObject serverWelcomeMessage() throws JSONException {
    JSONObject welcome = new JSONObject();
    welcome.put("type", "welcome");
    welcome.put("info", "Version: 1. Server Group Owner: Tiriande. Players get maximum 3 chances to make a valid move, otherwise they lose their turn.");
    return welcome;
  }

  public void sendToClient(String message, MessageType type) {
    if (type.equals(MessageType.OBSERVER_VIEW) ||
            type.equals(MessageType.NO_MOVE) ||
            type.equals(MessageType.RESULT)) {
      output.println(message);
    }
    else {
      output.println(makeStartLevelMessage().toString());
    }
  }

  public void sendToAllPlayerClients(ArrayList<ClientThread> clients, JSONObject update) {
    for (ClientThread client : clients) {
      client.output.println(update);
    }
  }

  public Position getMoveFromClient(String message, MessageType type) throws IOException {
    Position movePos = null;
    if (type.equals(MessageType.MOVE)) {
      output.println(message);
      String move = input.readLine();
      try {
        JSONObject moveObject = new JSONObject(move);
        if (moveObject.isNull("to")) return null;
        JSONArray pos = moveObject.getJSONArray("to");
        movePos = new Position(pos.getInt(0), pos.getInt(1));
      }
      catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return movePos;
  }

}
