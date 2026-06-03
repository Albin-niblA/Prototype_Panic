package model.entities.enemies;

import model.entities.Enemy;
import model.managers.ProjectileManager;

public class IceMage extends Enemy {

    // Shooting: basic attack
    private static final double SHOOT_COOLDOWN = 1.5;
    private static final double PROJECTILE_SPEED = 750;
    private static final double PROJECTILE_RADIUS = 20; // Var 8 innan
    private static final int PROJECTILE_DAMAGE = 20;
    private static final int PROJECTILE_TEXTURE_ID_BASIC = 5; // arrow.png atm (gör egen projektil)
    private double shootTimer = 0;

    // Shooting: special attack (every 5th shot)
    private static final int SPECIAL_ATTACK_INTERVAL = 5;
    private static final double SPECIAL_PROJECTILE_SPEED = 500;
    private static final int SPECIAL_PROJECTILE_DAMAGE = 20;
    private static final int PROJECTILE_TEXTURE_ID_SPECIAL = 6; // ice-projectile
    private static final int FREEZE_EFFECT_ID = 1;
    private int shotCounter = 0;

    // Movement
    private static final double PREFERRED_DISTANCE = 300;
    private static final double DISTANCE_TOLERANCE = 50;
    private int strafeDirection = 1;

    public IceMage(double x, double y) {
        super(x, y, 100, 500, 200, 10, 3, 100, 100);
        this.strafeDirection = Math.random() < 0.5 ? 1 : -1;
    }

    @Override
    public void update(double deltaTime, double playerX, double playerY,
                       ProjectileManager projectileManager) {
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
            shotCounter++;
            if (shotCounter >= SPECIAL_ATTACK_INTERVAL) {
                // Special freeze attack
                projectileManager.addProjectile(
                        x, y, PROJECTILE_RADIUS,
                        playerX, playerY,
                        SPECIAL_PROJECTILE_SPEED, PROJECTILE_TEXTURE_ID_SPECIAL,
                        FREEZE_EFFECT_ID,
                        (int) (SPECIAL_PROJECTILE_DAMAGE * projectileDamageMultiplier), 0, true
                );
                shotCounter = 0;
            } else {
                // Basic attack
                projectileManager.addProjectile(
                        x, y, PROJECTILE_RADIUS,
                        playerX, playerY,
                        PROJECTILE_SPEED, PROJECTILE_TEXTURE_ID_BASIC, 0,
                        (int) (PROJECTILE_DAMAGE * projectileDamageMultiplier), 0, true
                );
            }
            shootTimer = SHOOT_COOLDOWN;
        }
    }
}
