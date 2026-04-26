package model.managers;

public class EnemyProjectileManager {
    private static final int MAX_PROJECTILES = 500;
    private int projectileCount = 0;
    private final double worldWidth;
    private final double worldHeight;

    private final double[] posX = new double[MAX_PROJECTILES];
    private final double[] posY = new double[MAX_PROJECTILES];
    private final double[] radius = new double[MAX_PROJECTILES];
    private final double[] velX = new double[MAX_PROJECTILES];
    private final double[] velY = new double[MAX_PROJECTILES];
    private final int[] textureID = new int[MAX_PROJECTILES];
    private final int[] damage = new int[MAX_PROJECTILES];

    public EnemyProjectileManager(double worldWidth, double worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void update(double deltaTime) {
        for (int i = 0; i < projectileCount; i++) {
            posX[i] += velX[i] * deltaTime;
            posY[i] += velY[i] * deltaTime;

            if (!isInBounds(posX[i], posY[i])) {
                deleteProjectile(i--);
            }
        }
    }

    private boolean isInBounds(double x, double y) {
        return (x > 0 && x < worldWidth) && (y > 0 && y < worldHeight);
    }

    public void addProjectile(double x, double y, double r,
                               double targetX, double targetY,
                               double speed, int texID, int dmg) {
        if (projectileCount >= MAX_PROJECTILES) return;

        posX[projectileCount] = x;
        posY[projectileCount] = y;
        radius[projectileCount] = r;

        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist == 0) {
            velX[projectileCount] = speed;
            velY[projectileCount] = 0;
        } else {
            velX[projectileCount] = (dx / dist) * speed;
            velY[projectileCount] = (dy / dist) * speed;
        }

        textureID[projectileCount] = texID;
        damage[projectileCount] = dmg;
        projectileCount++;
    }

    public void deleteProjectile(int index) {
        int last = projectileCount - 1;
        posX[index] = posX[last];
        posY[index] = posY[last];
        radius[index] = radius[last];
        velX[index] = velX[last];
        velY[index] = velY[last];
        textureID[index] = textureID[last];
        damage[index] = damage[last];
        projectileCount--;
    }

    public int checkPlayerHit(double playerX, double playerY, double playerRadius) {
        for (int i = 0; i < projectileCount; i++) {
            double dx = posX[i] - playerX;
            double dy = posY[i] - playerY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < radius[i] + playerRadius) {
                int dmg = damage[i];
                deleteProjectile(i);
                return dmg;
            }
        }
        return 0;
    }

    public void clear() {
        projectileCount = 0;
    }

    public int getCount() { return projectileCount; }
    public double getX(int i) { return posX[i]; }
    public double getY(int i) { return posY[i]; }
    public double getRadius(int i) { return radius[i]; }
    public double getVelX(int i) { return velX[i]; }
    public double getVelY(int i) { return velY[i]; }
    public int getTextureID(int i) { return textureID[i]; }
    public int getDamage(int i) { return damage[i]; }
}
