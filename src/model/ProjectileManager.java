package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class ProjectileManager {
    // This class will store coordinates, velocity and handle all bullets on screen for each gametick
    private final int MAX_PROJECTILES = 1000;
    private final int PROJECTILE_TEXTURES_COUNT = 1;
    private int projectileCount = 0;
    private final double X_VALUE_MAX;
    private final double Y_VALUE_MAX;

    public ProjectileManager(double X_VALUE_MAX, double Y_VALUE_MAX) {
        this.X_VALUE_MAX = X_VALUE_MAX;
        this.Y_VALUE_MAX = Y_VALUE_MAX;
        initTextures();
    }

    //pos and radius
    private double[] posX = new double[MAX_PROJECTILES];
    private double[] posY = new double[MAX_PROJECTILES];
    private double[] radius = new double[MAX_PROJECTILES];

    //vel (the relative change to the next pos)
    private double[] velX = new double[MAX_PROJECTILES];
    private double[] velY = new double[MAX_PROJECTILES];

    //projectile id and effectID for if it needs special treatment
    private int[] projectileID = new int[MAX_PROJECTILES];
    private int[] effectID = new int[MAX_PROJECTILES];

    //projectile textures
    private final Image[] projectileTextures = new Image[PROJECTILE_TEXTURES_COUNT];
    private void initTextures() {
        projectileTextures[0] = new Image(getClass().getResourceAsStream("/util/images/bullet1.png"));
    }

    //
    private final int[][] textureGroup = new int[PROJECTILE_TEXTURES_COUNT][MAX_PROJECTILES];
    private final int[] groupCount = new int[PROJECTILE_TEXTURES_COUNT];

    public void update(double deltaTime) {
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

    private boolean isInBounds(double x, double y) {
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

    public void addProjectile(double x, double y, double r, double targetX, double targetY, double speed, int projID, int effID) {
        if (projectileCount >= MAX_PROJECTILES) return;

        posX[projectileCount] = x;
        posY[projectileCount] = y;
        radius[projectileCount] = r;

        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx*dx + dy*dy);

        if (dist == 0) {
            velX[projectileCount] = speed;
            velY[projectileCount] = 0;
        } else {
            velX[projectileCount] = (dx / dist) * speed;
            velY[projectileCount] = (dy / dist) * speed;
        }

        projectileID[projectileCount] = projID;
        effectID[projectileCount++] = effID;
    }

    public void drawAll(GraphicsContext gc) {
        // resets groupCount
        for (int i = 0; i < groupCount.length; i++) {
            groupCount[i] = 0;
        }

        for (int i = 0; i < projectileCount; i++) {
            int texID = projectileID[i];
            // check that texture id exists in range
            // then adds the projectile index to the array that will handle that texture
            if (texID <= 0 || texID >= projectileTextures.length) {
                textureGroup[texID][groupCount[texID]++] = i;
            }
        }

        for (int i = 0; i < projectileTextures.length; i++) {
            Image texture = projectileTextures[i];
            if (texture != null && groupCount[i] != 0) {
                for (int j = 0; j < groupCount[i]; j++) {
                    int k = textureGroup[i][j];

                    // handle effects in the future here

                    gc.drawImage(texture, posX[k] - radius[k], posY[k] - radius[k], radius[k] * 2, radius[k] * 2);
                }
            }
        }
    }

    public int getCount() { return projectileCount; }
    public double getX(int i) { return posX[i]; }
    public double getY(int i) { return posY[i]; }
    public double getRadius(int i) { return radius[i]; }
    public int getProjectileID(int i) { return projectileID[i]; }
}