package model.entities;

import model.managers.EnemyProjectileManager;

public abstract class Enemy extends Entity {
    protected int textureID;

    public void update(double deltaTime, double playerX, double playerY) {
        double dx = playerX - x;
        double dy = playerY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 0) {
            x += (dx / dist) * movementSpeed * deltaTime;
            y += (dy / dist) * movementSpeed * deltaTime;
        }
    }

    public void update(double deltaTime, double playerX, double playerY,
                        EnemyProjectileManager projectileManager) {
        update(deltaTime, playerX, playerY);
    }

    public void takeProjectileDamage(int amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
            dead = true;
        }
    }

    public int getTextureID() {
        return textureID;
    }
}
