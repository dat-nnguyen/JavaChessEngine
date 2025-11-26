package gui;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class SoundManager {

    private static MediaPlayer musicPlayer;
    private static AudioClip clickSound;

    static {
        try {
            // Load background music
            URL musicUrl = SoundManager.class.getResource("/assets/sound/music.wav");
            if (musicUrl != null) {
                Media media = new Media(musicUrl.toExternalForm());
                musicPlayer = new MediaPlayer(media);
                musicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop
                musicPlayer.setVolume(0.5);
            } else {
                System.err.println("Music file not found!");
            }

            // Load click sound
            URL clickUrl = SoundManager.class.getResource("/assets/sound/click.wav");
            if (clickUrl != null) {
                clickSound = new AudioClip(clickUrl.toExternalForm());
                clickSound.setVolume(1.0);
            } else {
                System.err.println("Click sound not found!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setMusicMuted(boolean muted) {
        if (musicPlayer != null) {
            musicPlayer.setMute(muted);
        }
    }

    public static void playMusic() {
        if (musicPlayer != null) {
            musicPlayer.play();
        }
    }

    public static void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    public static void playClick() {
        if (clickSound != null) {
            clickSound.play();
        }
    }
}
