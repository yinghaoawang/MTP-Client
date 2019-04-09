package mtp;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    public static Clip currClip;
    private static AudioInputStream audioIn;

    public static void playSound(String fileName) {
        File file = new File(fileName);
        playSound(file);
    }

    public static void playSound(byte[] bytes) {
        try {
            audioIn = AudioSystem.getAudioInputStream(new ByteArrayInputStream(bytes));
            playSound();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playSound(File file) {
        try {
             audioIn = AudioSystem.getAudioInputStream(file);
            playSound();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void playSound() {
        try {
            currClip = AudioSystem.getClip();
            currClip.open(audioIn);
            audioIn.close();

            FloatControl gainControl = (FloatControl) currClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-20f);
            currClip.start();
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}