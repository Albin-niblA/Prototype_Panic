package model;

public enum Upgrades {
    Multishot("Shoot an additional projectile per level");

    String description;

    Upgrades(String description) {
        this.description = description;
    }
}
