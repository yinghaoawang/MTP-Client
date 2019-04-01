import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String args[]) throws Exception {
        // Creates a server socket
        ServerSocket serverSocket = new ServerSocket(1234);

        // Waits for a client to connect
        Socket clientSocket = serverSocket.accept();
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
        DataInputStream in = new DataInputStream(clientSocket.getInputStream());

        if (clientSocket.isConnected()) {
            String msg = clientSocket.getInetAddress() + " has connected.";
            System.out.println(msg);
            out.writeUTF(msg);
        }
        Thread readThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String inputLine = in.readUTF();
                        if (inputLine != null) {
                            System.out.println(inputLine);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        Thread writeThread = new Thread() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    try {
                        String inputLine = scanner.nextLine();
                        if (inputLine != null) {
                            out.writeUTF("Server: " + inputLine);
                            System.out.println(inputLine);
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