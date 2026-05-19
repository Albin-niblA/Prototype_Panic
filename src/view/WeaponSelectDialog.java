package view;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.*;
import model.weapon.WeaponType;

import java.io.InputStream;
import java.util.Random;
import java.util.function.*;

public class WeaponSelectDialog {

    private static final Color COLOR_DEFAULT  = Color.web("#66FF44");
    private static final Color COLOR_HOVER_A  = Color.web("#44FFCC");
    private static final Color COLOR_HOVER_B  = Color.WHITE;
    private static final Color COLOR_OUTLINE  = Color.BLACK;
    private static final Color COLOR_TITLE    = Color.GREEN;
    private static final Color COLOR_ACTIVE   = Color.web("#66FF44");
    private static final Color COLOR_INACTIVE = Color.web("#888888");
    private static final Color COLOR_LABEL    = Color.web("#AAFFAA");
    private static final int   STAR_COUNT     = 500;

    private final Stage stage;
    private final Scene scene;
    private final AnimationTimer starAnimation;

    private WeaponType selectedWeapon = WeaponType.BULLET;

    private final Font pixelFont;
    private final Font titleFont;
    private final Font smallFont;

    public WeaponSelectDialog(Stage stage, Consumer<WeaponType> onStart, Runnable onBack,
                              int width, int height, double resolutionScale) {
        this.stage = stage;

        pixelFont = loadPixelFont(20 * resolutionScale);
        titleFont = loadPixelFont(34 * resolutionScale);
        smallFont = loadPixelFont(13 * resolutionScale);

        // ── Starfield ────────────────────────────────────────────────────────
        Canvas starCanvas = new Canvas(width, height);
        GraphicsContext gc = starCanvas.getGraphicsContext2D();

        Random rand = new Random();
        double[] sx            = new double[STAR_COUNT];
        double[] sy            = new double[STAR_COUNT];
        double[] sSize         = new double[STAR_COUNT];
        double[] sSpeed        = new double[STAR_COUNT];
        double[] sPhase        = new double[STAR_COUNT];
        double[] sBaseAlpha    = new double[STAR_COUNT];
        double[] sTwinklePhase = new double[STAR_COUNT];
        double[] sTwinkleSpeed = new double[STAR_COUNT];

        for (int i = 0; i < STAR_COUNT; i++) {
            sx[i]            = rand.nextDouble() * width;
            sy[i]            = rand.nextDouble() * height;
            sSize[i]         = 1 + rand.nextDouble() * 2.5;
            sSpeed[i]        = 0.2 + rand.nextDouble() * 0.8;
            sPhase[i]        = rand.nextDouble() * Math.PI * 2;
            sBaseAlpha[i]    = 0.3 + rand.nextDouble() * 0.7;
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
                double dt      = (lastNano == 0) ? 0 : (now - lastNano) / 1_000_000_000.0;
                lastNano       = now;
                double timeSec = now / 1_000_000_000.0;

                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, width, height);

                for (int i = 0; i < STAR_COUNT; i++) {
                    sx[i] += sSpeed[i] * dt * 30;
                    sy[i] += sSpeed[i] * dt * 20;
                    if (sx[i] > width)  sx[i] -= width;
                    if (sy[i] > height) sy[i] -= height;

                    double twinkle   = 0.5 + 0.5 * Math.sin(timeSec * (1.5 + sSpeed[i]) + sPhase[i]);
                    double alpha     = sBaseAlpha[i] * (0.3 + 0.7 * twinkle);
                    double bluePulse = Math.sin(timeSec * sTwinkleSpeed[i] + sTwinklePhase[i]);
                    if (bluePulse > 0.85) {
                        double bi = (bluePulse - 0.85) / 0.15;
                        gc.setFill(Color.color(0.7+0.3*(1-bi), 0.85+0.15*(1-bi), 1.0,
                                Math.min(1.0, alpha+0.3*bi)));
                    } else {
                        gc.setFill(Color.gray(1.0, alpha));
                    }
                    gc.fillOval(sx[i], sy[i], sSize[i], sSize[i]);
                }

                if (ssActive[0] == 0) {
                    ssCooldown[0] -= dt;
                    if (ssCooldown[0] <= 0) {
                        ssActive[0]  = 1;
                        ssMaxLife[0] = 0.5 + rand.nextDouble() * 0.5;
                        ssLife[0]    = ssMaxLife[0];
                        if (rand.nextBoolean()) {
                            ssX[0] = rand.nextDouble() * width  * 0.7;
                            ssY[0] = rand.nextDouble() * height * 0.3;
                        } else {
                            ssX[0] = rand.nextDouble() * width  * 0.3;
                            ssY[0] = rand.nextDouble() * height * 0.5;
                        }
                        double spd   = 300 + rand.nextDouble() * 200;
                        double angle = Math.toRadians(25 + rand.nextDouble() * 30);
                        ssVx[0] = spd * Math.cos(angle);
                        ssVy[0] = spd * Math.sin(angle);
                    }
                }
                if (ssActive[0] == 1) {
                    ssX[0]   += ssVx[0] * dt;
                    ssY[0]   += ssVy[0] * dt;
                    ssLife[0] -= dt;
                    if (ssLife[0] <= 0) {
                        ssActive[0]   = 0;
                        ssCooldown[0] = 8 + rand.nextDouble() * 7;
                    } else {
                        int    tailSegs = 20;
                        double progress = 1.0 - (ssLife[0] / ssMaxLife[0]);
                        double tailLen  = 80 + 60 * (1.0 - progress);
                        double vLen = Math.sqrt(ssVx[0]*ssVx[0] + ssVy[0]*ssVy[0]);
                        double nx = ssVx[0]/vLen, ny = ssVy[0]/vLen;
                        double lifeFade = ssLife[0] < ssMaxLife[0]*0.3
                                ? ssLife[0]/(ssMaxLife[0]*0.3) : 1.0;
                        gc.save();
                        for (int t = 0; t < tailSegs; t++) {
                            double frac = t / (double) tailSegs;
                            double tx   = ssX[0] - nx * tailLen * frac;
                            double ty   = ssY[0] - ny * tailLen * frac;
                            double a    = (1.0 - frac) * lifeFade;
                            double sz   = 3.0 * (1.0 - frac * 0.7);
                            gc.setGlobalAlpha(Math.max(0, Math.min(1.0, a)));
                            gc.setFill(Color.color(1.0-0.3*frac, 1.0-0.15*frac,
                                    Math.min(1.0, 0.9+0.1*frac)));
                            gc.fillOval(tx-sz/2, ty-sz/2, sz, sz);
                        }
                        gc.restore();
                    }
                }
            }
        };

        // ── Title ────────────────────────────────────────────────────────────
        Text title = new Text("GAME SETUP");
        title.setFont(titleFont);
        title.setFill(COLOR_TITLE);

        Text weaponLabel = new Text("Weapon");
        weaponLabel.setFont(smallFont);
        weaponLabel.setFill(COLOR_LABEL);
        weaponLabel.setStroke(COLOR_OUTLINE);
        weaponLabel.setStrokeWidth(1);

        // ── Stat panel (declared before weapon loop) ─────────────────────────
        double pad      = 18 * resolutionScale;   // inner padding each side
        double panelW   = 360 * resolutionScale;  // total panel width
        double contentW = panelW - pad * 2;       // usable text/row width

        Color statTitleColor = Color.web("#AAFFAA");
        Color statValueColor = Color.WHITE;
        Color statBorder     = Color.web("#44FF88");

        // Square image frame at the top of the panel
        double frameSize  = 90 * resolutionScale;
        double frameInset = 4  * resolutionScale;

        Rectangle imageFrame = new Rectangle(frameSize + frameInset*2, frameSize + frameInset*2);
        imageFrame.setFill(Color.color(0, 0.15, 0, 0.9));
        imageFrame.setStroke(statBorder);
        imageFrame.setStrokeWidth(2);
        imageFrame.setArcWidth(8);
        imageFrame.setArcHeight(8);

        ImageView weaponImage = new ImageView();
        weaponImage.setFitWidth(frameSize);
        weaponImage.setFitHeight(frameSize);
        weaponImage.setPreserveRatio(true);
        weaponImage.setSmooth(true);

        StackPane imagePicture = new StackPane(imageFrame, weaponImage);
        imagePicture.setAlignment(Pos.CENTER);

        // Stat rows live here — width is constrained to contentW
        VBox statContent = new VBox(10 * resolutionScale);
        statContent.setAlignment(Pos.TOP_LEFT);
        statContent.setMaxWidth(contentW);
        statContent.setPrefWidth(contentW);

        // Full panel VBox: image frame on top, stat rows below
        VBox statPanel = new VBox(12 * resolutionScale, imagePicture, statContent);
        statPanel.setAlignment(Pos.TOP_CENTER);
        statPanel.setPadding(new Insets(pad));
        statPanel.setMaxWidth(panelW);
        statPanel.setPrefWidth(panelW);

        // Panel height: frame + padding + name + desc(~3 lines) + divider + 4 rows + bottom pad
        double lineH  = 13 * resolutionScale * 2.4;
        double panelH = (frameSize + frameInset*2)
                + pad * 2
                + (20 * resolutionScale) * 1.8   // name
                + lineH * 3.2                    // description wrapping
                + pad                            // divider gap
                + lineH * 4                      // stat rows
                + pad;

        Canvas panelBg = new Canvas(panelW, panelH);
        GraphicsContext pgc = panelBg.getGraphicsContext2D();

        Runnable drawPanel = () -> {
            pgc.clearRect(0, 0, panelW, panelH);
            pgc.setFill(Color.color(0, 0.08, 0, 0.82));
            pgc.fillRoundRect(0, 0, panelW, panelH, 18, 18);
            pgc.setStroke(statBorder);
            pgc.setLineWidth(2);
            pgc.strokeRoundRect(1, 1, panelW-2, panelH-2, 18, 18);
        };
        drawPanel.run();

        StackPane panelPane = new StackPane(panelBg, statPanel);
        panelPane.setAlignment(Pos.TOP_CENTER);
        panelPane.setMaxWidth(panelW);
        panelPane.setPrefWidth(panelW);
        panelPane.setMaxHeight(panelH);
        panelPane.setPrefHeight(panelH);
        panelPane.setVisible(false);

        // Helper: label + value stat row, both pinned to contentW
        BiFunction<String, String, HBox> makeRow = (label, value) -> {
            Text lbl = new Text(label);
            lbl.setFont(smallFont);
            lbl.setFill(statTitleColor);

            Text val = new Text(value);
            val.setFont(smallFont);
            val.setFill(statValueColor);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox row = new HBox(lbl, spacer, val);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setMaxWidth(contentW);
            row.setPrefWidth(contentW);
            return row;
        };

        // Populate the stat panel on hover
        Consumer<WeaponType> showStats = (wt) -> {
            WeaponStats s = getStats(wt);

            // Load weapon sprite
            InputStream imgStream = getClass().getResourceAsStream(
                    "/util/images/projectiles/" + s.imageName());
            weaponImage.setImage(imgStream != null ? new Image(imgStream) : null);

            Text nameText = new Text(s.name());
            nameText.setFont(pixelFont);
            nameText.setFill(COLOR_HOVER_A);
            nameText.setWrappingWidth(contentW);

            Text descText = new Text(s.description());
            descText.setFont(smallFont);
            descText.setFill(Color.web("#CCFFCC"));
            descText.setWrappingWidth(contentW);

            Line divider = new Line(0, 0, contentW, 0);
            divider.setStroke(statBorder);
            divider.setOpacity(0.5);

            statContent.getChildren().setAll(
                    nameText,
                    descText,
                    divider,
                    makeRow.apply("Damage",    String.valueOf(s.damage())),
                    makeRow.apply("Fire rate", String.format("%.1f /s", s.fireRate())),
                    makeRow.apply("Speed",     String.valueOf((int) s.speed())),
                    makeRow.apply("Special",   s.special())
            );

            drawPanel.run();
            panelPane.setVisible(true);
        };

        // ── Weapon selector row ──────────────────────────────────────────────
        WeaponType[] weapons     = WeaponType.values();
        Text[]       weaponTexts = new Text[weapons.length];

        Runnable updateWeaponColors = () -> {
            for (int i = 0; i < weapons.length; i++) {
                weaponTexts[i].setFill(weapons[i] == selectedWeapon ? COLOR_ACTIVE : COLOR_INACTIVE);
            }
        };

        HBox weaponRow = new HBox(20 * resolutionScale);
        weaponRow.setAlignment(Pos.CENTER);

        for (int i = 0; i < weapons.length; i++) {
            if (i > 0) {
                Text sep = new Text("/");
                sep.setFont(pixelFont);
                sep.setFill(COLOR_LABEL);
                sep.setStroke(COLOR_OUTLINE);
                sep.setStrokeWidth(1.5);
                weaponRow.getChildren().add(sep);
            }

            Text wText = new Text(weapons[i].getDisplayName());
            wText.setFont(pixelFont);
            wText.setStroke(COLOR_OUTLINE);
            wText.setStrokeWidth(1.5);
            wText.setCursor(Cursor.HAND);
            weaponTexts[i] = wText;

            final WeaponType wt = weapons[i];
            wText.setOnMouseClicked(e -> {
                selectedWeapon = wt;
                updateWeaponColors.run();
            });
            wText.setOnMouseEntered(e -> {
                wText.setFill(COLOR_HOVER_A);
                showStats.accept(wt);
            });
            wText.setOnMouseExited(e -> {
                updateWeaponColors.run();
                panelPane.setVisible(false);
            });

            weaponRow.getChildren().add(wText);
        }
        updateWeaponColors.run();

        // ── Final layout ─────────────────────────────────────────────────────
        Text startItem = createMenuItem("Start", () -> onStart.accept(selectedWeapon));
        Text backItem  = createMenuItem("Back",  onBack);

        VBox menuBox = new VBox(40 * resolutionScale,
                title,
                weaponLabel, weaponRow,
                startItem, backItem
        );
        menuBox.setAlignment(Pos.CENTER);

        HBox contentRow = new HBox(60 * resolutionScale, menuBox, panelPane);
        contentRow.setAlignment(Pos.CENTER);
        contentRow.setFillHeight(false);

        StackPane root = new StackPane(starCanvas, contentRow);
        scene = new Scene(root, width, height);

        stage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != scene) starAnimation.stop();
        });
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Text createMenuItem(String label, Runnable action) {
        Text item = new Text(label);
        item.setFont(pixelFont);
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

    private record WeaponStats(String name, String description, String imageName,
                               int damage, double fireRate, double speed, String special) {}

    private static WeaponStats getStats(WeaponType wt) {
        return switch (wt) {
            case BULLET  -> new WeaponStats("BULLET",
                    "Rapid-fire pistol. Low damage, very fast.",
                    "bullet1.png", 10, 3.3, 1000, "No special");
            case ARROW   -> new WeaponStats("ARROW",
                    "Balanced crossbow. High damage, moderate speed.",
                    "arrow.png",   25, 2.0,  800, "No special");
            case ROCKET  -> new WeaponStats("ROCKET",
                    "Heavy launcher. Huge damage, slow fire rate.",
                    "rocket.png",  50, 1.0,  750, "Explosive on hit");
            case GRENADE -> new WeaponStats("GRENADE",
                    "Timed explosive. Massive AoE damage.",
                    "grenade.png", 75, 0.3,  700, "2s fuse · 150px blast");
        };
    }

    public void show() {
        starAnimation.start();
        stage.setTitle("Prototype Panic - Game Setup");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
}