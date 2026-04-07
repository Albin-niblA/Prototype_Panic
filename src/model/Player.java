package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Iterator;

public class Player extends Entity {
    private boolean moveUp;
    private boolean moveDown;
    private boolean moveLeft;
    private boolean moveRight;
    private int moveDir = 0;
    private final int PLAYER_TEXTURES = 4;
    private ArrayList<Player> players = new ArrayList<>();

    private final Image[] playerTextures = new Image[PLAYER_TEXTURES];
    private void initTextures() {
        playerTextures[0] = new Image(getClass().getResourceAsStream("/util/images/player/pFront.png"));
        playerTextures[1] = new Image(getClass().getResourceAsStream("/util/images/player/pBack.png"));
        playerTextures[2] = new Image(getClass().getResourceAsStream("/util/images/player/pLeft.png"));
        playerTextures[3] = new Image(getClass().getResourceAsStream("/util/images/player/pRight.png"));
    }

    public Player(double startX, double startY){
        this.x = startX;
        this.y = startY;
        initTextures();
    }

    public void update(double deltaTime, double mapWidth, double mapHeight){
        double dx = 0;
        double dy = 0;

        if(moveUp) {
            dy -= 1;
            moveDir = 1;
        }
        if(moveDown) {
            dy += 1;
            moveDir = 0;
        }
        if(moveLeft) {
            dx -= 1;
            moveDir = 2;
        }
        if(moveRight) {
            dx += 1;
            moveDir = 3;
        }



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
        Image currentTexture = playerTextures[moveDir];
        gc.drawImage(currentTexture, x - size / 2,  y - size / 2, size, size);
    }

    public void takeDamage(int amount){
        health -= amount;
        if (health <= 0 ){
            isDead();
        }
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
