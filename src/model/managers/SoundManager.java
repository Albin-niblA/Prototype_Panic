package model.managers;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class SoundManager {
    private static AudioClip shootSound;
    private static AudioClip arrowSound;
    private static AudioClip rocketSound;
    private static AudioClip hitSound;
    private static AudioClip deathSound;
    private static AudioClip teleportSound;
    private static boolean initialized = false;
    private static double volume = 1.0;
    private static MediaPlayer menuMusic;
    private static boolean musicPlaying = false;

    public static void init() {
        if (initialized) return;
        //weapon sounds
        shootSound = loadClip("/util/sounds/shoot.wav");
        arrowSound = loadClip("/util/sounds/arrow.wav");
        rocketSound = loadClip("/util/sounds/rocket.wav");
        // Ready for when sound files are added:
        hitSound = loadClip("/util/sounds/taking_damage.wav");
        // deathSound = loadClip("/util/sounds/death.wav");
        teleportSound = loadClip("/util/sounds/teleport.wav");

        initialized = true;
    }

    private static AudioClip loadClip(String path) {
        var url = SoundManager.class.getResource(path);
        return url != null ? new AudioClip(url.toExternalForm()) : null;
    }

    public static void setVolume(double v) {
        volume = Math.max(0.0, Math.min(1.0, v));
        if (shootSound != null) shootSound.setVolume(volume);
        if (hitSound   != null) hitSound.setVolume(volume);
        if (menuMusic   != null) menuMusic.setVolume(volume);
    }

    public static double getVolume() {
        return volume;
    }

    public static void playShoot() { if (shootSound != null) shootSound.play(volume);}
    public static void playRocketSound() { if (rocketSound != null) rocketSound.play(volume);}
    public static void playArrowShoot() { if (arrowSound != null) arrowSound.play(volume);}
    public static void playHit() { if (hitSound != null) hitSound.play(volume); }
    public static void playTeleport() { if (teleportSound != null) teleportSound.play(volume); }
    public static void playDeath() { if (deathSound != null) deathSound.play(); }

    public static void playMenuMusic() {
        if (musicPlaying) return;
        var url = SoundManager.class.getResource("/util/sounds/thememusic2.wav");
        if (url == null) return;
        menuMusic = new MediaPlayer(new Media(url.toExternalForm()));
        menuMusic.setVolume(volume);
        menuMusic.setCycleCount(MediaPlayer.INDEFINITE);
        menuMusic.play();
        musicPlaying = true;
    }

    public static void playGameMusic() {
        if (musicPlaying) return;
        var url = SoundManager.class.getResource("/util/sounds/gamemusic.wav");
        if (url == null) return;
        menuMusic = new MediaPlayer(new Media(url.toExternalForm()));
        menuMusic.setVolume(volume);
        menuMusic.setCycleCount(MediaPlayer.INDEFINITE);
        menuMusic.play();
        musicPlaying = true;
    }

    public static void stopMenuMusic() {
        if (menuMusic != null) {
            menuMusic.stop();
            menuMusic.dispose();
            menuMusic = null;
        }
        musicPlaying = false;
    }
}
