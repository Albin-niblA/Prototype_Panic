package controller;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.world.Camera;
import model.world.GameState;
import model.world.GameWorld;
import model.weapon.WeaponType;
import model.managers.SoundManager;
import view.GameRenderer;

public class GameController {
    private final Stage stage;
    private final int viewportWidth;
    private final int viewportHeight;

    private final GameWorld world;
    private final InputHandler input;
    private final GameRenderer renderer;
    private final Camera camera;

    private final AnimationTimer gameLoop;
    private final GraphicsContext gc;
    private long lastTime = -1;
    private Runnable onReturnToMenu;

    public GameController(Stage stage, int width, int height, WeaponType weaponType) {
        this.stage = stage;
        this.viewportWidth = width;
        this.viewportHeight = height;

        SoundManager.init();

        this.camera = new Camera(width, height);
        this.world = new GameWorld(weaponType);
        this.input = new InputHandler();
        this.renderer = new GameRenderer(width, height, camera, world.getUpgradeManager());

        Canvas canvas = new Canvas(width, height);
        this.gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, width, height);
        input.attachTo(scene);

        stage.setScene(scene);
        stage.setTitle("Prototype Panic");
        stage.setResizable(false);

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime < 0) {
                    lastTime = now;
                    return;
                }
                double delta = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                handleStateInput();
                world.update(delta, input, camera, now);
                camera.follow(world.getPlayer());
                renderer.render(gc, world);
                input.clearFrameState();
            }
        };
    }

    private void handleStateInput() {
        if (input.wasPressed(KeyCode.ESCAPE)) {
            if (world.getState() == GameState.RUNNING) {
                world.pause();
            } else if (world.getState() == GameState.PAUSED) {
                world.resume();
            }
        }

        if (input.wasPressed(KeyCode.M) &&
            (world.getState() == GameState.PAUSED || world.getState() == GameState.GAME_OVER)) {
            world.reset();
            gameLoop.stop();
            if (onReturnToMenu != null) onReturnToMenu.run();
            return;
        }

        if (input.wasPressed(KeyCode.R) && world.getState() == GameState.GAME_OVER) {
            world.reset();
        }

        if (input.wasPressed(KeyCode.F4)) {
            world.upgrade();
        }

        if (input.wasMouseClicked()) {
            world.toggleShooting();
        }
    }

    public void setOnReturnToMenu(Runnable callback) {
        this.onReturnToMenu = callback;
    }

    public void start() {
        stage.show();
        gameLoop.start();
    }

    public void stop() {
        gameLoop.stop();
    }
}
