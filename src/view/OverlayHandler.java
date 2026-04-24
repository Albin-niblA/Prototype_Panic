package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import model.upgrades.UpgradeManager;
import model.upgrades.Upgrades;
import model.world.GameState;
import util.images.TextureAtlas;

import java.util.List;

/**
 * Ritar halvtransparenta överläggsmenyer ovanpå spelscenen.
 *
 * Anropas från GamePanel.render() med aktuellt GameState.
 * Lägg till nya case här när fler tillstånd behöver en skärm (t.ex. UPGRADE).
 */
public class OverlayHandler {
    UpgradeManager upgradeManager;
    List<Upgrades> upgrades;
    TextureAtlas textures;
    private boolean drawnUpgrade = false;

    private final int width;
    private final int height;

    public OverlayHandler(int width, int height, UpgradeManager upgradeManager, TextureAtlas textures) {
        this.width = width;
        this.height = height;
        this.upgradeManager = upgradeManager;
        this.textures = textures;
    }

    public void draw(GraphicsContext gc, GameState state) {
        switch (state) {
            case PAUSED    -> drawPaused(gc);
            case GAME_OVER -> drawGameOver(gc);
            case UPGRADE -> drawUpgrade(gc);
            default        -> { /* RUNNING / UPGRADE – rita ingenting än */ }
        }
    }

    private void drawPaused(GraphicsContext gc) {
        dimBackground(gc, 0.5);
        gc.setTextAlign(TextAlignment.CENTER);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Times New Roman", 64));
        gc.fillText("PAUSED", width / 2.0, height / 2.0 - 20);

        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font("Arial", 22));
        gc.fillText("Press ESC to continue", width / 2.0, height / 2.0 + 30);
        gc.fillText("Press M to go back to main menu", width / 2.0, height / 2.0 + 65);
    }

    private void drawGameOver(GraphicsContext gc) {
        dimBackground(gc, 0.65);
        gc.setTextAlign(TextAlignment.CENTER);

        gc.setFill(Color.web("#FF4444"));
        gc.setFont(Font.font("Times New Roman", 72));
        gc.fillText("YOU LOST!", width / 2.0, height / 2.0 - 30);

        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font("Arial", 22));
        gc.fillText("Press R to play again", width / 2.0, height / 2.0 + 30);
        gc.fillText("Press M to go back to main menu", width / 2.0, height / 2.0 + 65);
    }

    public void drawUpgrade(GraphicsContext gc) {
        if (!drawnUpgrade) {
            upgrades = upgradeManager.rollThree();
        }

        dimBackground(gc, 0.65);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Times New Roman", 72));
        gc.fillText("Choose an upgrade", width / 2.0, height / 5.0);

        int cardWidth = width / 4;
        int cardHeight = height / 2;
        int spacing = width / 8;
        int totalWidth = 3*cardWidth + 2*spacing;
        int startX = (width - totalWidth) / 2;
        int cardY = height / 3;

        for (int i = 0; i < upgrades.size(); i++) {
            int x = startX + i * (cardWidth + spacing);
            drawCard(gc, upgrades.get(i), x, cardY, cardWidth, cardHeight);
        }
        drawnUpgrade = true;
    }

    private void drawCard(GraphicsContext gc, Upgrades upgrade, int x, int y, int width, int height) {
        // Creates a background with a gradient from the card's rarity colors
        LinearGradient background = new LinearGradient(0, y, 0, y + height, false,
                CycleMethod.NO_CYCLE, new Stop(0,
                Color.web(upgrade.getRarity().getGradientStart())), new Stop(1,
                Color.web(upgrade.getRarity().getGradientEnd())));
        gc.setFill(background);

        // Rounded rectangles and stroke
        gc.fillRoundRect(x, y, width, height, 32, 32);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRoundRect(x, y, width, height, 32, 32);

        // Icon
        Image icon = textures.getUpgradeIcon(upgrade.getId());
        int iconSize = 128;
        gc.drawImage(icon, x + (width - iconSize) / 2, y + 100, iconSize, iconSize);

        // Cardname
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(upgrade.name(), x + width / 2, y + 60);

        // Description
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Times New Roman", 20));
        String[] lines = upgrade.getDescription().split("\n");
        for (int i = 0; i < lines.length; i++) {
            gc.fillText(lines[i], x + width / 2, y + 250 + i * 30);
        }
    }

    public void setUpgradeManager(UpgradeManager upgradeManager) {
        this.upgradeManager = upgradeManager;
    }

    private void dimBackground(GraphicsContext gc, double opacity) {
        gc.setFill(Color.color(0, 0, 0, opacity));
        gc.fillRect(0, 0, width, height);
    }
}
