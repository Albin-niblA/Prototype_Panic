package util.sounds;

import javafx.scene.media.AudioClip;

public class SoundManager {
    private static AudioClip shootSound;
    private static AudioClip hitSound;
    private static AudioClip deathSound;

    public static void init() {
        shootSound = new AudioClip(SoundManager.class.getResource("/util/sounds/shoot.wav").toExternalForm());
        hitSound   = new AudioClip(SoundManager.class.getResource("/util/sounds/hit.wav").toExternalForm());
        deathSound = new AudioClip(SoundManager.class.getResource("/util/sounds/death.wav").toExternalForm());
    }

    public static void playShoot() { shootSound.play(); }
    public static void playHit()   { hitSound.play(); }
    public static void playDeath() { deathSound.play(); }
}