package mtp.view;

import javax.swing.*;
import java.awt.*;

public class MTPView {
    private static ChatBoxPanel chatBox;
    private static JFrame frame;
    private JPanel addressPanel;
    private JPanel portPanel;
    private JPanel selectionPanel;


    public JTextField getPortTextField() {
        return portTextField;
    }

    private JTextField portTextField;

    public JTextField getClientIPTextField() {
        return clientIPTextField;
    }

    private JTextField clientIPTextField;
    private JButton clientButton;
    private JButton serverButton;

    public MTPView() {

    }

    public void init() {
        frame = new JFrame();
        frame.setSize(610, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.setTitle("Client");

        addressPanel = new JPanel();
        addressPanel.setLayout(new FlowLayout());
        clientIPTextField = new JTextField();
        clientIPTextField.setColumns(10);
        addressPanel.add(new JLabel("IP: "));
        addressPanel.add(clientIPTextField);

        portPanel = new JPanel();
        portPanel.setLayout(new FlowLayout());
        portTextField = new JTextField();
        portTextField.setColumns(10);
        portPanel.add(new JLabel("Port: "));
        portPanel.add(portTextField);

        selectionPanel = new JPanel();
        selectionPanel.setLayout(new FlowLayout());
        clientButton = new JButton("Client");
        serverButton = new JButton("Server");
        clientButton.setSize(100, 75);
        serverButton.setSize(100, 75);
        selectionPanel.add(clientButton);
        selectionPanel.add(serverButton);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0;
        c.gridy = 0;
        frame.add(addressPanel, c);

        c.gridy = 1;
        frame.add(portPanel, c);

        c.gridy = 2;
        frame.add(selectionPanel, c);

        frame.setVisible(true);
    }

    // Transition from two button scene to the chatbox scene after selection (only gui elements)
    public void loadChatBoxScene() {
        frame.setLayout(null);
        frame.remove(selectionPanel);
        frame.remove(portPanel);
        frame.remove(addressPanel);
        chatBox = new ChatBoxPanel();
        frame.add(chatBox);
        frame.repaint();
        frame.setVisible(true);
    }

    /** GETTERS AND SETTERS **/
    public ChatBoxPanel getChatBox() {
        return chatBox;
    }

    public JFrame getFrame() {
        return frame;
    }

    public JButton getClientButton() {
        return clientButton;
    }

    public JButton getServerButton() {
        return serverButton;
    }
}
