package model.managers;

import javafx.scene.media.AudioClip;

public class SoundManager {
    private static AudioClip shootSound;
    private static AudioClip hitSound;
    private static AudioClip deathSound;
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        shootSound = loadClip("/util/sounds/shoot.wav");
        // Ready for when sound files are added:
        // hitSound = loadClip("/util/sounds/hit.wav");
        // deathSound = loadClip("/util/sounds/death.wav");
        initialized = true;
    }

    private static AudioClip loadClip(String path) {
        var url = SoundManager.class.getResource(path);
        return url != null ? new AudioClip(url.toExternalForm()) : null;
    }

    public static void playShoot() { if (shootSound != null) shootSound.play(); }
    public static void playHit()   { if (hitSound != null) hitSound.play(); }
    public static void playDeath() { if (deathSound != null) deathSound.play(); }
}
