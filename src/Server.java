import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    ServerSocket serverSocket;
    Socket clientSocket;
    DataOutputStream out;
    DataInputStream in;

    public void createServer(int port) throws IOException {
        serverSocket = new ServerSocket(1234);
    }

    public void awaitConnection() throws IOException {
        if (serverSocket != null) {
            // Waits for a client to connect
            clientSocket = serverSocket.accept();
            if (clientSocket.isConnected()) {
                out = new DataOutputStream(clientSocket.getOutputStream());
                in = new DataInputStream(clientSocket.getInputStream());
            }
        }

    }

    public void sendMessage(String msg) throws IOException {
        out.writeUTF(msg);
    }

    public Server() {
    }

}