package model.entities;

import model.upgrades.Upgrades;

import java.util.EnumMap;

public class Player extends Entity {
    private boolean moveUp;
    private boolean moveDown;
    private boolean moveLeft;
    private boolean moveRight;
    private int moveDir = 0; // 0=front, 1=back, 2=left, 3=right, 4=frontLeft, 5=frontRight, 6=backLeft, 7=backRight

    private EnumMap<Upgrades, Integer> upgrades = new EnumMap<>(Upgrades.class);

    private double BLINK_DISTANCE = 200.0;
    private static final double BLINK_COOLDOWN_DURATION = 3.0;
    private static final double BLINK_VISIBLE_DELAY = 0.21;

    private double blinkCooldown = 0.0;
    private double blinkVisibleTimer = 0.0;
    private boolean blinking = false;
    private boolean canBlink = true;

    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.maxHealth = 100;
        this.health = 100;
        this.contactDamage = 0;
        for (Upgrades u : Upgrades.values()) {
            upgrades.put(u, 0);
        }
    }

    public void setMoving(boolean up, boolean down, boolean left, boolean right) {
        this.moveUp = up;
        this.moveDown = down;
        this.moveLeft = left;
        this.moveRight = right;
    }

    public void update(double deltaTime, double mapWidth, double mapHeight) {
        double dx = 0;
        double dy = 0;

        if      ( moveDown && !moveLeft && !moveRight) moveDir = 0;
        else if ( moveUp   && !moveLeft && !moveRight) moveDir = 1;
        else if ( moveLeft && !moveUp   && !moveDown)  moveDir = 2;
        else if ( moveRight&& !moveUp   && !moveDown)  moveDir = 3;
        else if ( moveDown &&  moveLeft)               moveDir = 4;
        else if ( moveDown)                            moveDir = 5;
        else if ( moveUp   &&  moveLeft)               moveDir = 6;
        else if ( moveUp)                              moveDir = 7;

        if (moveUp)    dy -= 1;
        if (moveDown)  dy += 1;
        if (moveLeft)  dx -= 1;
        if (moveRight) dx += 1;

        if (dx != 0 && dy != 0) {
            double factor = 1.0 / Math.sqrt(2);
            dx *= factor;
            dy *= factor;
        }

        x += dx * movementSpeed * deltaTime;
        y += dy * movementSpeed * deltaTime;

        x = Math.max(size / 2, Math.min(mapWidth - size / 2, x));
        y = Math.max(size / 2, Math.min(mapHeight - size / 2, y));

        if (damageCooldown > 0)    damageCooldown -= deltaTime;
        if (blinkCooldown > 0)     blinkCooldown -= deltaTime;
        if (blinkVisibleTimer > 0) {
            blinkVisibleTimer -= deltaTime;
            if (blinkVisibleTimer <= 0) blinking = false;
        }
    }

    public boolean tryBlink(double mapWidth, double mapHeight) {
        int blinkLevel = getUpgradeLevel(Upgrades.Blink);
        if (blinkLevel == 0) return false;
        if (!canBlink || blinkCooldown > 0) return false;

        double dx = 0, dy = 0;
        switch (moveDir) {
            case 0 -> { dy =  1; }
            case 1 -> { dy = -1; }
            case 2 -> { dx = -1; }
            case 3 -> { dx =  1; }
            case 4 -> { dx = -1; dy =  1; }
            case 5 -> { dx =  1; dy =  1; }
            case 6 -> { dx = -1; dy = -1; }
            case 7 -> { dx =  1; dy = -1; }
        }

        if (dx != 0 && dy != 0) {
            double factor = 1.0 / Math.sqrt(2);
            dx *= factor;
            dy *= factor;
        }

        x = Math.max(size / 2, Math.min(mapWidth  - size / 2, x + dx * BLINK_DISTANCE));
        y = Math.max(size / 2, Math.min(mapHeight - size / 2, y + dy * BLINK_DISTANCE));

        blinkCooldown = BLINK_COOLDOWN_DURATION * Math.pow(0.9, blinkLevel);
        blinking = true;
        blinkVisibleTimer = BLINK_VISIBLE_DELAY;
        return true;
    }

    public int getMoveDir()                 { return moveDir; }
    public double getBlinkCooldown()        { return blinkCooldown; }
    public double getBlinkCooldownDuration(){ return BLINK_COOLDOWN_DURATION; }
    public boolean isBlinking()             { return blinking; }
    public void setCanBlink(boolean v)      { canBlink = v; }
    public void setBlinking(boolean v)      { blinking = v; }
    public double getBLINK_DISTANCE() { return BLINK_DISTANCE; }
    public void setBLINK_DISTANCE(double BLINK_DISTANCE) { this.BLINK_DISTANCE = BLINK_DISTANCE; }

    public int getUpgradeLevel(Upgrades u) {
        return upgrades.getOrDefault(u, 0);
    }

    public void levelUpgrade(Upgrades u) {
        int level = upgrades.getOrDefault(u, 0);
        upgrades.put(u, level + 1);
    }

    public void reset(double startX, double startY) {
        x = startX;
        y = startY;
        health = maxHealth;
        damageCooldown = 0;
        dead = false;
        moveUp = moveDown = moveLeft = moveRight = false;
        blinkCooldown = 0;
        blinkVisibleTimer = 0;
        blinking = false;
    }
}