package mtp.controller;

import mtp.SoundManager;
import mtp.model.*;
import mtp.view.MTPView;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MTPController {
    private MTPView view;
    private MTPModel model;

    public final String[] supportedImageFormats = new String[] {
            ".jpeg", ".jpg", ".png", ".bmp", ".gif"
    };

    public MTPController(MTPView view, MTPModel model) {
        this.view = view;
        this.model = model;
    }

    public void init() {
        view.init();
        view.getClientIPTextField().setText(model.getClientIP());
        view.getPortTextField().setText(model.getPort() + "");

        updateViewWithConf(".conf.txt");
        addListeners();
    }

    private void addListeners() {
        view.getClientButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.loadChatBoxScene();
                view.getFrame().setTitle("Client");

                String clientIPText = view.getClientIPTextField().getText();
                String portText = view.getPortTextField().getText();

                model.setClientIP(clientIPText);
                model.setPort(Integer.parseInt(portText));
                model.createClient();

                writeNewConf(new String[] { clientIPText, portText }, ".conf.txt");

                // Client tries to connect to the specified address and port
                clientSearchForServer(3);
            }
        });

        view.getServerButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.loadChatBoxScene();
                view.getFrame().setTitle("Server");

                String clientIPText = view.getClientIPTextField().getText();
                String portText = view.getPortTextField().getText();

                model.setClientIP(clientIPText);
                model.setPort(Integer.parseInt(portText));

                // Creates the server at the specified port then looks for a connection
                try {
                    model.createServer();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }

                writeNewConf(new String[] { clientIPText, portText }, ".conf.txt");

                serverWaitForClient();
            }
        });
    }

    // Server waits for a client connection, if found then functionality added to the chatbox
    public void serverWaitForClient() {
        // Waits for a connection from a client by using a thread since await connection is blocking
        view.getChatBox().addLine("Awaiting connection on port " + model.getPort());
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Server server = model.getServer();
                    // looks for a client socket to connect, blocks until a connection is found
                    server.awaitConnection();
                    if (server.getClientSocket() == null) {
                        throw new IOException("Client connection found but client socket does not exist.");
                    }
                    view.getChatBox().addLine(server.getClientSocket().getInetAddress() + " has connected");

                    // adds functionality to the text area for sencind messages
                    view.getChatBox().getOutField().addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            super.keyPressed(e);
                            keyPressedFunction(e, server.getOutputStream(), "Server");
                        }
                    });

                    view.getChatBox().getAttachmentButton().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            getAndSendMedia("Server", server.getOutputStream());
                        }
                    });

                    // adds a new thread that looks for input from the socket to display on the chatbox
                    Thread readThread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                readThreadFunction(server.getInputStream());
                            } catch (InterruptedException ex) {
                                // if the client disconnects, remove functionality and look for new client
                                view.getChatBox().addLine(server.getClientSocket().getInetAddress() + " has disconnected.");
                                view.getChatBox().getOutField().removeKeyListener(view.getChatBox().getOutField().getKeyListeners()[0]);
                                view.getChatBox().getAttachmentButton().removeActionListener(view.getChatBox().getAttachmentButton().getActionListeners()[0]);
                                serverWaitForClient();
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
    public void clientSearchForServer(int seconds) {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

        view.getChatBox().addLine("Connecting to /" + model.getClientIP() + ":" + model.getPort());
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    Client client = model.getClient();
                    // client tries to connect to the specified address and port
                    client.startConnection(model.getClientIP(), model.getPort());
                    view.getChatBox().addLine("Connected to " + model.getClientIP());

                    // stops trying to connect to server, since we have connected already
                    exec.shutdown();

                    // adds functionality to the box which sends messages (pressing enter sends messages)
                    view.getChatBox().getOutField().addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            super.keyPressed(e);
                            keyPressedFunction(e, client.getOutputStream(), "Client");
                        }
                    });

                    view.getChatBox().getAttachmentButton().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            getAndSendMedia("Client", client.getOutputStream());
                        }
                    });

                    // creates and runs thread that handles reading input from the socket
                    Thread readThread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                readThreadFunction(client.getInputStream());
                            } catch (InterruptedException ex) {
                                // if the server disconnects, remove functionality and look for server at same ip & port
                                view.getChatBox().addLine(model.getClientIP() + " has disconnected.");
                                view.getChatBox().getOutField().removeKeyListener(view.getChatBox().getOutField().getKeyListeners()[0]);
                                view.getChatBox().getAttachmentButton().removeActionListener(view.getChatBox().getAttachmentButton().getActionListeners()[0]);
                                clientSearchForServer(seconds);
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

    public void getAndSendMedia(String username, DataOutputStream out) {
        JFileChooser fc = new JFileChooser("./");
        int fcReturn = fc.showOpenDialog(view.getFrame());
        if (fcReturn == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();
                String fileName = file.getName();

                String extension = file.getName().substring(fileName.lastIndexOf('.'));
                if (Arrays.asList(supportedImageFormats).contains(extension)) {
                    // PRETTY BAD CODE HUH
                } else if (extension.equals(".wav")) {
                } else {
                    view.getChatBox().addLine("Unsupported file extension: " + extension);
                    return;
                }
                out.writeUTF(extension);
                byte[] buffer = Files.readAllBytes(Paths.get(file.getPath()));
                out.writeLong(buffer.length);
                out.write(buffer, 0, buffer.length);
                String sendMessage = username + " sent " + fileName;
                view.getChatBox().addLine(sendMessage);
                out.writeUTF(sendMessage);
                if (Arrays.asList(supportedImageFormats).contains(extension)) {
                    view.getChatBox().addImage(file.getAbsolutePath(), 200, 200);
                }
                SoundManager.playSound("res/msg.wav");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // The function that constantly checks any input has been sent to the socket (should be in a thread)
    public void readThreadFunction(DataInputStream in) throws InterruptedException {
        while (true) {
            try {
                String inputLine = in.readUTF();
                if (inputLine != null) {
                    if (inputLine.equals("text")) {
                        String contentLine = in.readUTF();
                        addLineToChatBox(contentLine);
                    } else {
                        long bufferLen = in.readLong();
                        byte[] buffer = new byte[(int) bufferLen];
                        in.readFully(buffer, 0, (int) bufferLen);
                        String contentLine = in.readUTF();
                        File temp = File.createTempFile("tmp", inputLine);
                        temp.deleteOnExit();
                        FileOutputStream os = new FileOutputStream(temp);
                        os.write(buffer);
                        os.flush();
                        os.close();

                        addLineToChatBox(contentLine);

                        if (inputLine.equals(".wav")) {
                            SoundManager.playSound(temp.getAbsolutePath());
                            Clip clip = SoundManager.currClip;
                            clip.addLineListener(new LineListener() {
                                @Override
                                public void update(LineEvent event) {
                                    if (event.getType() == LineEvent.Type.STOP) {
                                        clip.close();
                                    }
                                }
                            });
                        } else if (Arrays.asList(supportedImageFormats).contains(inputLine)) {
                            view.getChatBox().addImage(temp.getAbsolutePath(), 200, 200);
                        }

                    }
                }
            } catch (IOException e) {
                throw new InterruptedException();
            }

        }
    }

    // The function for sending a message for client and server
    public void keyPressedFunction(KeyEvent e, DataOutputStream out, String username) {
        if (e.getKeyChar() == '\n') {
            e.consume();
            try {
                String msg = view.getChatBox().getOutField().getText();
                if (msg.trim() == "") return;
                out.writeUTF("text");
                sendMessage(username, msg, out);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void addLineToChatBox(String msg) {
        view.getChatBox().addLine(msg);
        view.getChatBox().scrollLast();
        SoundManager.playSound("res/msg.wav");
    }

    public void sendMessage(String username, String msg, DataOutputStream out) throws IOException {
        msg = username + ": " + msg;
        out.writeUTF(msg);

        addLineToChatBox(msg);
        view.getChatBox().getOutField().setText("");
    }

    private void updateViewWithConf(String configPath) {
        File f = new File(configPath);
        if (f.isFile() && f.canRead()) {
            FileInputStream fin = null;
            BufferedReader br = null;
            try {
                fin = new FileInputStream(f);
                br = new BufferedReader(new InputStreamReader(fin));
                String line;
                if ((line = br.readLine().trim()).length() > 0) {
                    view.getClientIPTextField().setText(line);
                }
                if ((line = br.readLine().trim()).length() > 0) {
                    view.getPortTextField().setText(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fin.close();
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // creates a new config file that keeps track of data
    public void writeNewConf(String[] lines, String configPath) {
        try {
            FileWriter fw = new FileWriter(configPath, false);
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
