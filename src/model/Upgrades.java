package model;

public enum Upgrades {
    Multishot("Shoot an additional projectile per level"),
    Nimble("Faster projectile firerate and movespeed"),
    Blink("Unlock blink movement. Increase distance and reduce cooldown");

    String description;

    Upgrades(String description) {
        this.description = description;
    }
}
