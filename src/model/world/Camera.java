package model.world;

import model.entities.Player;

public class Camera {
    private double offsetX;
    private double offsetY;
    private final int viewportWidth;
    private final int viewportHeight;

    public Camera(int viewportWidth, int viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
    }

    public void follow(Player player) {
        offsetX = player.getX() - viewportWidth / 2.0;
        offsetY = player.getY() - viewportHeight / 2.0;

        offsetX = Math.max(0, Math.min(offsetX, GameWorld.WORLD_WIDTH - viewportWidth));
        offsetY = Math.max(0, Math.min(offsetY, GameWorld.WORLD_HEIGHT - viewportHeight));
    }

    public double getOffsetX() { return offsetX; }
    public double getOffsetY() { return offsetY; }
    public int getViewportWidth() { return viewportWidth; }
    public int getViewportHeight() { return viewportHeight; }

    public boolean isVisible(double wx, double wy, double size) {
        return wx + size / 2 > offsetX && wx - size / 2 < offsetX + viewportWidth
            && wy + size / 2 > offsetY && wy - size / 2 < offsetY + viewportHeight;
    }
}
