package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import util.settings.SettingsListener;

public class SettingsMenu {
    private static final String ACTIVE_STYLE =
            "-fx-border-width: 4; -fx-border-color: #4a9eff; -fx-background-color: transparent; -fx-cursor: hand;";
    private static final String INACTIVE_STYLE =
            "-fx-border-width: 4; -fx-border-color: transparent; -fx-background-color: transparent; -fx-cursor: hand;";

    private final Stage stage;
    private final Scene scene;
    private final VBox wasdBox;
    private final VBox arrowBox;
    private final SettingsListener listener;
    private double resolutionScale;

    public SettingsMenu(Stage stage, SettingsListener listener, Runnable onBack, int width, int height, double resolutionScale) {
        this.stage = stage;
        this.listener = listener;
        this.resolutionScale = resolutionScale;

        Text title = new Text("Settings");
        title.setFont(new Font(40 * resolutionScale));

        Text subtitle = new Text("Select control scheme");
        subtitle.setFont(new Font(18 * resolutionScale));

        wasdBox = createImageBox("WASD", "/util/images/assets/WASD_Keys.png");
        arrowBox = createImageBox("Arrow keys", "/util/images/assets/Arrow_Keys.png");

        wasdBox.setOnMouseClicked(e -> {
            listener.onWasdSelected();
            updateHighlight();
        });
        arrowBox.setOnMouseClicked(e -> {
            listener.onArrowKeysSelected();
            updateHighlight();
        });
        updateHighlight();

        HBox controlsRow = new HBox(60 * resolutionScale, wasdBox, arrowBox);
        controlsRow.setAlignment(Pos.CENTER);

        Button backButton = new Button("Back to main menu");
        backButton.setPrefWidth(250 * resolutionScale);
        backButton.setPrefHeight(50 * resolutionScale);
        backButton.setOnAction(e -> onBack.run());

        VBox layout = new VBox(25 * resolutionScale);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(title, subtitle, controlsRow, backButton);

        scene = new Scene(layout, width, height);
    }

    private VBox createImageBox(String label, String imagePath) {
        Image img = new Image(getClass().getResourceAsStream(imagePath), 200*resolutionScale, 200*resolutionScale, true, false);
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(200*resolutionScale);
        imageView.setFitHeight(200*resolutionScale);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(false);

        Text labelText = new Text(label);
        labelText.setFont(new Font(20 * resolutionScale));

        VBox box = new VBox(12 * resolutionScale, imageView, labelText);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(16 * resolutionScale));
        box.setStyle(INACTIVE_STYLE);
        return box;
    }

    private void updateHighlight() {
        boolean wasdActive = listener.isWasdActive();
        wasdBox.setStyle(wasdActive ? ACTIVE_STYLE : INACTIVE_STYLE);
        arrowBox.setStyle(wasdActive ? INACTIVE_STYLE : ACTIVE_STYLE);
    }

    public void show() {
        stage.setTitle("Prototype Panic - Settings");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
}
