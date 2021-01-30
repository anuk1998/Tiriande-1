
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private BufferedReader input;
    private PrintStream output;
    private Scanner s;

    public Client() {
        this.s = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    public void run() {
        try {
            this.clientSocket = new Socket("localhost", 8000);
            this.output = new PrintStream(this.clientSocket.getOutputStream());
            this.output.println("client to server output");
            this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            while(this.clientSocket.isConnected()) {
                //String message = this.input.readLine();
                //System.out.println(message);

                //client to server communication
                String reply = this.s.nextLine();
                this.output.println("client: " + reply);
                this.output.flush();
            }

            clientSocket.close();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }
}
