package model.weapon;

public class Weapon {
    private final String name;
    private final int damage;
    private final double fireInterval;
    private final double projectileSpeed;
    private final double projectileRadius;
    private final int textureId;
    private final double fuseTime;
    private final double explosionRadius;

    public Weapon(String name, int damage, double fireInterval,
                  double projectileSpeed, double projectileRadius, int textureId,
                  double fuseTime, double explosionRadius) {
        this.name = name;
        this.damage = damage;
        this.fireInterval = fireInterval;
        this.projectileSpeed = projectileSpeed;
        this.projectileRadius = projectileRadius;
        this.textureId = textureId;
        this.fuseTime = fuseTime;
        this.explosionRadius = explosionRadius;
    }

    public static Weapon fromType(WeaponType type) {
        return switch (type) {
            case BULLET  -> new Weapon("Bullet",  10, 0.3, 1000, 10, 0, 0, 0);
            case ARROW   -> new Weapon("Arrow",   25, 0.5,  800,  8, 1, 0, 0);
            case ROCKET  -> new Weapon("Rocket",  50, 1,    750, 15, 2, 0, 0);
            case GRENADE -> new Weapon("Grenade", 75, 3,    400, 20, 3, 2.0, 150);
        };
    }

    public String getName() { return name; }
    public int getDamage() { return damage; }
    public double getFireInterval() { return fireInterval; }
    public double getProjectileSpeed() { return projectileSpeed; }
    public double getProjectileRadius() { return projectileRadius; }
    public int getTextureId() { return textureId; }
    public double getFuseTime() { return fuseTime; }
    public double getExplosionRadius() { return explosionRadius; }
    public boolean isGrenade() { return fuseTime > 0; }
}
