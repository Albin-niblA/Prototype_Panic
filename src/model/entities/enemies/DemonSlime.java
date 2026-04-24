package model.entities.enemies;

import model.entities.Enemy;

public class DemonSlime extends Enemy {

    public DemonSlime(double x, double y) {
        this.x = x;
        this.y = y;
        this.size = 40;
        this.health = 100;
        this.maxHealth = 100;
        this.movementSpeed = 100;
        this.contactDamage = 25;
        this.textureID = 1;
    }
}
