package model;

public abstract class Entity {
    protected double x;
    protected double y;
    protected double movementSpeed = 220; // pps / pixlar per sekund
    protected double size = 50;

    protected int maxHealth = 100;
    protected int health = 100;
    protected double damageCooldown = 0.0;
    protected double DAMAGE_COOLDOWN_DURATION = 0.75;
}
