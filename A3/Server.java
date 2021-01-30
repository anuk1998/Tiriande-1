import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.Scanner;
import a2.a2;
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
            String message = input.readLine();

            while(!message.equals("END")) {
            //while(this.acceptSocket.isConnected()) {
                //reading in from client
                message = this.input.readLine();
                //System.out.println(message);

                //replying back to client
                //String reply = this.s.nextLine();
                //this.output.println("server: " + reply);

                //compiling input from client into StringBuilder
                clientInput.append(message);

                System.out.println("testing");

            }

            System.out.println("made it out of while loop");

            //parsing input from client StringBuilder
            a2 hi = new a2();
            JSONArray messageToClient = hi.parse(clientInput.toString(), 1);

            //send message back to client
            this.output.println(messageToClient);

            //close socket
            serverSocket.close();
        } catch (IOException var3) {
            System.out.println(var3);
        }

    }
}
