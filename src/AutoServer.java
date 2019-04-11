import mtp.MTPClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class AutoServer {
    public static void main(String[] args) {
        MTPClient mtpClient = new MTPClient();
        mtpClient.init();

        if (SystemTray.isSupported()) {
            try {
                JFrame frame = mtpClient.getFrame();
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                SystemTray tray = SystemTray.getSystemTray();
                Image image = Toolkit.getDefaultToolkit().getImage("/res/tray.png");
                PopupMenu popup = new PopupMenu("hey");
                TrayIcon trayIcon = new TrayIcon(image, "MTP Client", popup);
                trayIcon.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        restoreFrame(frame, tray, trayIcon);
                    }
                });

                MenuItem popupClose = new MenuItem("Close");
                popupClose.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                    }
                });
                MenuItem popupRestore = new MenuItem("Restore");
                popupRestore.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        restoreFrame(frame, tray, trayIcon);
                    }
                });
                popup.add(popupRestore);
                popup.add(popupClose);

                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        if (frame.getDefaultCloseOperation() != JFrame.DO_NOTHING_ON_CLOSE) return;
                        minimizeFrame(frame, tray, trayIcon);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mtpClient.forceInitServer();
    }

    public static void restoreFrame(JFrame frame, SystemTray tray, TrayIcon trayIcon) {
        frame.setVisible(true);
        try {
            tray.remove(trayIcon);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void minimizeFrame(JFrame frame, SystemTray tray, TrayIcon trayIcon) {
        try {
            tray.add(trayIcon);
            frame.setVisible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}