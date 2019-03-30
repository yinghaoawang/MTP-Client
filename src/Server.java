import java.io.*;
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
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        while (true) {
            String inputLine = in.readUTF();
            if (inputLine != null) {
                System.out.println(inputLine);
            }
        }
    }
}