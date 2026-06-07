package view;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import model.managers.UpgradeManager;
import model.upgrades.Upgrades;
import model.world.GameState;
import util.images.TextureAtlas;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OverlayHandler {

    private final int width;
    private final int height;
    private final double resolutionScale;
    private final UpgradeManager upgradeManager;
    private final TextureAtlas textures;

    private final List<Rectangle2D> cards = new ArrayList<>();
    private List<Upgrades> upgrades;
    private boolean drawnUpgrade;
    private double mouseX;
    private double mouseY;

    // Score & initials entry state
    private int displayedScore = 0;
    private boolean showInitialsEntry = false;
    private final char[] initials = {'A', 'A', 'A'};
    private int cursorPos = 0;
    private boolean initialsConfirmed = false;
    private Font pixelFont;
    private Font pixelFontLarge;
    private Font pixelFontSmall;

    public OverlayHandler(int width, int height, double resolutionScale,
                          UpgradeManager upgradeManager, TextureAtlas textures) {
        this.width = width;
        this.height = height;
        this.resolutionScale = resolutionScale;
        this.upgradeManager = upgradeManager;
        this.textures = textures;
        loadPixelFonts();
    }

    private void loadPixelFonts() {
        InputStream fontStream = getClass().getResourceAsStream("/util/fonts/PressStart2P.ttf");
        if (fontStream != null) {
            Font loaded = Font.loadFont(fontStream, 20 * resolutionScale);
            if (loaded != null) {
                pixelFont = loaded;
                // Reload at different sizes
                fontStream = getClass().getResourceAsStream("/util/fonts/PressStart2P.ttf");
                pixelFontLarge = Font.loadFont(fontStream, 40 * resolutionScale);
                fontStream = getClass().getResourceAsStream("/util/fonts/PressStart2P.ttf");
                pixelFontSmall = Font.loadFont(fontStream, 14 * resolutionScale);
                return;
            }
        }
        pixelFont = Font.font("Monospaced", 20 * resolutionScale);
        pixelFontLarge = Font.font("Monospaced", 40 * resolutionScale);
        pixelFontSmall = Font.font("Monospaced", 14 * resolutionScale);
    }

    public void draw(GraphicsContext gc, GameState state) {
        switch (state) {
            case PAUSED -> drawPaused(gc);
            case GAME_OVER -> drawGameOver(gc);
            case UPGRADE -> drawUpgrade(gc);
            default -> { }
        }
    }

    private void drawPaused(GraphicsContext gc) {
        drawCenteredOverlay(gc, 0.5,
                "PAUSED", 64, Color.WHITE, height / 2.0 - 20 * resolutionScale,
                new String[]{"Press ESC to continue", "Press M to go back to main menu"},
                height / 2.0 + 30 * resolutionScale);
    }

    private void drawGameOver(GraphicsContext gc) {
        dimBackground(gc, 0.65);
        gc.setTextAlign(TextAlignment.CENTER);

        double centerX = width / 2.0;
        double baseY = height * 0.25;

        // "YOU LOST!" title
        gc.setFill(Color.web("#FF4444"));
        gc.setFont(pixelFontLarge);
        gc.fillText("YOU LOST!", centerX, baseY);

        // Score display
        gc.setFill(Color.WHITE);
        gc.setFont(pixelFont);
        gc.fillText("SCORE", centerX, baseY + 60 * resolutionScale);

        gc.setFill(Color.GOLD);
        gc.setFont(pixelFontLarge);
        gc.fillText(formatScore(displayedScore), centerX, baseY + 110 * resolutionScale);

        if (showInitialsEntry && !initialsConfirmed) {
            gc.setFill(Color.web("#FFD700"));
            gc.setFont(pixelFont);
            gc.fillText("NEW HIGH SCORE!", centerX, baseY + 160 * resolutionScale);

            gc.setFill(Color.LIGHTGRAY);
            gc.setFont(pixelFontSmall);
            gc.fillText("ENTER YOUR INITIALS", centerX, baseY + 200 * resolutionScale);

            // Draw the 3 initial slots
            double slotSpacing = 60 * resolutionScale;
            double initialsY = baseY + 260 * resolutionScale;
            double startX = centerX - slotSpacing;

            gc.setFont(pixelFontLarge);
            for (int i = 0; i < 3; i++) {
                double slotX = startX + i * slotSpacing;

                // Up arrow indicator for selected slot
                if (i == cursorPos) {
                    gc.setFill(Color.web("#44FFCC"));
                    gc.setFont(pixelFontSmall);
                    gc.fillText("^", slotX, initialsY - 35 * resolutionScale);
                    gc.setFont(pixelFontLarge);
                }

                // The letter
                gc.setFill(i == cursorPos ? Color.web("#44FFCC") : Color.WHITE);
                gc.fillText(String.valueOf(initials[i]), slotX, initialsY);

                // Down arrow indicator for selected slot
                if (i == cursorPos) {
                    gc.setFill(Color.web("#44FFCC"));
                    gc.setFont(pixelFontSmall);
                    gc.fillText("v", slotX, initialsY + 25 * resolutionScale);
                    gc.setFont(pixelFontLarge);
                }

                // Underline
                double underY = initialsY + 8 * resolutionScale;
                double underW = 30 * resolutionScale;
                gc.setStroke(i == cursorPos ? Color.web("#44FFCC") : Color.gray(0.5));
                gc.setLineWidth(3 * resolutionScale);
                gc.strokeLine(slotX - underW / 2, underY, slotX + underW / 2, underY);
            }

            // Instructions
            gc.setFill(Color.LIGHTGRAY);
            gc.setFont(pixelFontSmall);
            double instrY = initialsY + 60 * resolutionScale;
            gc.fillText("UP/DOWN = Change letter", centerX, instrY);
            gc.fillText("LEFT/RIGHT = Move slot", centerX, instrY + 25 * resolutionScale);
            gc.fillText("ENTER = Confirm", centerX, instrY + 50 * resolutionScale);
        } else {
            // Normal hints (after confirm or if doesn't qualify)
            double hintsY = showInitialsEntry
                    ? baseY + 180 * resolutionScale
                    : baseY + 160 * resolutionScale;
            gc.setFill(Color.LIGHTGRAY);
            gc.setFont(pixelFontSmall);
            gc.fillText("Press R to restart", centerX, hintsY);
            gc.fillText("Press M to go back to main menu", centerX, hintsY + 25 * resolutionScale);
        }
    }

    private String formatScore(int score) {
        return String.format("%,d", score);
    }

    public void setGameOverScore(int score, boolean qualifies) {
        this.displayedScore = score;
        this.showInitialsEntry = qualifies;
        this.initialsConfirmed = false;
        this.cursorPos = 0;
        this.initials[0] = 'A';
        this.initials[1] = 'A';
        this.initials[2] = 'A';
    }

    public void cycleInitialUp() {
        if (initialsConfirmed) return;
        initials[cursorPos]--;
        if (initials[cursorPos] < 'A') initials[cursorPos] = 'Z';
    }

    public void cycleInitialDown() {
        if (initialsConfirmed) return;
        initials[cursorPos]++;
        if (initials[cursorPos] > 'Z') initials[cursorPos] = 'A';
    }

    public void moveCursorLeft() {
        if (initialsConfirmed) return;
        cursorPos = Math.max(0, cursorPos - 1);
    }

    public void moveCursorRight() {
        if (initialsConfirmed) return;
        cursorPos = Math.min(2, cursorPos + 1);
    }

    public String confirmInitials() {
        if (initialsConfirmed) return null;
        initialsConfirmed = true;
        return new String(initials);
    }

    public boolean isInitialsEntryActive() {
        return showInitialsEntry && !initialsConfirmed;
    }

    public boolean isInitialsConfirmed() {
        return initialsConfirmed;
    }

    private void drawUpgrade(GraphicsContext gc) {
        if (!drawnUpgrade) {
            upgrades = upgradeManager.rollThree();
        }

        drawCenteredOverlay(gc, 0.65,
                "Choose an upgrade", 72, Color.WHITE, height / 5.0,
                null, 0);

        int cardWidth = width / 4;
        int cardHeight = height / 2;
        int spacing = width / 12;
        int totalWidth = 3 * cardWidth + 2 * spacing;
        int startX = (width - totalWidth) / 2;
        int cardY = height / 3;

        if (cards.isEmpty()) {
            for (int i = 0; i < 3; i++) {
                int x = startX + i * (cardWidth + spacing);
                cards.add(new Rectangle2D(x, cardY, cardWidth, cardHeight));
            }
        }

        for (int i = 0; i < upgrades.size(); i++) {
            int x = startX + i * (cardWidth + spacing);
            drawCard(gc, upgrades.get(i), x, cardY, cardWidth, cardHeight, i);
        }
        drawnUpgrade = true;
    }

    private void drawCenteredOverlay(GraphicsContext gc, double dimOpacity,
                                     String title, double titleSize, Color titleColor, double titleY,
                                     String[] hints, double hintsY) {
        dimBackground(gc, dimOpacity);
        gc.setTextAlign(TextAlignment.CENTER);

        gc.setFill(titleColor);
        gc.setFont(Font.font("Times New Roman", titleSize * resolutionScale));
        gc.fillText(title, width / 2.0, titleY);

        if (hints != null) {
            gc.setFill(Color.LIGHTGRAY);
            gc.setFont(Font.font("Arial", 22 * resolutionScale));
            for (int i = 0; i < hints.length; i++) {
                gc.fillText(hints[i], width / 2.0, hintsY + i * 35 * resolutionScale);
            }
        }
    }

    private void drawCard(GraphicsContext gc, Upgrades upgrade, int x, int y,
                          int cardWidth, int cardHeight, int index) {
        boolean hovered = cards.get(index).contains(mouseX, mouseY);

        LinearGradient background = new LinearGradient(0, y, 0, y + cardHeight, false,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(upgrade.getRarity().getGradientStart())),
                new Stop(1, Color.web(upgrade.getRarity().getGradientEnd())));
        gc.setFill(background);

        double cornerRadius = 32 * resolutionScale;
        gc.fillRoundRect(x, y, cardWidth, cardHeight, cornerRadius, cornerRadius);

        Light.Point light = new Light.Point();
        light.setX(mouseX - x);
        light.setY(mouseY - y);
        light.setZ(500);
        light.setColor(Color.web(upgrade.getRarity().getLightColor()));

        Lighting lighting = new Lighting(light);
        lighting.setSurfaceScale(5.0);
        lighting.setDiffuseConstant(1.5);

        gc.setEffect(lighting);
        gc.setGlobalAlpha(0.65);
        gc.fillRoundRect(x, y, cardWidth, cardHeight, cornerRadius, cornerRadius);
        gc.setGlobalAlpha(1.0);

        gc.setStroke(hovered ? Color.WHITE : Color.BLACK);
        gc.setLineWidth(5);
        gc.strokeRoundRect(x, y, cardWidth, cardHeight, cornerRadius, cornerRadius);
        gc.setEffect(null);

        // Icon
        Image icon = textures.getUpgradeIcon(upgrade.getId());
        int iconSize = (int) (128 * resolutionScale);
        gc.drawImage(icon, x + (cardWidth - iconSize) / 2.0, y + 100 * resolutionScale, iconSize, iconSize);

        // Card name
        gc.setFont(Font.font("Montserrat Black", FontWeight.BOLD, 40 * resolutionScale));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(8 * resolutionScale);
        gc.strokeText(upgrade.name(), x + cardWidth / 2.0, y + 60 * resolutionScale);
        gc.setFill(hovered ? Color.web(upgrade.getRarity().getLightColor()) : Color.WHITE);
        gc.fillText(upgrade.name(), x + cardWidth / 2.0, y + 60 * resolutionScale);

        // Rarity
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20 * resolutionScale));
        gc.fillText(upgrade.getRarity().name(), x + cardWidth / 2.0, y + 90 * resolutionScale);

        // Description
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Times New Roman", 20 * resolutionScale));
        String[] lines = upgrade.getDescription().split("\n");
        for (int i = 0; i < lines.length; i++) {
            gc.fillText(lines[i], x + cardWidth / 2.0, y + 250 * resolutionScale + i * 30 * resolutionScale);
        }
    }

    private void dimBackground(GraphicsContext gc, double opacity) {
        gc.setFill(Color.color(0, 0, 0, opacity));
        gc.fillRect(0, 0, width, height);
    }

    public Upgrades getClickedCard() {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).contains(mouseX, mouseY)) {
                drawnUpgrade = false;
                cards.clear();
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
