package view;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.EnemyHandler;
import model.GameState;
import model.Player;
import model.Projectile;
import model.ProjectileManager;
import util.sounds.SoundManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GamePanel {
    private final Stage stage;
    private final Scene scene;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final EnemyHandler eh = new EnemyHandler(WIDTH, HEIGHT);
    private final ProjectileManager pm = new ProjectileManager(WIDTH, HEIGHT);
    private int projectileTexture = 0;
    private double spawnTimer = 0;
    private static final double SPAWN_INTERVAL = 3.0;

    private final Player player = new Player(WIDTH / 2.0, HEIGHT / 2.0);
    private final List<Projectile> projectiles = new ArrayList<>();
    Random rand = new Random();
    int type = rand.nextInt(2);

    private double mouseX = 0;
    private double mouseY = 0;
    private boolean shooting = false;

    private double shootCooldown = 0.2;
    private static final double SHOOT_INTERVAL = 0.02;

    private GameState gameState = GameState.RUNNING;
    private final PauseOverlay pauseOverlay = new PauseOverlay(WIDTH, HEIGHT);
    private Runnable onReturnToMenu;

    public GamePanel(Stage stage, int initialWeapon) {
        this.stage = stage;
        this.projectileTexture = initialWeapon;
        SoundManager.init();

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);

        StackPane root = new StackPane(canvas);
        scene = new Scene(root, WIDTH, HEIGHT);

        setupInput(scene);

        GameLoop gameLoop = new GameLoop(gc);
        gameLoop.start();
    }

    private void setupInput(Scene scene) {
        scene.setOnKeyPressed(e -> {
            KeyCode k = e.getCode();

            if (k == KeyCode.ESCAPE) {
                if (gameState == GameState.RUNNING) pause();
                else if (gameState == GameState.PAUSED) resume();
                return;
            }

            if (k == KeyCode.M && (gameState == GameState.PAUSED || gameState == GameState.GAME_OVER)) {
                returnToMenu();
                return;
            }

            if (k == KeyCode.R && gameState == GameState.GAME_OVER) {
                resetGame();
                return;
            }

            if (gameState != GameState.RUNNING) return;

            if (k == KeyCode.W) player.setMoveUp(true);
            if (k == KeyCode.S) player.setMoveDown(true);
            if (k == KeyCode.A) player.setMoveLeft(true);
            if (k == KeyCode.D) player.setMoveRight(true);
            if (k == KeyCode.Q) {
                if (projectileTexture >= pm.getPROJECTILE_TEXTURES_COUNT() - 1) {
                    projectileTexture = 0;
                } else {
                    projectileTexture++;
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            KeyCode k = e.getCode();
            if (k == KeyCode.W) player.setMoveUp(false);
            if (k == KeyCode.S) player.setMoveDown(false);
            if (k == KeyCode.A) player.setMoveLeft(false);
            if (k == KeyCode.D) player.setMoveRight(false);
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
            if (e.getButton() == MouseButton.PRIMARY && gameState == GameState.RUNNING)
                shooting = !shooting;
        });

        eh.spawnRandom();
    }

    // --- Paus-hantering ---------------------------------------------------
    // pause() och resume() är publika så de kan anropas utifrån senare,
    // t.ex. från en upgrade-skärm eller ett pause-menyval.

    public void pause() {
        if (gameState == GameState.RUNNING) {
            gameState = GameState.PAUSED;
            shooting = false;
            player.setMoveUp(false);
            player.setMoveDown(false);
            player.setMoveLeft(false);
            player.setMoveRight(false);
        }
    }

    public void resume() {
        if (gameState == GameState.PAUSED) {
            gameState = GameState.RUNNING;
        }
    }

    /** Återuppta från ett godtyckligt tillstånd – används t.ex. av upgrade-skärmen. */
    public void resumeFrom(GameState expected) {
        if (gameState == expected) gameState = GameState.RUNNING;
    }

    public GameState getGameState() { return gameState; }

    public void setOnReturnToMenu(Runnable callback) { this.onReturnToMenu = callback; }

    private void returnToMenu() {
        resetGame();
        if (onReturnToMenu != null) onReturnToMenu.run();
    }
    // ----------------------------------------------------------------------

    private void update(double delta) {
        if (gameState != GameState.RUNNING) return;

        player.update(delta, WIDTH, HEIGHT);

        if (shootCooldown > 0) shootCooldown -= delta;

        if (shooting && shootCooldown <= 0) {
            pm.addProjectile(player.getX(), player.getY(), 10, mouseX, mouseY, 1000, projectileTexture, 0);
            shootCooldown = SHOOT_INTERVAL;
            SoundManager.playShoot();
        }

        pm.update(delta);
        eh.update(delta, player.getX(), player.getY());
        checkCollisions();
        spawnTimer += delta;
        if (spawnTimer >= SPAWN_INTERVAL) {
            eh.spawnRandom();
            spawnTimer = 0;
        }
    }

    private void checkCollisions() {
        for (int i = 0; i < pm.getCount(); i++) {
            double px = pm.getX(i);
            double py = pm.getY(i);
            double pr = pm.getRadius(i);

            if (eh.checkHit(px, py, pr)) {
                pm.deleteProjectile(i--);
            }
        }

        if (eh.checkPlayerHit(player.getX(), player.getY(), player.getSize() / 2)) {
            gameState = GameState.GAME_OVER;
            shooting = false;
        }
    }

    private void resetGame() {
        player.reset(WIDTH / 2.0, HEIGHT / 2.0);
        pm.clear();
        eh.clear();
        eh.spawnRandom();
        shootCooldown = 0;
        shooting = false;
        spawnTimer = 0;
        gameState = GameState.RUNNING;
    }


    private void render(GraphicsContext gc) {
        // Bakgrund
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Grid
        gc.setStroke(Color.GREY);
        gc.setLineWidth(1);
        for (int x = 0; x < WIDTH;  x += 60) gc.strokeLine(x, 0, x, HEIGHT);
        for (int y = 0; y < HEIGHT; y += 60) gc.strokeLine(0, y, WIDTH, y);

        // Projektiler
        pm.drawAll(gc);

        // Spelare
        player.draw(gc);
        eh.drawAll(gc);

        // Overlay – ritas sist ovanpå allt (PAUSED, GAME_OVER, osv.)
        pauseOverlay.draw(gc, gameState);
    }

    public void show() {
        stage.setTitle("Prototype Panic");
        stage.setScene(scene);
        stage.show();
    }

    private class GameLoop extends AnimationTimer {
        private final GraphicsContext gc;
        private long lastTime = -1;

        GameLoop(GraphicsContext gc) {
            this.gc = gc;
        }

        @Override
        public void handle(long now) {
            if (lastTime < 0) {
                lastTime = now;
                return;
            }
            double delta = (now - lastTime) / 1_000_000_000.0;
            lastTime = now;
            update(delta);
            render(gc);
        }
    }
}
