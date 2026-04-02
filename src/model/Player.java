package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player extends Entity {
    private boolean moveUp;
    private boolean moveDown;
    private boolean moveLeft;
    private boolean moveRight;

    public Player(double startX, double startY){
        this.x = startX;
        this.y = startY;
    }

    public void update(double deltaTime, double mapWidth, double mapHeight){
        double dx = 0;
        double dy = 0;

        if(moveUp) dy -= 1;
        if(moveDown) dy += 1;
        if(moveLeft) dx -= 1;
        if(moveRight) dx += 1;

        // Normalize diagonal movement so speed is consistent in all 8 directions
        if(dx != 0 && dy != 0){
            double factor = 1.0 / Math.sqrt(2);
            dx *= factor;
            dy *= factor;
        }

        x += dx * movementSpeed * deltaTime;
        y += dy * movementSpeed * deltaTime;

        x = Math.max(size / 2, Math.min(mapWidth - size / 2, x));
        y = Math.max(size / 2, Math.min(mapHeight - size / 2, y));

        if(damageCooldown > 0) damageCooldown -= deltaTime;

    }

    public void draw(GraphicsContext gc){
        gc.setFill(Color.CORNFLOWERBLUE);

        gc.fillOval(x - size / 2, y - size / 2, size, size);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeOval(x - size / 2, y - size / 2, size, size);
    }

    public boolean isDead() { return health <= 0; }

    public void reset(double startX, double startY){
        x = startX;
        y = startY;
        health = maxHealth;
        damageCooldown = 0;
    }

    public void setMoveUp(boolean v) { moveUp = v; }
    public void setMoveDown(boolean v) { moveDown = v; }
    public void setMoveLeft(boolean v) { moveLeft = v; }
    public void setMoveRight(boolean v) { moveRight = v; }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getSize() { return size; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }


}
