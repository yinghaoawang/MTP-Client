import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String args[]) throws Exception {
        // Creates a server socket
        ServerSocket serverSocket = new ServerSocket(1234);

        // Waits for a client to connect
        Socket clientSocket = serverSocket.accept();
        if (clientSocket.isConnected()) {
            System.out.println(clientSocket.getInetAddress() + " has connected.");
        }
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        while (true) {
            String inputLine = in.readLine();
            if (inputLine != null) {
                System.out.println(inputLine);
            }
        }
    }
}