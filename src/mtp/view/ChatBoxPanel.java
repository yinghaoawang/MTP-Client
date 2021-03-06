package mtp.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

public class ChatBoxPanel extends JPanel {
    private JTextPane messageField;
    private JTextArea outField;
    private JScrollPane scrollPane;
    private JButton attachmentButton;

    public ChatBoxPanel() {
        Border textBoxBorder = new LineBorder(Color.black, 1);

        setSize(600, 450);
        setBorder(textBoxBorder);

        setLayout(new GridBagLayout());
        messageField = new JTextPane();
        messageField.setContentType("text/html");
        messageField.setEditorKit(new HTMLEditorKit());
        messageField.setDocument(new HTMLDocument());
        //messageField.setRows(10);
        //messageField.setColumns(6);
        messageField.setSize(new Dimension(500, 300));
        messageField.setBorder(textBoxBorder);
        messageField.setEditable(false);
        //messageField.setLineWrap(true);

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

        GridBagConstraints c = new GridBagConstraints();
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
        messageField.setCaretPosition(messageField.getDocument().getLength());
    }

    public JTextArea getOutField() {
        return outField;
    }

    public JButton getAttachmentButton() { return attachmentButton; }

    public void addLine(String msg) {
        try {
            HTMLDocument doc = (HTMLDocument)messageField.getDocument();
            HTMLEditorKit kit = (HTMLEditorKit)messageField.getEditorKit();
            doc.insertString(doc.getLength(), msg + "\n", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addImage(String imagePath, int width, int height) {
        try {
            String src = "file:" + imagePath;
            System.out.println(src);

            HTMLDocument doc = (HTMLDocument)messageField.getDocument();
            HTMLEditorKit kit = (HTMLEditorKit)messageField.getEditorKit();

            kit.insertHTML(doc, doc.getLength(), "<img src=\"" + src + "\" width=\"" + width + "\" height= \"" + height + "\" />", 0, 0, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
