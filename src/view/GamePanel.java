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
import model.Player;
import model.Projectile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePanel {
    private final Stage stage;
    private final Scene scene;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    private final Player player = new Player(WIDTH / 2.0, HEIGHT / 2.0);
    private final List<Projectile> projectiles = new ArrayList<>();

    private double mouseX = 0;
    private double mouseY = 0;
    private boolean shooting = false;

    private double shootCooldown = 0;
    private static final double SHOOT_INTERVAL = 0.18;

    public GamePanel(Stage stage) {
        this.stage = stage;

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane(canvas);
        scene = new Scene(root, WIDTH, HEIGHT);

        setupInput(scene);

        GameLoop gameLoop = new GameLoop(gc);
        gameLoop.start();
    }

    private void setupInput(Scene scene) {
        scene.setOnKeyPressed(e -> {
            KeyCode k = e.getCode();
            if (k == KeyCode.W) player.setMoveUp(true);
            if (k == KeyCode.S) player.setMoveDown(true);
            if (k == KeyCode.A) player.setMoveLeft(true);
            if (k == KeyCode.D) player.setMoveRight(true);
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

        scene.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) shooting = true;
        });

        scene.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY) shooting = false;
        });
    }

    private void update(double delta) {
        player.update(delta, WIDTH, HEIGHT);

        if (shootCooldown > 0) shootCooldown -= delta;

        if (shooting && shootCooldown <= 0) {
            projectiles.add(new Projectile(player.getX(), player.getY(), mouseX, mouseY));
            shootCooldown = SHOOT_INTERVAL;
        }

        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();
            p.update(delta, WIDTH, HEIGHT);
            if (p.isDead()) it.remove();
        }
    }

    private void render(GraphicsContext gc) {
        // Bakgrund

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Projektiler
        for (Projectile p : projectiles) p.draw(gc);

        // Spelare
        player.draw(gc);
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
