import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Runner {
    public static ChatBoxPanel chatBox;
    public static JFrame frame;
    public static JPanel[] panels;
    public static Client client;
    public static Server server;

    public static void main(String[] args) {
        frame = new JFrame();
        frame.setSize(610, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        frame.setTitle("MTP Client");

        String clientIpString = "127.0.0.1";
        String portString = "1234";
        File f = new File(".conf.txt");
        if (f.isFile() && f.canRead()) {
            try {
                FileInputStream fin = new FileInputStream(f);
                BufferedReader br = new BufferedReader(new InputStreamReader(fin));
                String line;
                if ((line = br.readLine().trim()).length() > 0) {
                    clientIpString = line;
                }
                if ((line = br.readLine().trim()).length() > 0) {
                    portString = line;
                }
                br.close();
                fin.close();
            } catch (Exception e) {
            }
        }

        JPanel addressPanel = new JPanel();
        addressPanel.setLayout(new FlowLayout());
        JTextField clientIPText = new JTextField(clientIpString);
        clientIPText.setColumns(10);
        addressPanel.add(new JLabel("IP: "));
        addressPanel.add(clientIPText);

        JPanel portPanel = new JPanel();
        portPanel.setLayout(new FlowLayout());
        JTextField portText = new JTextField(portString);
        portText.setColumns(10);
        portPanel.add(new JLabel("Port: "));
        portPanel.add(portText);

        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new FlowLayout());
        JButton clientButton = new JButton("Client");
        JButton serverButton = new JButton("Server");
        clientButton.setSize(100, 75);
        serverButton.setSize(100, 75);
        selectionPanel.add(clientButton);
        selectionPanel.add(serverButton);

        panels = new JPanel[] {
            addressPanel, portPanel, selectionPanel
        };

        for (int i = 0; i < panels.length; ++i) {
            c.anchor = GridBagConstraints.EAST;
            c.gridx = 0;
            c.gridy = i;
            frame.add(panels[i], c);
        }

        frame.setVisible(true);

        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sceneTwo();
                frame.setTitle("Client");

                client = new Client();
                String inetAddress = clientIPText.getText();
                int port = Integer.parseInt(portText.getText());
                writeNewConf(new String[] {inetAddress, portText.getText()});

                // Client tries to connect to the specified address and port
                clientSearchForServer(inetAddress, port, 3);
            }
        });

        serverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sceneTwo();

                frame.setTitle("Server");

                server = new Server();
                int port = Integer.parseInt(portText.getText());
                writeNewConf(new String[] {clientIPText.getText(), port + ""});

                // Creates the server at the specified port then looks for a connection
                try {
                    server.createServer(port);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                serverWaitForClient(port);
            }
        });
    }

    // Server waits for a client connection, if found then functionality added to the chatbox
    public static void serverWaitForClient(int port) {
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

                    chatBox.getAttachmentButton().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            getAndSendMedia("Server", server.out);
                        }
                    });

                    // adds a new thread that looks for input from the socket to display on the chatbox
                    Thread readThread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                readThreadFunction(server.in);
                            } catch (InterruptedException ex) {
                                // if the client disconnects, remove functionality and look for new client
                                chatBox.addLine(server.clientSocket.getInetAddress() + " has disconnected.");
                                chatBox.getOutField().removeKeyListener(chatBox.getOutField().getKeyListeners()[0]);
                                chatBox.getAttachmentButton().removeActionListener(chatBox.getAttachmentButton().getActionListeners()[0]);
                                serverWaitForClient(port);
                            }
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

                    chatBox.getAttachmentButton().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            getAndSendMedia("Client", client.out);
                        }
                    });

                    // creates and runs thread that handles reading input from the socket
                    Thread readThread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                readThreadFunction(client.in);
                            } catch (InterruptedException ex) {
                                // if the server disconnects, remove functionality and look for server at same ip & port
                                chatBox.addLine(client.clientSocket.getInetAddress() + " has disconnected.");
                                chatBox.getOutField().removeKeyListener(chatBox.getOutField().getKeyListeners()[0]);
                                chatBox.getAttachmentButton().removeActionListener(chatBox.getAttachmentButton().getActionListeners()[0]);
                                clientSearchForServer(inetAddress, port, seconds);
                            }
                        }
                    };
                    readThread.start();

                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }, 0, seconds, TimeUnit.SECONDS);

    }

    public static void getAndSendMedia(String username, DataOutputStream out) {
        JFileChooser fc = new JFileChooser("./");
        int fcReturn = fc.showOpenDialog(frame);
        if (fcReturn == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();
                String fileName = file.getName();

                String extension = file.getName().substring(fileName.lastIndexOf('.'));
                out.writeUTF(extension);
                byte[] buffer = Files.readAllBytes(Paths.get(file.getPath()));
                out.writeLong(buffer.length);
                out.write(buffer, 0, buffer.length);
                out.writeUTF(username + " sent " + fileName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static final String[] imageFormats = new String[] {
        ".jpeg", ".jpg", ".png", ".bmp", ".gif"
    };

    // The function that constantly checks any input has been sent to the socket (should be in a thread)
    public static void readThreadFunction(DataInputStream in) throws InterruptedException {
        while (true) {
            try {
                String inputLine = in.readUTF();
                if (inputLine != null) {
                    if (inputLine.equals("text")) {
                        String contentLine = in.readUTF();
                        chatBox.addLine(contentLine);
                        chatBox.scrollLast();
                        SoundManager.playSound("res/msg.wav");
                    } else if (inputLine.equals(".wav")) {
                        long bufferLen = in.readLong();
                        byte[] buffer = new byte[(int)bufferLen];
                        in.readFully(buffer, 0, (int)bufferLen);
                        String contentLine = in.readUTF();
                        chatBox.addLine(contentLine);

                        SoundManager.playSound(buffer);
                        Clip clip = SoundManager.currClip;
                        clip.addLineListener(new LineListener() {
                            @Override
                            public void update(LineEvent event) {
                                if (event.getType() == LineEvent.Type.STOP) {
                                    clip.close();
                                }
                            }
                        });
                    } else if (Arrays.asList(imageFormats).contains(inputLine)) {
                        long bufferLen = in.readLong();
                        byte[] buffer = new byte[(int)bufferLen];
                        in.readFully(buffer, 0, (int)bufferLen);
                        String contentLine = in.readUTF();
                        chatBox.addLine(contentLine);
                        File temp = File.createTempFile("tmp", inputLine);
                        temp.deleteOnExit();
                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(buffer));
                        ImageIO.write(img, inputLine.substring(1), temp);
                        chatBox.addImage(temp.getAbsolutePath(), 200, 200);
                        SoundManager.playSound("res/msg.wav");
                    }

                    System.out.println(inputLine);

                }
            } catch (IOException e) {
                throw new InterruptedException();
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
                out.writeUTF("text");
                sendMessage(username, msg, out);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void sendMessage(String username, String msg, DataOutputStream out) throws IOException {
        msg = username + ": " + msg;
        out.writeUTF(msg);
        chatBox.addLine(msg);
        chatBox.getOutField().setText("");
        chatBox.scrollLast();
        SoundManager.playSound("res/msg.wav");
    }

    // Transition from two button scene to the chatbox scene after selection (only gui elements)
    public static void sceneTwo() {
        frame.setLayout(null);
        for (JPanel panel : panels) {
            frame.remove(panel);
        }
        chatBox = new ChatBoxPanel();
        frame.add(chatBox);
        frame.repaint();
        frame.setVisible(true);
    }

    // creates a new config file that keeps track of data
    public static void writeNewConf(String[] lines) {
        try {
            FileWriter fw = new FileWriter(".conf.txt", false);
            for (String line : lines) {
                fw.write(line + "\n");
            }
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
