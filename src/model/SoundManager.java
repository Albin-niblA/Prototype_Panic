package model;

import javafx.scene.media.AudioClip;
import model.Settings;
import model.weapon.WeaponType;

public class SoundManager {
    private static AudioClip[] shootSounds;
    private static AudioClip hitSound;
    private static AudioClip deathSound;
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;

        shootSounds = new AudioClip[WeaponType.values().length];
        shootSounds[WeaponType.BULLET.ordinal()]  = loadClip("/util/sounds/shoot_bullet.wav");
        shootSounds[WeaponType.ARROW.ordinal()]   = loadClip("/util/sounds/shoot_arrow.wav");
        shootSounds[WeaponType.ROCKET.ordinal()]  = loadClip("/util/sounds/shoot_rocket.wav");
        shootSounds[WeaponType.GRENADE.ordinal()] = loadClip("/util/sounds/shoot_grenade.wav");

        hitSound   = loadClip("/util/sounds/hit.wav");
        deathSound = loadClip("/util/sounds/death.wav");

        initialized = true;
    }

    private static AudioClip loadClip(String path) {
        var url = SoundManager.class.getResource(path);
        return url != null ? new AudioClip(url.toExternalForm()) : null;
    }

    public static void playShoot(WeaponType type) {
        AudioClip clip = shootSounds[type.ordinal()];
        if (clip != null) clip.play(Settings.getVolume());
    }

    public static void playHit() {
        if (hitSound != null) hitSound.play(Settings.getVolume());
    }

    public static void playDeath() {
        if (deathSound != null) deathSound.play(Settings.getVolume());
    }
}