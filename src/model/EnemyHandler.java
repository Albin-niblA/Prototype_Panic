package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnemyHandler {
    private final List<Enemy> enemies = new ArrayList<>();

    public void update(double deltaTime, double playerX, double playerY) {
        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy e = it.next();
            e.update(deltaTime, playerX, playerY);
            if (e.isDead()) it.remove();
        }
    }

    public boolean checkHit(double px, double py, double pr, int damage) {
        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy e = it.next();
            double dx = px - e.getX();
            double dy = py - e.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < pr + e.getSize() / 2) {
                e.takeProjectileDamage(damage);
                if (e.isDead()) it.remove();
                return true;
            }
        }
        return false;
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
