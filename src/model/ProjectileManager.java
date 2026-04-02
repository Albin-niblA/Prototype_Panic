package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ProjectileManager {
    // This class will store coordinates, velocity and handle all bullets on screen for each gametick
    private final int MAX_PROJECTILES = 1000;
    private int projectileCount = 0;
    private final float X_VALUE_MAX;
    private final float Y_VALUE_MAX;

    public ProjectileManager(float X_VALUE_MAX, float Y_VALUE_MAX) {
        this.X_VALUE_MAX = X_VALUE_MAX;
        this.Y_VALUE_MAX = Y_VALUE_MAX;
    }

    //pos and radius
    private float[] posX = new float[MAX_PROJECTILES];
    private float[] posY = new float[MAX_PROJECTILES];
    private float[] radius = new float[MAX_PROJECTILES];

    //vel (the relative change to the next pos)
    private float[] velX = new float[MAX_PROJECTILES];
    private float[] velY = new float[MAX_PROJECTILES];

    //projectile id and effectID for if it needs special treatment
    private int[] projectileID = new int[MAX_PROJECTILES];
    private int[] effectID = new int[MAX_PROJECTILES];

    public void update(float deltaTime) {
        // deltaTime is the time between frames
        // so the game speed shouldn't change based on fps

        for (int i = 0; i < projectileCount; i++) {
            posX[i] += velX[i] * deltaTime;
            posY[i] += velY[i] * deltaTime;

            if (!isInBounds(posX[i], posY[i])) {
                deleteProjectile(i--);
            }
        }
    }

    private boolean isInBounds(float x, float y) {
        return (x > 0 && x < X_VALUE_MAX) && (y > 0 && y < Y_VALUE_MAX);
    }

    public void deleteProjectile(int index) {
        // replace the projectile with the last element in its place
        posX[index] = posX[projectileCount - 1];
        posY[index] = posY[projectileCount - 1];
        radius[index] = radius[projectileCount - 1];
        velX[index] = velX[projectileCount - 1];
        velY[index] = velY[projectileCount - 1];
        projectileID[index] = projectileID[projectileCount - 1];
        effectID[index] = effectID[--projectileCount];
    }

    private void drawAll(GraphicsContext gc) {
        for (int i = 0; i < projectileCount; i++) {
            switch (projectileID[i]) {
                case 1:
                    gc.setFill(Color.RED);
                    gc.fillOval(posX[i] - radius[i], posY[i] - radius[i], radius[i] * 2, radius[i] * 2);
                    break;
            }
        }
    }

    public int getCount() { return projectileCount; }
    public float getX(int i) { return posX[i]; }
    public float getY(int i) { return posY[i]; }
    public float getRadius(int i) { return radius[i]; }
    public int getProjectileID(int i) { return projectileID[i]; }
}