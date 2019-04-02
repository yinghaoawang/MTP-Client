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
    public void startConnection(String ip, int port) throws Exception {
        clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress(ip, port), 5000);
        if (clientSocket.isConnected()) {
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
        }
    }

    public int sendMessage(String msg) throws IOException {
        out.writeUTF(msg);
        return 1;
    }

    public Client() {


        /*
        Thread writeThread = new Thread() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                while(true) {
                    try {
                        String msg = scanner.nextLine();
                        sendMessage(user + ": " + msg);
                        System.out.println(user + ": " + msg);
                    } catch (Exception e) {
                        System.out.println("Couldn't send message.");
                    }
                }
            }
        };

        Thread readThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String inputLine = in.readUTF();
                        if (in != null) {
                            System.out.println("Server: " + inputLine);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        readThread.start();
        writeThread.start();
        */

    }
}