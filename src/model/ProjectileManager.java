package model;

public class ProjectileManager {
    private static final int MAX_PROJECTILES = 1000;
    private int projectileCount = 0;
    private final double worldWidth;
    private final double worldHeight;

    private final double[] posX = new double[MAX_PROJECTILES];
    private final double[] posY = new double[MAX_PROJECTILES];
    private final double[] radius = new double[MAX_PROJECTILES];
    private final double[] velX = new double[MAX_PROJECTILES];
    private final double[] velY = new double[MAX_PROJECTILES];
    private final int[] projectileID = new int[MAX_PROJECTILES];
    private final int[] effectID = new int[MAX_PROJECTILES];
    private final int[] damage = new int[MAX_PROJECTILES];

    private final double[] fuseTimer = new double[MAX_PROJECTILES];
    private final double[] deceleration = new double[MAX_PROJECTILES];
    private final boolean[] isGrenade = new boolean[MAX_PROJECTILES];

    public ProjectileManager(double worldWidth, double worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void update(double deltaTime) {
        for (int i = 0; i < projectileCount; i++) {
            if (isGrenade[i]) {
                // Decelerate the grenade
                double speed = Math.sqrt(velX[i] * velX[i] + velY[i] * velY[i]);
                if (speed > 0) {
                    double newSpeed = Math.max(0, speed - deceleration[i] * deltaTime);
                    double scale = newSpeed / speed;
                    velX[i] *= scale;
                    velY[i] *= scale;
                }

                // Count down fuse
                fuseTimer[i] -= deltaTime;

                // Move (even while decelerating)
                posX[i] += velX[i] * deltaTime;
                posY[i] += velY[i] * deltaTime;
            } else {
                // Normal projectile: constant velocity, delete if out of bounds
                posX[i] += velX[i] * deltaTime;
                posY[i] += velY[i] * deltaTime;

                if (!isInBounds(posX[i], posY[i])) {
                    deleteProjectile(i--);
                }
            }
        }
    }

    private boolean isInBounds(double x, double y) {
        return (x > 0 && x < worldWidth) && (y > 0 && y < worldHeight);
    }

    public void deleteProjectile(int index) {
        int last = projectileCount - 1;
        posX[index] = posX[last];
        posY[index] = posY[last];
        radius[index] = radius[last];
        velX[index] = velX[last];
        velY[index] = velY[last];
        projectileID[index] = projectileID[last];
        effectID[index] = effectID[last];
        damage[index] = damage[last];
        fuseTimer[index] = fuseTimer[last];
        deceleration[index] = deceleration[last];
        isGrenade[index] = isGrenade[last];
        projectileCount--;
    }

    public void addProjectile(double x, double y, double r,
                               double targetX, double targetY,
                               double speed, int projID, int effID, int dmg) {
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

        projectileID[projectileCount] = projID;
        effectID[projectileCount] = effID;
        damage[projectileCount] = dmg;
        fuseTimer[projectileCount] = 0;
        deceleration[projectileCount] = 0;
        isGrenade[projectileCount] = false;
        projectileCount++;
    }

    public void addGrenade(double x, double y, double r,
                            double targetX, double targetY,
                            double speed, int projID, int effID, int dmg,
                            double fuseTime) {
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

        projectileID[projectileCount] = projID;
        effectID[projectileCount] = effID;
        damage[projectileCount] = dmg;
        this.fuseTimer[projectileCount] = fuseTime;
        this.deceleration[projectileCount] = speed / fuseTime;
        this.isGrenade[projectileCount] = true;
        projectileCount++;
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
    public int getTextureID(int i) { return projectileID[i]; }
    public int getDamage(int i) { return damage[i]; }
    public boolean isGrenade(int i) { return isGrenade[i]; }
    public double getFuseTimer(int i) { return fuseTimer[i]; }
}
