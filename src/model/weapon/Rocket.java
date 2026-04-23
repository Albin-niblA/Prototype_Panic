package model.weapon;

import model.ProjectileManager;

public class Rocket extends Weapon implements Upgrade{
    private static final String NAME = "Rocket";
    private static final int BASE_DAMAGE = 50;
    private static final double BASE_FIRE_INTERVAL = 1;
    private static final double BASE_PROJECTILE_SPEED = 750;
    private static final double BASE_PROJECTILE_RADIUS = 15;
    private static final int TEXTURE_ID = 2;

    public Rocket() {
        super(NAME, BASE_DAMAGE, BASE_FIRE_INTERVAL, BASE_PROJECTILE_SPEED, BASE_PROJECTILE_RADIUS, TEXTURE_ID);
    }

    @Override
    public void shoot(ProjectileManager pm, double originX, double originY, double targetX, double targetY) {
        pm.addProjectile(originX, originY, getProjectileRadius(), targetX, targetY, getProjectileSpeed(),
                getTextureId(), 0, getDamage());
    }

    @Override
    public void STAGE_ONE_UPGRADE_ONE(){
        setFireInterval(0.2);
        setProjectileRadius(300);
    }
}
