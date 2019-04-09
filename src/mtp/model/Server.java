package mtp.model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Socket getClientSocket() {
        return clientSocket;
    }

    private Socket clientSocket;

    public DataOutputStream getOutputStream() {
        return out;
    }

    public DataInputStream getInputStream() {
        return in;
    }

    DataOutputStream out;
    DataInputStream in;

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

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

}