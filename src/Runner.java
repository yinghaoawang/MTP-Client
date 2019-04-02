import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Runner {
    public static ChatBoxPanel chatBox;
    public static JFrame frame;
    public static void main(String[] args) {
        frame = new JFrame();
        frame.setSize(1000, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JButton clientButton = new JButton("Client");
        JButton serverButton = new JButton("Server");

        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sceneTwo(clientButton, serverButton);
                Client client = new Client();

                String inetAddress = "localhost";
                int port = 1234;

                ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

                chatBox.addLine("Connecting to /" + inetAddress + ":" + port);
                exec.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            client.startConnection(inetAddress, port);
                            chatBox.addLine("Connected to " + client.clientSocket.getInetAddress());

                            exec.shutdown();

                            Thread readThread = new Thread() {
                                @Override
                                public void run() {
                                    while (true) {
                                        try {
                                            String inputLine = client.in.readUTF();
                                            if (inputLine != null) {
                                                System.out.println(inputLine);
                                                chatBox.addLine(inputLine);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            };

                            readThread.start();

                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                            chatBox.addLine("Connecting...");
                        }
                    }
                }, 0, 5, TimeUnit.SECONDS);

            }
        });
        serverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sceneTwo(clientButton, serverButton);

                Server server = new Server();
                int port = 1234;

                try {
                    server.createServer(port);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                //ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

                chatBox.addLine("Awaiting connection on port " + port);
                Thread thread = new Thread() {
                    @Override
                    public void run() {

                        try {
                            server.awaitConnection();


                            if (server.clientSocket != null) {
                                chatBox.addLine(server.clientSocket.getInetAddress() + " has connected");

                                Thread readThread = new Thread() {
                                    @Override
                                    public void run() {
                                        while (true) {
                                            try {
                                                String inputLine = server.in.readUTF();
                                                if (inputLine != null) {
                                                    System.out.println(inputLine);
                                                    chatBox.addLine(inputLine);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }
                                };

                                readThread.start();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        });

        clientButton.setSize(100, 75);
        serverButton.setSize(100, 75);

        frame.add(clientButton);
        frame.add(serverButton);
        frame.setVisible(true);
    }

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
