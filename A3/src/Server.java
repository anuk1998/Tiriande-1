import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.Scanner;

import a23.a23;
import org.json.JSONArray;
import org.json.JSONException;

public class Server {
    private ServerSocket serverSocket;
    private Socket acceptSocket;
    private PrintStream output;
    private BufferedReader input;
    private Scanner s;

    public Server() {
        this.s = new Scanner(System.in);
    }

    public static void main(String[] args) throws ParseException, JSONException {
        Server server = new Server();
        server.run();
    }

    public void run() throws ParseException, JSONException {

        StringBuilder clientInput = new StringBuilder();
        try {
            this.serverSocket = new ServerSocket(8000);
            this.acceptSocket = this.serverSocket.accept();
            this.output = new PrintStream(this.acceptSocket.getOutputStream());
            this.input = new BufferedReader(new InputStreamReader(this.acceptSocket.getInputStream()));
            //String message = input.readLine();
            String message = "";

            while(!message.equals("END")) {
                //reading in from client
                message = this.input.readLine();

                //compiling input from client into StringBuilder
                clientInput.append(message);
            }


            //parsing input from client StringBuilder
            a23 inputParser = new a23();
            JSONArray messageToClient = inputParser.parse(clientInput.toString(), 0);
            System.out.println(messageToClient);
            //send message back to client
            this.output.println(messageToClient);
            this.output.flush();

            //close socket
            acceptSocket.close();
            serverSocket.close();
        } catch (IOException var3) {
            System.out.println(var3);
        }

    }
}

