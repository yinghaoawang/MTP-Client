import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ChatBoxPanel extends JPanel {

    private JTextArea messageField;
    private JTextArea outField;
    private JScrollPane scrollPane;

    public ChatBoxPanel() {
        Border textBoxBorder = new LineBorder(Color.black, 1);

        setSize(600, 400);
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
        outField.setRows(2);
        outField.setColumns(6);
        outField.setPreferredSize(new Dimension(500, 20));
        outField.setBorder(textBoxBorder);
        outField.setLineWrap(true);

        c.insets = new Insets(10, 10, 10, 10);

        c.gridx = 1;
        c.gridy = 0;
        add(scrollPane, c);

        c.gridx = 1;
        c.gridy = 1;
        add(outField, c);
    }

    public JTextArea getOutField() {
        return outField;
    }

    public void addLine(String msg) {
        messageField.setText(messageField.getText() + msg + "\n");

    }
}
