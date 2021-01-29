import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MyClient {

    private Socket clientSocket;
    private BufferedReader input;
    private PrintStream output;
    private Scanner s = new Scanner(System.in);

    public static void main(String[] args) {
        MyClient client = new MyClient();
        client.run();
    }

    public void run() {
        try {

            //client to server
            clientSocket = new Socket("localhost", 8000);
            output = new PrintStream(clientSocket.getOutputStream());
            output.println("client to server output");


            //server to client
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while(clientSocket.isConnected()) {
                //reading messages in from server
                String message = input.readLine();
                System.out.println(message);

                //replying to server
                String reply = s.nextLine();
                output.println("client: " + reply);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
