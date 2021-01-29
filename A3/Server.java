import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private ServerSocket serverSocket;
    private Socket acceptSocket;
    private PrintStream output;
    private BufferedReader input;
    private Scanner s;

    public Server() {
        this.s = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    public void run() {
        try {
            this.serverSocket = new ServerSocket(8000);
            this.acceptSocket = this.serverSocket.accept();
            this.output = new PrintStream(this.acceptSocket.getOutputStream());
            this.input = new BufferedReader(new InputStreamReader(this.acceptSocket.getInputStream()));

            while(this.acceptSocket.isConnected()) {
                String message = this.input.readLine();
                System.out.println(message);
                String reply = this.s.nextLine();
                this.output.println("server: " + reply);
            }
        } catch (IOException var3) {
            System.out.println(var3);
        }

    }
}
