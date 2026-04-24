package controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import util.settings.ControlScheme;
import util.settings.ControlSettings;

import java.util.HashSet;
import java.util.Set;

public class InputHandler {
    private final Set<KeyCode> heldKeys = new HashSet<>();
    private final Set<KeyCode> justPressed = new HashSet<>();
    private double mouseX;
    private double mouseY;
    private boolean mouseClicked = false;

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
        });

        scene.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                mouseClicked = true;
            }
        });
    }

    public boolean isHeld(KeyCode key) {
        return heldKeys.contains(key);
    }

    public boolean isMovingUp() {
        return ControlSettings.getScheme() == ControlScheme.WASD
                ? isHeld(KeyCode.W) : isHeld(KeyCode.UP);
    }

    public boolean isMovingDown() {
        return ControlSettings.getScheme() == ControlScheme.WASD
                ? isHeld(KeyCode.S) : isHeld(KeyCode.DOWN);
    }

    public boolean isMovingLeft() {
        return ControlSettings.getScheme() == ControlScheme.WASD
                ? isHeld(KeyCode.A) : isHeld(KeyCode.LEFT);
    }

    public boolean isMovingRight() {
        return ControlSettings.getScheme() == ControlScheme.WASD
                ? isHeld(KeyCode.D) : isHeld(KeyCode.RIGHT);
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
    }
}
