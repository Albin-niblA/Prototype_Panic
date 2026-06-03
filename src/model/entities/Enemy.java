package model.entities;

import model.managers.ProjectileManager;

public abstract class Enemy extends Entity {
    protected int textureID;
    protected int coinDropAmount;
    protected int xpDropAmount;
    protected boolean isBoss = false;
    protected double projectileDamageMultiplier = 1.0;
    protected double DOTTimer = 0;
    protected final static double DOT_TICK_SPEED = 0.5;
    protected double DOTCurrentTick = 0;
    protected double DOTDamage = 0;
    protected boolean hasLoadedPoisonEffect = false;

    public Enemy(double x, double y, int size, int maxHealth, int movementSpeed,
                 int contactDamage, int textureID, int coinDropAmount, int xpDropAmount) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.health = maxHealth;
        this.maxHealth = maxHealth;
        this.movementSpeed = movementSpeed;
        this.contactDamage = contactDamage;
        this.textureID = textureID;
        this.coinDropAmount = coinDropAmount;
        this.xpDropAmount = xpDropAmount;
    }

    public void update(double deltaTime, double playerX, double playerY) {
        double dx = playerX - x;
        double dy = playerY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 0) {
            x += (dx / dist) * movementSpeed * deltaTime;
            y += (dy / dist) * movementSpeed * deltaTime;
        }

        if (DOTTimer > 0) {
            DOTTimer -= deltaTime;
            DOTCurrentTick -= deltaTime;
            if (DOTCurrentTick <= 0) {
                takeProjectileDamage((int) (maxHealth * DOTDamage));
                DOTCurrentTick = DOT_TICK_SPEED;
            }
        } else {
            DOTDamage = 0;
            hasLoadedPoisonEffect = false;
        }
    }

    public void update(double deltaTime, double playerX, double playerY,
                        ProjectileManager projectileManager) {
        update(deltaTime, playerX, playerY);
    }

    public void takeProjectileDamage(int amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
            dead = true;
        }
    }

    public void takeDOTDamage(double damage, double timer) {
        if (damage > 0) {
            DOTDamage += damage;
            DOTTimer = timer;
        }
    }

    public void scaleStats(double multiplier) {
        this.maxHealth = (int) (this.maxHealth * multiplier);
        this.health = this.maxHealth;
        this.contactDamage = (int) (this.contactDamage * multiplier);
        this.projectileDamageMultiplier = multiplier;
    }

    public int getTextureID() {
        return textureID;
    }
    public int getCoinDropAmount() { return coinDropAmount; }
    public int getXpDropAmount() { return xpDropAmount; }
    public double getDOTTimer() {
        return DOTTimer;
    }
    public void setDOTTimer(double DOTTimer) {
        this.DOTTimer = DOTTimer;
    }
    public double getDOTTickSpeed() {
        return DOT_TICK_SPEED;
    }

    public double getDOTCurrentTick() {
        return DOTCurrentTick;
    }

    public boolean isHasLoadedPoisonEffect() {
        return hasLoadedPoisonEffect;
    }

    public void setHasLoadedPoisonEffect(boolean hasLoadedPoisonEffect) {
        this.hasLoadedPoisonEffect = hasLoadedPoisonEffect;
    }
}
