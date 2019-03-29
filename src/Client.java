import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static Socket clientSocket;
    public static PrintWriter out;
    public static BufferedReader in;

    public static void startConnection(String ip, int port) throws Exception {
         clientSocket = new Socket(ip, port);
         out = new PrintWriter(clientSocket.getOutputStream(), true);
         in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public static int sendMessage(String msg) {
        out.println(msg);
        return 1;
    }

    public static void main(String args[]) throws Exception {
        startConnection("localhost", 1234);
        Scanner scanner = new Scanner(System.in);
        while(true) {
            sendMessage(scanner.nextLine());
        }

    }
}