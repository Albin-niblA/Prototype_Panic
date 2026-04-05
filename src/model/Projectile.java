package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Projectile {
    private double x;
    private double y;
    private double vx;
    private double vy;
    private boolean dead = false;

    private static final double SPEED = 1;
    private static final double RADIUS = 6;

    public Projectile(double startX, double startY, double targetX, double targetY) {
        this.x = startX;
        this.y = startY;

        double dx = targetX - startX;
        double dy = targetY - startY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist == 0) {
            vx = SPEED;
            vy = 0;
        } else {
            vx = (dx / dist) * SPEED;
            vy = (dy / dist) * SPEED;
        }
    }

    public void update(double deltaTime, double mapWidth, double mapHeight) {
        x += vx * deltaTime;
        y += vy * deltaTime;

        if (x < 0 || x > mapWidth || y < 0 || y > mapHeight) {
            dead = true;
        }
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillOval(x - RADIUS, y - RADIUS, RADIUS * 3, RADIUS * 3);
    }

    public boolean isDead() { return dead; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getRadius() { return RADIUS; }
}
