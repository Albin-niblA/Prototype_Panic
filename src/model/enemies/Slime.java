package model.enemies;

import model.Enemy;
import model.Player;

public class Slime extends Enemy {

    Player player;

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

    @Override
    public void takeProjectileDamage(int amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
            dead = true;
        }
    }
}
