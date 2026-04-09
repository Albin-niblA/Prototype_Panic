package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import model.GameState;

/**
 * Ritar halvtransparenta överläggsmenyer ovanpå spelscenen.
 *
 * Anropas från GamePanel.render() med aktuellt GameState.
 * Lägg till nya case här när fler tillstånd behöver en skärm (t.ex. UPGRADE).
 */
public class PauseOverlay {

    private final int width;
    private final int height;

    public PauseOverlay(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void draw(GraphicsContext gc, GameState state) {
        switch (state) {
            case PAUSED    -> drawPaused(gc);
            case GAME_OVER -> drawGameOver(gc);
            default        -> { /* RUNNING / UPGRADE – rita ingenting än */ }
        }
    }

    private void drawPaused(GraphicsContext gc) {
        dimBackground(gc, 0.5);
        gc.setTextAlign(TextAlignment.CENTER);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 64));
        gc.fillText("PAUSAD", width / 2.0, height / 2.0 - 20);

        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font("Arial", 22));
        gc.fillText("Tryck ESC för att fortsätta", width / 2.0, height / 2.0 + 30);
        gc.fillText("Tryck M för huvudmenyn", width / 2.0, height / 2.0 + 65);
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
}
