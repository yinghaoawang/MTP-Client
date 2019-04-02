import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;
import java.util.Timer;

public class Client {
    public Socket clientSocket;
    public DataOutputStream out;
    public DataInputStream in;
    public String user = "Client";
    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress(ip, port), 5000);
        if (clientSocket.isConnected()) {
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
        }
    }

    public void sendMessage(String msg) throws IOException {
        out.writeUTF(msg);
    }

    public Client() {
    }
}