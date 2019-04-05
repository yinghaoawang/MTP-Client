import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ChatBoxPanel extends JPanel {

    private JTextArea messageField;
    private JTextArea outField;
    private JScrollPane scrollPane;
    private JButton attachmentButton;

    public ChatBoxPanel() {
        Border textBoxBorder = new LineBorder(Color.black, 1);

        setSize(600, 450);
        setBorder(textBoxBorder);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        messageField = new JTextArea();
        messageField.setRows(10);
        messageField.setColumns(6);
        messageField.setSize(new Dimension(500, 300));
        messageField.setBorder(textBoxBorder);
        messageField.setEditable(false);
        messageField.setLineWrap(true);

        scrollPane = new JScrollPane(messageField);
        scrollPane.setPreferredSize(messageField.getSize());

        outField = new JTextArea();
        outField.setRows(3);
        outField.setColumns(6);
        outField.setPreferredSize(new Dimension(500, 20));
        outField.setBorder(textBoxBorder);
        outField.setLineWrap(true);

        attachmentButton = new JButton("+");
        attachmentButton.setPreferredSize(new Dimension(40, 20));
        attachmentButton.setFont(new Font("Times New Roman", Font.PLAIN, 10));

        JPanel outFieldPanel = new JPanel();
        outFieldPanel.setLayout(new GridBagLayout());
        GridBagConstraints ofc = new GridBagConstraints();

        ofc.anchor = GridBagConstraints.WEST;
        outFieldPanel.add(attachmentButton, ofc);
        ofc.gridy = 1;
        ofc.insets = new Insets(3, 0, 0, 0);
        outFieldPanel.add(outField, ofc);

        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.NORTH;

        c.gridx = 1;
        c.gridy = 0;
        add(scrollPane, c);



        c.gridx = 1;
        c.gridy = 1;
        add(outFieldPanel, c);
    }

    public void scrollLast() {
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    public JTextArea getOutField() {
        return outField;
    }

    public JButton getAttachmentButton() { return attachmentButton; }

    public void addLine(String msg) {
        messageField.append(msg + "\n");

    }
}
