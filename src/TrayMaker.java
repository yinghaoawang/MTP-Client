import mtp.MTPClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TrayMaker {
    public TrayMaker(JFrame frame) {
    if (SystemTray.isSupported()) {
        try {
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage("res/tray.png");
            frame.setIconImage(image);
            PopupMenu popup = new PopupMenu();
            TrayIcon trayIcon = new TrayIcon(image, "MTP Client", popup);
            trayIcon.setImageAutoSize(true);
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
                    if (frame.getDefaultCloseOperation() == JFrame.DO_NOTHING_ON_CLOSE) {
                        minimizeFrame(frame, tray, trayIcon);
                    } else if (frame.getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE) {
                        tray.remove(trayIcon);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

    private void restoreFrame(JFrame frame, SystemTray tray, TrayIcon trayIcon) {
        frame.setVisible(true);
        try {
            tray.remove(trayIcon);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void minimizeFrame(JFrame frame, SystemTray tray, TrayIcon trayIcon) {
        try {
            tray.add(trayIcon);
            frame.setVisible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
