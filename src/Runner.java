import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Runner {
    public static ChatBoxPanel chatBox;
    public static JFrame frame;
    public static Client client;
    public static Server server;
    public static void main(String[] args) {
        frame = new JFrame();
        frame.setSize(1000, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        frame.setTitle("MTP Client");

        JButton clientButton = new JButton("Client");
        JButton serverButton = new JButton("Server");

        clientButton.setSize(100, 75);
        serverButton.setSize(100, 75);

        frame.add(clientButton);
        frame.add(serverButton);
        frame.setVisible(true);

        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sceneTwo(clientButton, serverButton);
                frame.setTitle("Client");

                client = new Client();
                String inetAddress = "localhost";
                int port = 1234;

                // Client tries to connect to the specified address and port
                clientSearchForServer(inetAddress, port, 3);
            }
        });

        serverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sceneTwo(clientButton, serverButton);
                frame.setTitle("Server");

                server = new Server();
                int port = 1234;

                // Creates the server at the specified port then looks for a connection
                serverWaitForClient(port);
            }
        });
    }

    // Creates the server at port, waits for a client connection, if found then functionality added to the chatbox
    public static void serverWaitForClient(int port) {
        // creates server
        try {
            server.createServer(port);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Waits for a connection from a client by using a thread since await connection is blocking
        chatBox.addLine("Awaiting connection on port " + port);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // looks for a client socket to connect, blocks until a connection is found
                    server.awaitConnection();
                    if (server.clientSocket == null) {
                        throw new IOException("Client connection found but client socket does not exist.");
                    }
                    chatBox.addLine(server.clientSocket.getInetAddress() + " has connected");

                    // adds functionality to the text area for sencind messages
                    chatBox.getOutField().addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            super.keyPressed(e);
                            keyPressedFunction(e, server.out, "Server");
                        }
                    });

                    // adds a new thread that looks for input from the socket to display on the chatbox
                    Thread readThread = new Thread() {
                        @Override
                        public void run() {
                            readThreadFunction(server.in);
                        }
                    };
                    readThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    // Client tries to connect to a server socket, and if it does, it gives functionality to the chat box
    public static void clientSearchForServer(String inetAddress, int port, int seconds) {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

        chatBox.addLine("Connecting to /" + inetAddress + ":" + port);
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    // client tries to connect to the specified address and port
                    client.startConnection(inetAddress, port);
                    chatBox.addLine("Connected to " + client.clientSocket.getInetAddress());

                    // stops trying to connect to server, since we have connected already
                    exec.shutdown();

                    // adds functionality to the box which sends messages (pressing enter sends messages)
                    chatBox.getOutField().addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            super.keyPressed(e);
                            keyPressedFunction(e, client.out, "Client");
                        }
                    });

                    // creates and runs thread that handles reading input from the socket
                    Thread readThread = new Thread() {
                        @Override
                        public void run() {
                            readThreadFunction(client.in);
                        }
                    };

                    readThread.start();

                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }, 0, seconds, TimeUnit.SECONDS);

    }

    // The function that constantly checks any input has been sent to the socket (should be in a thread)
    public static void readThreadFunction(DataInputStream in) {
        while (true) {
            try {
                String inputLine = in.readUTF();
                if (inputLine != null) {
                    System.out.println(inputLine);
                    chatBox.addLine(inputLine);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    // The function for sending a message for client and server
    public static void keyPressedFunction(KeyEvent e, DataOutputStream out, String username) {
        if (e.getKeyChar() == '\n') {
            e.consume();
            try {
                String msg = chatBox.getOutField().getText();
                if (msg.trim() == "") return;
                msg = username + ": " + msg;
                out.writeUTF(msg);
                chatBox.addLine(msg);
                chatBox.getOutField().setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // Transition from two button scene to the chatbox scene after selection (only gui elements)
    public static void sceneTwo(JButton clientButton, JButton serverButton) {
        frame.setLayout(null);
        frame.remove(clientButton);
        frame.remove(serverButton);
        chatBox = new ChatBoxPanel();
        frame.add(chatBox);
        frame.repaint();
        frame.setVisible(true);
    }
}
