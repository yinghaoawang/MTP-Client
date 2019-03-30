import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static Socket clientSocket;
    public static DataOutputStream out;
    public static DataInputStream in;
    public static String user = "Client";
    public static void startConnection(String ip, int port) throws Exception {
         clientSocket = new Socket(ip, port);
         out = new DataOutputStream(clientSocket.getOutputStream());
         in = new DataInputStream(clientSocket.getInputStream());
    }

    public static int sendMessage(String msg) throws IOException {
        out.writeUTF(user + ": " + msg);
        return 1;
    }

    public static void main(String args[]) throws Exception {
        startConnection("localhost", 1234);
        Scanner scanner = new Scanner(System.in);
        while(true) {
            try {
                sendMessage(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Couldn't send message.");
            }
        }

    }
}