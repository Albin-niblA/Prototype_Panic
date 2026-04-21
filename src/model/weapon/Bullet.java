package model.weapon;

import model.ProjectileManager;

public class Bullet extends Weapon implements Upgrade {
    private static final String NAME = "Bullet";
    private static final int BASE_DAMAGE = 10;
    private static final double BASE_FIRE_INTERVAL = 0.3;
    private static final double BASE_PROJECTILE_SPEED = 1000;
    private static final double BASE_PROJECTILE_RADIUS = 10;
    private static final int TEXTURE_ID = 0;

    public Bullet() {
        super(NAME, BASE_DAMAGE, BASE_FIRE_INTERVAL, BASE_PROJECTILE_SPEED, BASE_PROJECTILE_RADIUS, TEXTURE_ID);
    }

    @Override
    public void shoot(ProjectileManager pm, double originX, double originY, double targetX, double targetY) {
        pm.addProjectile(originX, originY, getProjectileRadius(), targetX, targetY, getProjectileSpeed(),
                getTextureId(), 0, getDamage());
    }

    @Override
    public void STAGE_ONE_UPGRADE_ONE(){
        setDamage(getDamage() * 5);
    }
}
