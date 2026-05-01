package com.hiraeth.Managers;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class MusicManager {

    private Clip bgm;
    private float volume = -10.0f;
    private float sound = -5.0f;

    public void playBGM(String soundFile) {

       stopBGM();

        try {

            InputStream is = getClass().getResourceAsStream("/BGM/" + soundFile);
            if (is == null) return;

                AudioInputStream sourceStream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
                AudioFormat baseFormat = sourceStream.getFormat();
                AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(), 16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2, 
                    baseFormat.getSampleRate(), false
                );

                AudioInputStream decodedStream = AudioSystem.getAudioInputStream(targetFormat, sourceStream);
                bgm = AudioSystem.getClip();
                bgm.open(decodedStream);

                FloatControl gainControl = (FloatControl) bgm.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(volume);

                bgm.loop(Clip.LOOP_CONTINUOUSLY);
                bgm.start();
            
        } catch (Exception e) {

            System.out.println("Error accessing BGM" + e.getMessage());
        }
    }

    public void stopBGM() {

        if (bgm != null && bgm.isRunning()) {
            bgm.stop();
            bgm.close();
        }
    }

    public void playSound(String soundFile) {

        try {

            InputStream is = getClass().getResourceAsStream("/Sound_Effects/" + soundFile);
            if (is == null) return;

            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(sound);
            clip.start();;

            clip.addLineListener (e -> {

                if (e.getType() == LineEvent.Type.STOP) {

                    clip.close();
                }
            });
        } catch (Exception e) {

            System.out.println("Error accessing SFX: " + e.getMessage());
        }
    }

    public void updateMusic(String fileName) {

        switch (fileName) {

            case "intro.json":

                playBGM("Far Away(intro).wav");
                break;
            
            case "Fifteen_Years_Later.json":

                playBGM("Relax(kitchen).wav");
                break;
            default:

                System.out.println("No music assigned for: " + fileName);
                break;
        }
    }
}