package model.upgrades;

public enum Upgrades {
    Multishot("Shoot an additional projectile per level", 0, Rarity.Epic),
    Nimble("Faster projectile firerate and movespeed", 1, Rarity.Common),
    Blink("Unlock blink movement. \nIncrease distance and reduce cooldown", 2, Rarity.Uncommon);

    String description;
    int id;
    Rarity rarity;

    Upgrades(String description, int id, Rarity rarity) {
        this.description = description;
        this.id = id;
        this.rarity = rarity;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Rarity getRarity() {
        return rarity;
    }
}
