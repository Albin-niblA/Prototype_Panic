package model.weapon;

import model.ProjectileManager;

public abstract class Shootable extends Weapon {
    protected Shootable(String name, int damage, double fireInterval, double projectileSpeed, double projectileRadius, int textureId) {
        super(name, damage, fireInterval, projectileSpeed, projectileRadius, textureId);
    }

    @Override
    public void shoot(ProjectileManager pm, double originX, double originY, double targetX, double targetY) {
        pm.addProjectile(originX, originY, getProjectileRadius(), targetX, targetY, getProjectileSpeed(),
                getTextureId(), 0, getDamage());
    }

    @Override
    public void shootMultiple(ProjectileManager pm, double originX, double originY, double targetX, double targetY, int count) {
        pm.addProjectiles(originX, originY, getProjectileRadius(), targetX, targetY, getProjectileSpeed(),
                getTextureId(), 0, getDamage(), count);
    }
}
