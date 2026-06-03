package view;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.Random;

public class CreditsMenu {

    private static final Color COLOR_DEFAULT = Color.web("#66FF44");
    private static final Color COLOR_HOVER_A = Color.web("#44FFCC");
    private static final Color COLOR_HOVER_B = Color.WHITE;
    private static final Color COLOR_OUTLINE = Color.BLACK;
    private static final Color COLOR_TITLE = Color.GREEN;
    private static final Color COLOR_ROLE = Color.web("#AAFFAA");
    private static final Color COLOR_NAME = Color.WHITE;
    private static final int STAR_COUNT = 500;

    private final Stage stage;
    private final Scene scene;
    private final AnimationTimer starAnimation;

    public CreditsMenu(Stage stage, Runnable onBack,
                       int width, int height, double resolutionScale) {
        this.stage = stage;

        Font titleFont = loadPixelFont(34 * resolutionScale);
        Font roleFont = loadPixelFont(14 * resolutionScale);
        Font nameFont = loadPixelFont(18 * resolutionScale);
        Font menuFont = loadPixelFont(20 * resolutionScale);

        Canvas starCanvas = new Canvas(width, height);
        GraphicsContext gc = starCanvas.getGraphicsContext2D();

        Random rand = new Random();
        double[] sx = new double[STAR_COUNT];
        double[] sy = new double[STAR_COUNT];
        double[] sSize = new double[STAR_COUNT];
        double[] sSpeed = new double[STAR_COUNT];
        double[] sPhase = new double[STAR_COUNT];
        double[] sBaseAlpha = new double[STAR_COUNT];
        double[] sTwinklePhase = new double[STAR_COUNT];
        double[] sTwinkleSpeed = new double[STAR_COUNT];

        for (int i = 0; i < STAR_COUNT; i++) {
            sx[i] = rand.nextDouble() * width;
            sy[i] = rand.nextDouble() * height;
            sSize[i] = 1 + rand.nextDouble() * 2.5;
            sSpeed[i] = 0.2 + rand.nextDouble() * 0.8;
            sPhase[i] = rand.nextDouble() * Math.PI * 2;
            sBaseAlpha[i] = 0.3 + rand.nextDouble() * 0.7;
            sTwinklePhase[i] = rand.nextDouble() * Math.PI * 2;
            sTwinkleSpeed[i] = 0.3 + rand.nextDouble() * 0.7;
        }

        double[] ssActive   = {0};
        double[] ssX        = {0};
        double[] ssY        = {0};
        double[] ssVx       = {0};
        double[] ssVy       = {0};
        double[] ssLife     = {0};
        double[] ssMaxLife  = {0};
        double[] ssCooldown = {rand.nextDouble() * 7 + 8};

        starAnimation = new AnimationTimer() {
            private long lastNano = 0;

            @Override
            public void handle(long now) {
                double dt = (lastNano == 0) ? 0 : (now - lastNano) / 1_000_000_000.0;
                lastNano = now;
                double timeSec = now / 1_000_000_000.0;

                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, width, height);

                for (int i = 0; i < STAR_COUNT; i++) {
                    sx[i] += sSpeed[i] * dt * 30;
                    sy[i] += sSpeed[i] * dt * 20;

                    if (sx[i] > width) sx[i] -= width;
                    if (sy[i] > height) sy[i] -= height;

                    double twinkle = 0.5 + 0.5 * Math.sin(timeSec * (1.5 + sSpeed[i]) + sPhase[i]);
                    double alpha = sBaseAlpha[i] * (0.3 + 0.7 * twinkle);

                    double bluePulse = Math.sin(timeSec * sTwinkleSpeed[i] + sTwinklePhase[i]);
                    if (bluePulse > 0.85) {
                        double blueIntensity = (bluePulse - 0.85) / 0.15;
                        gc.setFill(Color.color(0.7 + 0.3 * (1 - blueIntensity), 0.85 + 0.15 * (1 - blueIntensity), 1.0, Math.min(1.0, alpha + 0.3 * blueIntensity)));
                    } else {
                        gc.setFill(Color.gray(1.0, alpha));
                    }
                    gc.fillOval(sx[i], sy[i], sSize[i], sSize[i]);
                }

                if (ssActive[0] == 0) {
                    ssCooldown[0] -= dt;
                    if (ssCooldown[0] <= 0) {
                        ssActive[0] = 1;
                        ssMaxLife[0] = 0.5 + rand.nextDouble() * 0.5;
                        ssLife[0] = ssMaxLife[0];
                        if (rand.nextBoolean()) {
                            ssX[0] = rand.nextDouble() * width * 0.7;
                            ssY[0] = rand.nextDouble() * height * 0.3;
                        } else {
                            ssX[0] = rand.nextDouble() * width * 0.3;
                            ssY[0] = rand.nextDouble() * height * 0.5;
                        }
                        double speed = 300 + rand.nextDouble() * 200;
                        double angle = Math.toRadians(25 + rand.nextDouble() * 30);
                        ssVx[0] = speed * Math.cos(angle);
                        ssVy[0] = speed * Math.sin(angle);
                    }
                }
                if (ssActive[0] == 1) {
                    ssX[0] += ssVx[0] * dt;
                    ssY[0] += ssVy[0] * dt;
                    ssLife[0] -= dt;
                    if (ssLife[0] <= 0) {
                        ssActive[0] = 0;
                        ssCooldown[0] = 8 + rand.nextDouble() * 7;
                    } else {
                        int tailSegments = 20;
                        double progress = 1.0 - (ssLife[0] / ssMaxLife[0]);
                        double tailLength = 80 + 60 * (1.0 - progress);
                        double vLen = Math.sqrt(ssVx[0] * ssVx[0] + ssVy[0] * ssVy[0]);
                        double nx = ssVx[0] / vLen;
                        double ny = ssVy[0] / vLen;
                        double lifeFade = ssLife[0] < ssMaxLife[0] * 0.3
                                ? ssLife[0] / (ssMaxLife[0] * 0.3) : 1.0;

                        gc.save();
                        for (int t = 0; t < tailSegments; t++) {
                            double frac = t / (double) tailSegments;
                            double tx = ssX[0] - nx * tailLength * frac;
                            double ty = ssY[0] - ny * tailLength * frac;
                            double a = (1.0 - frac) * lifeFade;
                            double size = 3.0 * (1.0 - frac * 0.7);
                            double r = 1.0 - 0.3 * frac;
                            double g = 1.0 - 0.15 * frac;
                            double b = Math.min(1.0, 0.9 + 0.1 * frac);
                            gc.setGlobalAlpha(Math.max(0, Math.min(1.0, a)));
                            gc.setFill(Color.color(r, g, b));
                            gc.fillOval(tx - size / 2, ty - size / 2, size, size);
                        }
                        gc.restore();
                    }
                }
            }
        };

