package model.weapon;

public class Bullet extends Shootable {
    private static final String NAME = "Bullet";
    private static final int BASE_DAMAGE = 10;
    private static final double BASE_FIRE_INTERVAL = 0.2;
    private static final double BASE_PROJECTILE_SPEED = 1000;
    private static final double BASE_PROJECTILE_RADIUS = 10;
    private static final int TEXTURE_ID = 0;

    public Bullet() {
        super(NAME, BASE_DAMAGE, BASE_FIRE_INTERVAL, BASE_PROJECTILE_SPEED, BASE_PROJECTILE_RADIUS, TEXTURE_ID);
    }
}
