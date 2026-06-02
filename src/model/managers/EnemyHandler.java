package model.managers;

import model.entities.Enemy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnemyHandler {
    private final List<Enemy> enemies = new ArrayList<>();
    private final EffectManager fxMan;

    public EnemyHandler(EffectManager fxMan) {
        this.fxMan = fxMan;
    }

    public void update(double deltaTime, double playerX, double playerY,
                       ProjectileManager projectileManager) {
        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy e = it.next();
            e.update(deltaTime, playerX, playerY, projectileManager);
            if (e.getDOTCurrentTick() > 0) {
                fxMan.addEffect(e.getX(), e.getY(), 3, System.nanoTime());
            }
            if (e.isDead()) it.remove();
        }
    }

    public Enemy checkHit(double px, double py, double pr, int damage, double slowMultiplier,
                          double poisonDamage, double DOTDuration, double electricMultiplier) {
        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy e = it.next();
            double dx = px - e.getX();
            double dy = py - e.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < pr + e.getSize() / 2) {
                e.takeProjectileDamage(damage);
                applyElectricDamage((int) (damage * electricMultiplier), e);
                e.takeDOTDamage(poisonDamage, DOTDuration);
                e.setMovementSpeed(e.getMovementSpeed() * slowMultiplier);
                if (e.isDead()) it.remove();
                return e;
            }
        }
        return null;
    }

    private void applyElectricDamage(int damage, Enemy e) {
        int targets = 3;
        if (damage > 0) {
            for (Enemy enemy : enemies) {
                if (targets > 0) {
                    if (enemy != e) {
                        fxMan.addEffect(enemy.getX(), enemy.getY(), 4, System.nanoTime());
                        enemy.takeProjectileDamage(damage);
                        targets--;
                    }
                }
            }
        }
    }

    public int applyAoeDamage(double px, double py, double aoeRadius, int damage) {
        int hitCount = 0;
        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy e = it.next();
            double dx = px - e.getX();
            double dy = py - e.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < aoeRadius + e.getSize() / 2) {
                e.takeProjectileDamage(damage);
                hitCount++;
                if (e.isDead()) it.remove();
            }
        }
        return hitCount;
    }

    public Enemy checkPlayerHit(double px, double py, double pr) {
        for (Enemy e : enemies) {
            double dx = px - e.getX();
            double dy = py - e.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < pr + e.getSize() / 2) {
                return e;
            }
        }
        return null;
    }

    public void addEnemy(Enemy e) {
        enemies.add(e);
    }

    public void clear() {
        enemies.clear();
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public int getCount() {
        return enemies.size();
    }
}
