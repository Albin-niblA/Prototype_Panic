package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import model.enemies.Slime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class EnemyHandler {
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private final int X_VALUE_MAX;
    private final int Y_VALUE_MAX;
    private Random rand = new Random();
    private final int ENEMY_TEXTURE_COUNT = 1;
    private final Image[] enemyTextures = new Image[ENEMY_TEXTURE_COUNT];
    private void initTextures() {
        enemyTextures[0] = new Image(getClass().getResourceAsStream("/util/images/enemies/slime.png"));
    }

    public EnemyHandler(int x, int y) {
        this.X_VALUE_MAX = x;
        this.Y_VALUE_MAX = y;
        initTextures();
    }


    //uppdaterar spelarens koordinater på skärmen
    public void update(double deltaTime, double playerX, double playerY) {
        Iterator<Enemy> it = enemies.iterator();

        while (it.hasNext()) {
            Enemy e = it.next();
            e.update(deltaTime, playerX, playerY);

            if (e.isDead()) {
                it.remove();
            }
        }
    }

    public boolean checkHit(double px, double py, double pr) {
        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy e = it.next();
            double dx = px - e.getX();
            double dy = py - e.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < pr + e.getSize() / 2) {
                e.takeDamage(100); // one-shot, lower for multi-hit
                if (e.isDead()) it.remove();
                return true;
            }
        }
        return false;
    }

    public boolean checkPlayerHit(double px, double py, double pr) {
        for (Enemy e : enemies) {
            double dx = px - e.getX();
            double dy = py - e.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < pr + e.getSize() / 2) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        enemies.clear();
    }

    public void drawAll(GraphicsContext gc) {
        Iterator<Enemy> it = enemies.iterator();

        while (it.hasNext()) {
            Enemy e = it.next();
            Image tex = enemyTextures[e.getTextureID()];
            double size = e.getSize();

            gc.drawImage(tex, e.getX() - size/2, e.getY() - size/2, size, size);
        }
    }

    public void spawnRandom(int type) {
        Enemy e;
        double x = rand.nextDouble(0, X_VALUE_MAX);
        double y = rand.nextDouble(0, Y_VALUE_MAX);
        switch (type) {
            case 0:
                e = new Slime(x, y);
                break;
            default:
                e = new Slime(x, y);
        }
        enemies.add(e);
    }
}