        // Credits content
        Text title = new Text("CREDITS");
        title.setFont(titleFont);
        title.setFill(COLOR_TITLE);

        Text dividerTop = new Text("______________________________");
        dividerTop.setFont(roleFont);
        dividerTop.setFill(Color.web("#44FF44"));

        VBox creditsBox = new VBox(30 * resolutionScale);
        creditsBox.setAlignment(Pos.CENTER);

        String[][] credits = {
                {"CREATORS"},
                {"Adam"},
                {"Svante"},

                {"DEVELOPERS"},
                {"Albin"},
                {"Adam"},
                {"Sunny"},
                {"Svante"},

                {"ART DIRECTION"},
                {"Svante"}
        };

        for (String[] entry : credits) {
            if (entry.length == 1 && entry[0].equals(entry[0].toUpperCase())) {
                Text roleText = new Text(entry[0]);
                roleText.setFont(roleFont);
                roleText.setFill(COLOR_ROLE);
                roleText.setStroke(COLOR_OUTLINE);
                roleText.setStrokeWidth(0.5);
                creditsBox.getChildren().add(roleText);
            } else {
                Text nameText = new Text(entry[0]);
                nameText.setFont(nameFont);
                nameText.setFill(COLOR_NAME);
                nameText.setStroke(COLOR_OUTLINE);
                nameText.setStrokeWidth(0.5);
                creditsBox.getChildren().add(nameText);
            }
        }

        Text dividerBottom = new Text("______________________________");
        dividerBottom.setFont(roleFont);
        dividerBottom.setFill(Color.web("#44FF44"));

        Text backItem = createMenuItem("Back", onBack, menuFont);

        VBox content = new VBox(40 * resolutionScale);
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(title, dividerTop, creditsBox, dividerBottom, backItem);

        VBox menuBox = new VBox(0);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.getChildren().add(content);

        StackPane root = new StackPane(starCanvas, menuBox);
        scene = new Scene(root, width, height);

        stage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != scene) {
                starAnimation.stop();
            }
        });
    }

    private Text createMenuItem(String label, Runnable action, Font font) {
        Text item = new Text(label);
        item.setFont(font);
        item.setFill(COLOR_DEFAULT);
        item.setStroke(COLOR_OUTLINE);
        item.setStrokeWidth(1.5);
        item.setCursor(Cursor.HAND);

        boolean[] toggle = {false};
        Timeline hoverAnim = new Timeline(new KeyFrame(Duration.millis(300), e -> {
            toggle[0] = !toggle[0];
            item.setFill(toggle[0] ? COLOR_HOVER_B : COLOR_HOVER_A);
        }));
        hoverAnim.setCycleCount(Animation.INDEFINITE);

        item.setOnMouseEntered(e -> {
            item.setText("> " + label + " <");
            item.setFill(COLOR_HOVER_A);
            toggle[0] = false;
            hoverAnim.playFromStart();
        });

        item.setOnMouseExited(e -> {
            hoverAnim.stop();
            item.setText(label);
            item.setFill(COLOR_DEFAULT);
        });

        item.setOnMouseClicked(e -> action.run());

        return item;
    }

    private Font loadPixelFont(double size) {
        InputStream fontStream = getClass().getResourceAsStream("/util/fonts/PressStart2P.ttf");
        if (fontStream != null) {
            Font font = Font.loadFont(fontStream, size);
            if (font != null) return font;
        }
        return Font.font("Monospaced", size);
    }

    public void show() {
        starAnimation.start();
        stage.setTitle("Prototype Panic - Credits");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
}
