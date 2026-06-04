package model.items;

import java.util.List;
import java.util.ArrayList;

public class Item {
    private String name;
    private List<String> description;
    private final int textureID;
    private final int price;

    private final int movementSpeed;
    private final int health;
    private final int damage;
    private final double attackSpeed;
    private final int onHit;
    private final int blinkDistance;
    private final double healthPercent;

    public Item(String name, int textureID, int price, int movementSpeed, int health, int damage, double attackSpeed, int onHit, int blinkDistance) {
        this(name, textureID, price, movementSpeed, health, 0.0, damage, attackSpeed, onHit, blinkDistance);
    }
    public Item(String name, int textureID, int price, int movementSpeed, int health, double healthPercent, int damage, double attackSpeed, int onHit, int blinkDistance){
        this.name = name;
        this.textureID = textureID;
        this.price = price;
        this.movementSpeed = movementSpeed;
        this.health = health;
        this.healthPercent = healthPercent; // NYTT
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.onHit = onHit;
        this.blinkDistance = blinkDistance;
        this.description = new ArrayList<>(); // ÄNDRAT: var null innan → krasch
        formatDescription();
    }
    private void formatDescription() {
        if (movementSpeed > 0) {
            description.add("Movement speed: " + movementSpeed);
        }
        if (health > 0) {
            description.add("Health: " + health);
        }
        if (damage > 0) {
            description.add("Damage: " + damage);
        }
        if (attackSpeed > 0) {
            description.add("Attack speed: " + attackSpeed + "%");
        }
        if (onHit > 0) {
            description.add("Onhit damage: " + onHit);
        }
        if (blinkDistance > 0) {
            description.add("Bonus blink distance: " + blinkDistance);
        }
        if (healthPercent > 0){
            description.add("Life gain: +" + (int)(healthPercent * 100) + "% of max HP");
        }
    }

    public String getName() {
        return name;
    }

    public List<String> getDescription() {
        return description;
    }

    public int getTextureID() {
        return textureID;
    }

    public int getMovementSpeed() {
        return movementSpeed;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public int getOnHit() {
        return onHit;
    }

    public int getBlinkDistance() {
        return blinkDistance;
    }

    public int getPrice() {
        return price;
    }
    public double getHealthPercent(){
        return healthPercent;}
}
