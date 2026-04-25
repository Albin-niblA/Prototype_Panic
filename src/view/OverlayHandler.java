package view;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.*;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Ritar halvtransparenta överläggsmenyer ovanpå spelscenen.
 *
 * Anropas från GamePanel.render() med aktuellt GameState.
 * Lägg till nya case här när fler tillstånd behöver en skärm (t.ex. UPGRADE).
 */
public class OverlayHandler {
    private UpgradeManager upgradeManager;
    private List<Upgrades> upgrades;
    private TextureAtlas textures;
    private boolean drawnUpgrade = false;
    CardBounds cardBounds;
    List<Rectangle2D> cards = new ArrayList<>();

    private final int width;
    private final int height;
    private double resolutionScale;

    private double mouseX = 0;
    private double mouseY = 0;

    public OverlayHandler(int width, int height, double resolutionScale, UpgradeManager upgradeManager, TextureAtlas textures) {
        this.width = width;
        this.height = height;
        this.resolutionScale = resolutionScale;
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
        gc.setFont(Font.font("Times New Roman", 64 * resolutionScale));
        gc.fillText("PAUSED", width / 2.0, height / 2.0 - (20 * resolutionScale));

        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font("Arial", 22 * resolutionScale));
        gc.fillText("Press ESC to continue", width / 2.0, height / 2.0 + (30 * resolutionScale));
        gc.fillText("Press M to go back to main menu", width / 2.0, height / 2.0 + (65 * resolutionScale));
    }

    private void drawGameOver(GraphicsContext gc) {
        dimBackground(gc, 0.65);
        gc.setTextAlign(TextAlignment.CENTER);

        gc.setFill(Color.web("#FF4444"));
        gc.setFont(Font.font("Times New Roman", 72 * resolutionScale));
        gc.fillText("YOU LOST!", width / 2.0, height / 2.0 - (30 * resolutionScale));

        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font("Arial", 22 * resolutionScale));
        gc.fillText("Press R to play again", width / 2.0, height / 2.0 + (30 * resolutionScale));
        gc.fillText("Press M to go back to main menu", width / 2.0, height / 2.0 + (65 * resolutionScale));
    }

    public void drawUpgrade(GraphicsContext gc) {
        if (!drawnUpgrade) {
            upgrades = upgradeManager.rollThree();
        }

        dimBackground(gc, 0.65);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Times New Roman", 72 * resolutionScale));
        gc.fillText("Choose an upgrade", width / 2.0, height / 5.0);

        int cardWidth = width / 4;
        int cardHeight = height / 2;
        int spacing = width / 12;
        int totalWidth = 3*cardWidth + 2*spacing;
        int startX = (width - totalWidth) / 2;
        int cardY = height / 3;
        cardBounds = new CardBounds(cardWidth, cardHeight, spacing, totalWidth, startX, cardY);
        if (cards.isEmpty()) {
            for (int i = 0; i < 3; i++) {
                int x = cardBounds.startX + i * (cardBounds.cardWidth + cardBounds.spacing);
                cards.add(new Rectangle2D(x, cardBounds.cardY, cardBounds.cardWidth, cardBounds.cardHeight));
            }
        }

        for (int i = 0; i < upgrades.size(); i++) {
            int x = startX + i * (cardWidth + spacing);
            drawCard(gc, upgrades.get(i), x, cardY, cardWidth, cardHeight, i);
        }
        drawnUpgrade = true;
    }

    private class CardBounds {
        int cardWidth;
        int cardHeight;
        int spacing;
        int totalWidth;
        int startX;
        int cardY;

        public CardBounds(int cardWidth, int cardHeight, int spacing, int totalWidth, int startX, int cardY) {
            this.cardWidth = cardWidth;
            this.cardHeight = cardHeight;
            this.spacing = spacing;
            this.totalWidth = totalWidth;
            this.startX = startX;
            this.cardY = cardY;
        }
    }

    private void drawCard(GraphicsContext gc, Upgrades upgrade, int x, int y, int width, int height, int index) {
        boolean hovered = cards.get(index).contains(mouseX, mouseY);

        // Creates a background with a gradient from the card's rarity colors
        LinearGradient background = new LinearGradient(0, y, 0, y + height, false,
                CycleMethod.NO_CYCLE, new Stop(0,
                Color.web(upgrade.getRarity().getGradientStart())), new Stop(1,
                Color.web(upgrade.getRarity().getGradientEnd())));
        gc.setFill(background);

        // Rounded rectangles and stroke
        double strokeSize = 32*resolutionScale;
        gc.fillRoundRect(x, y, width, height, strokeSize, strokeSize);
        Light.Point light = new Light.Point(); // A light that centers around a point
        light.setX(mouseX - x);
        light.setY(mouseY - y);
        light.setZ(500); // Height of the light
        light.setColor(Color.web(upgrade.getRarity().getGradientStart()));

        Lighting lighting = new Lighting(light);
        lighting.setSurfaceScale(5.0); // How much depth

        gc.setEffect(lighting);
        gc.fillRoundRect(x, y, width, height, strokeSize, strokeSize);
        gc.setEffect(null);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(5);
        gc.strokeRoundRect(x, y, width, height, strokeSize, strokeSize);

        // Icon
        Image icon = textures.getUpgradeIcon(upgrade.getId());
        int iconSize = (int) (128 * resolutionScale);
        gc.drawImage(icon, x + (double) (width - iconSize) / 2, y + (100 * resolutionScale), iconSize, iconSize);

        // Cardname
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 40 * resolutionScale));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(upgrade.name(), x + width / 2, y + (60 * resolutionScale));

        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20 * resolutionScale));
        gc.fillText(upgrade.getRarity().name(), x + width / 2, y + (80 * resolutionScale));

        // Description
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Times New Roman", 20 * resolutionScale));
        String[] lines = upgrade.getDescription().split("\n");
        for (int i = 0; i < lines.length; i++) {
            gc.fillText(lines[i], x + (double) width / 2, y + (250 * resolutionScale) + i * (30 * resolutionScale));
        }
    }

    public void setUpgradeManager(UpgradeManager upgradeManager) {
        this.upgradeManager = upgradeManager;
    }

    private void dimBackground(GraphicsContext gc, double opacity) {
        gc.setFill(Color.color(0, 0, 0, opacity));
        gc.fillRect(0, 0, width, height);
    }


    // Returns the card if the mouse has been clicked on one
    public Upgrades getClickedCard() {
        for (int i = 0; i < 3; i++) {
            if (cards.get(i).contains(mouseX, mouseY)) {
                drawnUpgrade = false;
                return upgrades.get(i);
            }
        }
        return null;
    }

    public void setMouseCoords(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
