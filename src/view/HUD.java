package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import model.entities.Player;
import model.world.GameWorld;
import util.images.TextureAtlas;
import util.settings.ControlScheme;
import util.settings.ControlSettings;

import java.io.InputStream;

public class HUD {

    private static final double TOOLTIP_DURATION = 12.0;
    private static final double TOOLTIP_FADE_START = 3.0;

    private final int screenWidth;
    private final int screenHeight;
    private final double resolutionScale;
    private final Image coinAsset;

    private double tooltipTimer = TOOLTIP_DURATION;
    private Font tooltipFont;
    private Font tooltipFontSmall;

    public HUD(int screenWidth, int screenHeight, double resolutionScale, TextureAtlas textures) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.resolutionScale = resolutionScale;
        coinAsset = textures.getAsset(0);
        loadTooltipFonts();
    }

    private void loadTooltipFonts() {
        InputStream fontStream = getClass().getResourceAsStream("/util/fonts/PressStart2P.ttf");
        if (fontStream != null) {
            Font font = Font.loadFont(fontStream, 14 * resolutionScale);
            if (font != null) {
                tooltipFont = font;
                fontStream = getClass().getResourceAsStream("/util/fonts/PressStart2P.ttf");
                tooltipFontSmall = Font.loadFont(fontStream, 10 * resolutionScale);
                return;
            }
        }
        tooltipFont = Font.font("Monospaced", 14 * resolutionScale);
        tooltipFontSmall = Font.font("Monospaced", 10 * resolutionScale);
    }

    public void showTooltip() {
        tooltipTimer = TOOLTIP_DURATION;
    }

    public void draw(GraphicsContext gc, GameWorld world, double blinkMax, double blinkCurrent, double delta) {
        drawBlinkBar(gc, blinkMax, blinkCurrent);
        drawHealthBar(gc, world.getPlayer());
        drawWaveInfo(gc, world);
        drawXpBar(gc, world.getPlayer());
        drawCoins(gc, world);
        drawTooltip(gc, delta);
    }

    private void drawBlinkBar(GraphicsContext gc, double blinkMax, double blinkCurrent) {
        if (blinkCurrent > 0) {
            double barWidth = screenWidth / 5.0;
            double barHeight = screenHeight / 30.0;
            double barX = (screenWidth / 2) - (barWidth/2);
            double barY = screenHeight - screenHeight / 20.0;
            double blinkPct = blinkCurrent/blinkMax;

            gc.setFill(Color.DARKGRAY);
            gc.fillRect(barX, barY, barWidth, barHeight);

            Color blinkColor = Color.MAGENTA;

            gc.setFill(blinkColor);
            gc.fillRect(barX, barY, barWidth * blinkPct, barHeight);

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeRect(barX, barY, barWidth, barHeight);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", 16 * resolutionScale));
            gc.fillText("Blink Cooldown",
                    barX + 10, barY + barHeight * 0.6);
        }
    }

    private void drawHealthBar(GraphicsContext gc, Player p) {
        double barWidth = screenWidth / 5.0;
        double barHeight = screenHeight / 30.0;
        double barX = screenWidth / 70.0;
        double barY = screenHeight - screenHeight / 20.0;
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
        gc.setFont(Font.font("Arial", 16 * resolutionScale));
        gc.fillText("HP: " + p.getHealth() + " / " + p.getMaxHealth(),
                barX + 10, barY + barHeight * 0.6);
    }

    private void drawWaveInfo(GraphicsContext gc, GameWorld world) {
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", 22 * resolutionScale));
        double rightMargin = screenWidth - 200 * resolutionScale;
        gc.fillText("Wave: " + world.getWaveManager().getCurrentWave(),
                rightMargin, 35 * resolutionScale);
        gc.fillText("Enemies: " + world.getEnemyHandler().getCount(),
                rightMargin, 65 * resolutionScale);
    }

    private void drawCoins(GraphicsContext gc, GameWorld world) {
        int coins = world.getCoinManager().getBalance();
        String text = "Coins: " + coins;

        // Samma X som HP/XP-barerna, precis ovanför XP-baren
        double barX      = screenWidth / 70.0;
        double barHeight = screenHeight / 30.0;
        double xpBarY    = screenHeight - screenHeight / 10.0;
        double coinY     = xpBarY - barHeight * 0.3; // lite ovanför XP-baren

        gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT);
        gc.setFont(javafx.scene.text.Font.font("Arial",
                javafx.scene.text.FontWeight.BOLD, 18 * resolutionScale));

        // Svart skugga för läsbarhet
        gc.drawImage(coinAsset, (double) screenWidth / 110, coinY * 0.89, 100 * resolutionScale, 100 * resolutionScale);
        gc.setFill(Color.BLACK);
        gc.fillText(text, barX + 1, coinY + 1);

        // Gul text
        gc.setFill(Color.GOLD);
        gc.fillText(text, barX, coinY);
    }

    private void drawXpBar(GraphicsContext gc, Player p){
        double barWidth = screenWidth / 5.0;
        double barHeight = screenHeight / 30.0;
        double barX = screenWidth / 70.0;
        double barY = screenHeight - screenHeight / 10.0;
        double xpPct = (double) p.getXp()/p.getXpRequired();

        gc.setFill(Color.BLACK);
        gc.fillRect(barX, barY, barWidth, barHeight);
        gc.setFill(Color.PURPLE);
        gc.fillRect(barX, barY, barWidth * xpPct, barHeight);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(barX, barY, barWidth, barHeight);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 16 * resolutionScale));
        gc.fillText("LVL " + p.getLevel() + "  XP: " + p.getXp() + " / " + p.getXpRequired(),
                barX + 10, barY + barHeight * 0.75);

    }

    private void drawTooltip(GraphicsContext gc, double delta) {
        if (tooltipTimer <= 0) return;

        tooltipTimer -= delta;
        if (tooltipTimer < 0) tooltipTimer = 0;

        double alpha;
        if (tooltipTimer > TOOLTIP_FADE_START) {
            alpha = 0.7;
        } else {
            alpha = 0.7 * (tooltipTimer / TOOLTIP_FADE_START);
        }

        if (alpha <= 0) return;

        boolean wasd = ControlSettings.getScheme() == ControlScheme.WASD;
        String moveLine = wasd ? "MOVE: W A S D" : "MOVE: ARROW KEYS";
        String shootLine = "SHOOT: MOUSE 1 (toggle)";

        double padding = 16 * resolutionScale;
        double lineHeight = 22 * resolutionScale;
        double boxWidth = 320 * resolutionScale;
        double boxHeight = padding * 2 + lineHeight * 2 + 8 * resolutionScale;
        double boxX = (screenWidth - boxWidth) / 2.0;
        double boxY = screenHeight * 0.3 - boxHeight / 2.0;

        gc.save();
        gc.setGlobalAlpha(alpha);

        gc.setFill(Color.color(0, 0, 0, 0.6));
        gc.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 12 * resolutionScale, 12 * resolutionScale);

        gc.setStroke(Color.web("#66FF44"));
        gc.setLineWidth(1.5 * resolutionScale);
        gc.strokeRoundRect(boxX, boxY, boxWidth, boxHeight, 12 * resolutionScale, 12 * resolutionScale);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(tooltipFont);
        gc.setFill(Color.web("#66FF44"));
        gc.fillText(moveLine, screenWidth / 2.0, boxY + padding + 14 * resolutionScale);

        gc.setFont(tooltipFontSmall);
        gc.setFill(Color.web("#CCCCCC"));
        gc.fillText(shootLine, screenWidth / 2.0, boxY + padding + 14 * resolutionScale + lineHeight + 8 * resolutionScale);

        gc.restore();
    }
}
