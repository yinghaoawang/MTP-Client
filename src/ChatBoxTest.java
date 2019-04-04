import javax.swing.*;
import java.awt.*;

public class ChatBoxTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        ChatBoxPanel chatBox = new ChatBoxPanel();
        frame.add(chatBox);
        frame.setVisible(true);    }
}
