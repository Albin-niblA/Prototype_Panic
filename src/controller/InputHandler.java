package controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import view.PauseOverlay;

import java.util.HashSet;
import java.util.Set;

public class InputHandler {
    private final Set<KeyCode> heldKeys = new HashSet<>();
    private final Set<KeyCode> justPressed = new HashSet<>();
    private double mouseX;
    private double mouseY;
    private boolean mouseClicked = false;
    private boolean mouseDragging = false;


    private PauseOverlay overlay;
    private GameController controller;

    public void attachTo(Scene scene) {
        scene.setOnKeyPressed(e -> {
            heldKeys.add(e.getCode());
            justPressed.add(e.getCode());
        });

        scene.setOnKeyReleased(e -> {
            heldKeys.remove(e.getCode());
        });

        scene.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });

        scene.setOnMouseDragged(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
            mouseDragging = true;
            if (controller != null && controller.isPaused() && overlay != null) {
                overlay.handleMouseX(e.getX(), e.getY());
            }
        });

        scene.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (controller != null && controller.isPaused() && overlay != null) {
                    overlay.handleMouseClick(e.getX(), e.getY());
                } else {
                    mouseClicked = true;
                }
            }
        });
    }

    // Call this from GameController after creating both overlay and controller
    public void setOverlay(PauseOverlay overlay) {
        this.overlay = overlay;
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public boolean isHeld(KeyCode key) {
        return heldKeys.contains(key);
    }

    public boolean wasPressed(KeyCode key) {
        return justPressed.contains(key);
    }

    public boolean wasMouseClicked() {
        return mouseClicked;
    }

    public double getMouseX() { return mouseX; }
    public double getMouseY() { return mouseY; }

    public void clearFrameState() {
        justPressed.clear();
        mouseClicked = false;
        mouseDragging = false;
    }
}