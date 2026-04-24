package model.entities;

public abstract class Entity {
    protected double x;
    protected double y;
    protected double movementSpeed = 220;
    protected double size = 50;

    protected int maxHealth = 100;
    protected int health = 100;
    protected int contactDamage = 0;
    protected double damageCooldown = 0.0;
    protected static final double DAMAGE_COOLDOWN_DURATION = 0.75;
    protected boolean dead = false;

    public boolean isDead() {
        return dead;
    }

    public void takeDamage(int amount) {
        if (damageCooldown > 0) return;
        health -= amount;
        damageCooldown = DAMAGE_COOLDOWN_DURATION;
        if (health <= 0) {
            health = 0;
            dead = true;
        }
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public double getSize() {
        return size;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getContactDamage() {
        return contactDamage;
    }

    public double getDamageCooldown() {
        return damageCooldown;
    }

    public double getMovementSpeed() { return movementSpeed; }

    public void setMovementSpeed(double movementSpeed) { this.movementSpeed = movementSpeed; }
}
