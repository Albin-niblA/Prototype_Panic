package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Settings;

public class SettingsMenu {
    private final Stage stage;
    private final Runnable onBack;
    private final int WIDTH;
    private final int HEIGHT;

    public SettingsMenu(Stage stage, Runnable onBack, int width, int height) {
        this.stage = stage;
        this.onBack = onBack;
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    public void show() {
        Text title = new Text("Settings");
        title.setFont(new Font(36));

        //Volume
        Label volumeLabel = new Label("Volume: " + (int)(Settings.getVolume() * 100) + "%");
        Slider volumeSlider = new Slider(0, 1, Settings.getVolume());
        volumeSlider.setMaxWidth(300);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            Settings.setVolume(newVal.doubleValue());
            volumeLabel.setText("Volume: " + (int)(newVal.doubleValue() * 100) + "%");
        });


        //Resolution
        Label resLabel = new Label("Resolution:");
        ToggleGroup resGroup = new ToggleGroup();

        int[][] resolutions = {{800, 600}, {1280, 720}, {1600, 900}, {1920, 1080}};
        VBox resBox = new VBox(5);
        resBox.setAlignment(Pos.CENTER);
        for (int[] res : resolutions) {
            RadioButton rb = new RadioButton(res[0] + " x " + res[1]);
            rb.setToggleGroup(resGroup);
            if (res[0] == Settings.getWidth() && res[1] == Settings.getHeight()) {
                rb.setSelected(true);
            }
            final int w = res[0], h = res[1];
            rb.setOnAction(e -> Settings.setResolution(w, h));
            resBox.getChildren().add(rb);
        }

        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(200);
        backBtn.setPrefHeight(40);
        backBtn.setOnAction(e -> onBack.run());

        VBox layout = new VBox(20, title, volumeLabel, volumeSlider, resLabel, resBox, backBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        stage.setScene(new Scene(layout, WIDTH, HEIGHT));
        stage.show();
    }
}