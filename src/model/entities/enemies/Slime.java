package model.entities.enemies;

import model.entities.Enemy;

public class Slime extends Enemy {

    public Slime(double x, double y) {
        this.x = x;
        this.y = y;
        this.size = 40;
        this.health = 50;
        this.maxHealth = 50;
        this.movementSpeed = 30;
        this.contactDamage = 10;
        this.textureID = 0;
    }
}
