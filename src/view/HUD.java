package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.world.GameWorld;
import model.entities.Player;

public class HUD {
    private final int screenWidth;
    private final int screenHeight;

    public HUD(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void draw(GraphicsContext gc, GameWorld world) {
        drawHealthBar(gc, world.getPlayer());
        drawWaveInfo(gc, world);
    }

    private void drawHealthBar(GraphicsContext gc, Player p) {
        double barWidth = 300;
        double barHeight = 25;
        double barX = 20;
        double barY = screenHeight - 45;

        double healthPct = (double) p.getHealth() / p.getMaxHealth();

        gc.setFill(Color.DARKGRAY);
        gc.fillRect(barX, barY, barWidth, barHeight);

        Color healthColor;
        if (healthPct > 0.6) healthColor = Color.LIMEGREEN;
        else if (healthPct > 0.3) healthColor = Color.YELLOW;
        else healthColor = Color.RED;

        gc.setFill(healthColor);
        gc.fillRect(barX, barY, barWidth * healthPct, barHeight);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(barX, barY, barWidth, barHeight);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 16));
        gc.fillText("HP: " + p.getHealth() + " / " + p.getMaxHealth(),
                     barX + 10, barY + 18);
    }

    private void drawWaveInfo(GraphicsContext gc, GameWorld world) {
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", 22));
        gc.fillText("Wave: " + world.getWaveManager().getCurrentWave(),
                     screenWidth - 200, 35);
        gc.fillText("Enemies: " + world.getEnemyHandler().getCount(),
                     screenWidth - 200, 65);
    }
}
