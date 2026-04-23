package model.weapon;

import model.ProjectileManager;

public class Grenade extends Weapon implements Upgrade {
    private static final String NAME = "Grenade";
    private static final int BASE_DAMAGE = 75;
    private static final double BASE_FIRE_INTERVAL = 3;
    private static final double BASE_PROJECTILE_SPEED = 400;
    private static final double BASE_PROJECTILE_RADIUS = 20;
    private static final int TEXTURE_ID = 3;

    private static final double BASE_FUSE_TIME = 2.0;
    private static final double BASE_EXPLOSION_RADIUS = 150;

    private double fuseTime;
    private double explosionRadius;

    public Grenade() {
        super(NAME, BASE_DAMAGE, BASE_FIRE_INTERVAL, BASE_PROJECTILE_SPEED, BASE_PROJECTILE_RADIUS, TEXTURE_ID);
        this.fuseTime = BASE_FUSE_TIME;
        this.explosionRadius = BASE_EXPLOSION_RADIUS;
    }

    @Override
    public void shoot(ProjectileManager pm, double originX, double originY, double targetX, double targetY) {
        pm.addGrenade(originX, originY, getProjectileRadius(), targetX, targetY, getProjectileSpeed(), getTextureId(),
                0, getDamage(), fuseTime, explosionRadius);
    }

    public double getFuseTime() { return fuseTime; }
    public void setFuseTime(double fuseTime) { this.fuseTime = fuseTime; }
    public double getExplosionRadius() { return explosionRadius; }
    public void setExplosionRadius(double explosionRadius) { this.explosionRadius = explosionRadius; }

    @Override
    public void STAGE_ONE_UPGRADE_ONE(){
        setFireInterval(0.5);
    }
}
