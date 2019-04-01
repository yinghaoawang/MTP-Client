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
        out.writeUTF(msg);
        return 1;
    }

    public static void main(String args[]) throws Exception {
        startConnection("localhost", 1234);
        System.out.println("Connected to " + clientSocket.getInetAddress());

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


    }
}