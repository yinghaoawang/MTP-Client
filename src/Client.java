import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    private Socket clientSocket;
    public DataOutputStream out;
    public DataInputStream in;
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