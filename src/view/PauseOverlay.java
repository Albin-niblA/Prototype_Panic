package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import model.GameState;
import model.Settings;

public class PauseOverlay {

    private final int width;
    private final int height;

    // Slider drag state
    private boolean draggingVolume = false;
    private final double SLIDER_WIDTH = 300;
    private final double SLIDER_X_START;
    private final double SLIDER_Y;

    public PauseOverlay(int width, int height) {
        this.width = width;
        this.height = height;
        this.SLIDER_X_START = (width / 2.0) - SLIDER_WIDTH / 2;
        this.SLIDER_Y = height / 2.0 + 60;
    }

    public void draw(GraphicsContext gc, GameState state) {
        switch (state) {
            case PAUSED    -> drawPaused(gc);
            case GAME_OVER -> drawGameOver(gc);
            default        -> { }
        }
    }

    private void drawPaused(GraphicsContext gc) {
        dimBackground(gc, 0.5);
        gc.setTextAlign(TextAlignment.CENTER);

        // Title
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 64));
        gc.fillText("PAUSAD", width / 2.0, height / 2.0 - 100);

        // Volume label
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 22));
        gc.fillText("Volym: " + (int)(Settings.getVolume() * 100) + "%", width / 2.0, SLIDER_Y - 15);

        // Slider track
        gc.setFill(Color.DARKGRAY);
        gc.fillRoundRect(SLIDER_X_START, SLIDER_Y, SLIDER_WIDTH, 8, 8, 8);

        // Slider fill
        gc.setFill(Color.CORNFLOWERBLUE);
        gc.fillRoundRect(SLIDER_X_START, SLIDER_Y, SLIDER_WIDTH * Settings.getVolume(), 8, 8, 8);

        // Slider knob
        double knobX = SLIDER_X_START + SLIDER_WIDTH * Settings.getVolume();
        gc.setFill(Color.WHITE);
        gc.fillOval(knobX - 10, SLIDER_Y - 6, 20, 20);

        // Resolution options
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 22));
        gc.fillText("Upplösning:", width / 2.0, SLIDER_Y + 60);

        int[][] resolutions = {{800, 600}, {1280, 720}, {1600, 900}, {1920, 1080}};
        for (int i = 0; i < resolutions.length; i++) {
            int w = resolutions[i][0], h = resolutions[i][1];
            boolean selected = (w == Settings.getWidth() && h == Settings.getHeight());

            gc.setFill(selected ? Color.CORNFLOWERBLUE : Color.LIGHTGRAY);
            gc.fillRoundRect(width / 2.0 - 80, SLIDER_Y + 80 + i * 40, 160, 30, 8, 8);

            gc.setFill(selected ? Color.WHITE : Color.BLACK);
            gc.setFont(Font.font("Arial", 16));
            gc.fillText(w + " x " + h, width / 2.0, SLIDER_Y + 100 + i * 40);
        }

        // Controls hint
        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font("Arial", 18));
        gc.fillText("ESC – fortsätt   M – huvudmeny", width / 2.0, height - 40);
    }

    private void drawGameOver(GraphicsContext gc) {
        dimBackground(gc, 0.65);
        gc.setTextAlign(TextAlignment.CENTER);

        gc.setFill(Color.web("#FF4444"));
        gc.setFont(Font.font("Arial", 72));
        gc.fillText("DU FÖRLORADE", width / 2.0, height / 2.0 - 30);

        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font("Arial", 22));
        gc.fillText("Tryck R för att spela igen", width / 2.0, height / 2.0 + 30);
        gc.fillText("Tryck M för huvudmenyn", width / 2.0, height / 2.0 + 65);
    }

    private void dimBackground(GraphicsContext gc, double opacity) {
        gc.setFill(Color.color(0, 0, 0, opacity));
        gc.fillRect(0, 0, width, height);
    }

    // Call this from GamePanel on mouse pressed/dragged when paused
    public void handleMouseX(double mouseX, double mouseY) {
        if (mouseY >= SLIDER_Y - 10 && mouseY <= SLIDER_Y + 20) {
            double clamped = Math.max(SLIDER_X_START, Math.min(SLIDER_X_START + SLIDER_WIDTH, mouseX));
            double volume = (clamped - SLIDER_X_START) / SLIDER_WIDTH;
            Settings.setVolume(volume);
        }
    }

    // Call this from GamePanel on mouse clicked when paused
    public void handleMouseClick(double mouseX, double mouseY) {
        int[][] resolutions = {{800, 600}, {1280, 720}, {1600, 900}, {1920, 1080}};
        for (int i = 0; i < resolutions.length; i++) {
            double btnX = width / 2.0 - 80;
            double btnY = SLIDER_Y + 80 + i * 40;
            if (mouseX >= btnX && mouseX <= btnX + 160 && mouseY >= btnY && mouseY <= btnY + 30) {
                Settings.setResolution(resolutions[i][0], resolutions[i][1]);
            }
        }

        // Also check slider
        handleMouseX(mouseX, mouseY);
    }


}