package model.entities.enemies;

import model.entities.Enemy;
import model.managers.EnemyProjectileManager;

public class Bandit extends Enemy {

    // Shooting
    private static final double SHOOT_COOLDOWN = 1.5;
    private static final double PROJECTILE_SPEED = 500;
    private static final double PROJECTILE_RADIUS = 20; // Var 8 innan
    private static final int PROJECTILE_DAMAGE = 15;
    private static final int PROJECTILE_TEXTURE_ID = 1; // arrow.png atm (gör egen projektil)
    private double shootTimer = 0;

    // Movement
    private static final double PREFERRED_DISTANCE = 300;
    private static final double DISTANCE_TOLERANCE = 50;
    private int strafeDirection = 1;

    public Bandit(double x, double y) {
        this.x = x;
        this.y = y;
        this.size = 100;
        this.health = 80;
        this.maxHealth = 80;
        this.movementSpeed = 100;
        this.contactDamage = 10;
        this.textureID = 2;

        this.strafeDirection = Math.random() < 0.5 ? 1 : -1;
    }

    @Override
    public void update(double deltaTime, double playerX, double playerY,
                        EnemyProjectileManager projectileManager) {
        double dx = playerX - x;
        double dy = playerY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 0) {
            double dirX = dx / dist;
            double dirY = dy / dist;

            double perpX = -dirY * strafeDirection;
            double perpY = dirX * strafeDirection;

            double moveX = 0;
            double moveY = 0;

            if (dist > PREFERRED_DISTANCE + DISTANCE_TOLERANCE) {
                moveX = dirX * 0.6 + perpX * 0.4;
                moveY = dirY * 0.6 + perpY * 0.4;
            } else if (dist < PREFERRED_DISTANCE - DISTANCE_TOLERANCE) {
                moveX = -dirX * 0.6 + perpX * 0.4;
                moveY = -dirY * 0.6 + perpY * 0.4;
            } else {
                moveX = perpX;
                moveY = perpY;
            }

            double moveLen = Math.sqrt(moveX * moveX + moveY * moveY);
            if (moveLen > 0) {
                x += (moveX / moveLen) * movementSpeed * deltaTime;
                y += (moveY / moveLen) * movementSpeed * deltaTime;
            }
        }

        if (Math.random() < 0.005) {
            strafeDirection *= -1;
        }

        // Shooting
        shootTimer -= deltaTime;
        if (shootTimer <= 0 && dist < 500) {
            projectileManager.addProjectile(
                    x, y, PROJECTILE_RADIUS,
                    playerX, playerY,
                    PROJECTILE_SPEED, PROJECTILE_TEXTURE_ID, PROJECTILE_DAMAGE
            );
            shootTimer = SHOOT_COOLDOWN;
        }
    }
}
