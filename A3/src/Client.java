import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private BufferedReader input;
    private OutputStream output;
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
            this.output = this.clientSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(this.output, true);
            //this.output.println("client to server output");
            this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));


            while(true) {


                //client to server communication
                String reply = this.s.nextLine();
                writer.println(reply);
                writer.flush();
                if(reply.equals("END")){
                    String message = this.input.readLine();
                    System.out.println(message);
                    break;
                }

            }

            clientSocket.close();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }
}
