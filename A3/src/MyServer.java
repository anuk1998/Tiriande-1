
import java.net.*;
import java.io.*;
import java.util.Scanner;


public class MyServer {

    private ServerSocket serverSocket;
    private Socket acceptSocket;
    private PrintStream output;
    private BufferedReader input;
    private Scanner s = new Scanner(System.in);


    public static void main(String[] args) {
        MyServer server = new MyServer();
        server.run();
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(8000);
            acceptSocket = serverSocket.accept();

            //server to client
            output = new PrintStream(acceptSocket.getOutputStream());

            //client to server
            input = new BufferedReader(new InputStreamReader(acceptSocket.getInputStream()));

            while(acceptSocket.isConnected()) {
                //reading messages in from client
                String message = input.readLine();
                System.out.println(message);

                //send messages back to client
                String reply = s.nextLine();
                output.println("server: " + reply);
            }


        } catch (IOException e) {
            System.out.println(e);
        }

    }





}

