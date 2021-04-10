package Remote;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
  String host = "localhost";
  int port = 45678;

  public static void main(String[] args) {
    Client client = new Client();
    client.run();
  }

  public void run() {
    try {
      Socket clientSocket = new Socket(host, port);
      BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
      Scanner sc = new Scanner(System.in);

      while (true) {
        String serverMessage = input.readLine();
        boolean isResponseNeeded = parseServerMessage(serverMessage);
        if (isResponseNeeded) {
          String reply = sc.nextLine();
          output.println(reply);
          output.flush();
        }
      }

    } catch (Exception var3) {
      var3.printStackTrace();
    }
  }
}
