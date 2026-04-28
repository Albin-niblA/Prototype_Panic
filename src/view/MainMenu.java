package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainMenu {
    private final Stage stage;
    private final Scene scene;

    public MainMenu(Stage stage, GameListener listener, int width, int height, double resolutionScale) {
        this.stage = stage;

        Text title = new Text("Prototype Panic");
        title.setFont(new Font(40 * resolutionScale));

        VBox layout = new VBox(20 * resolutionScale);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(title);

        for (ButtonType type : ButtonType.values()) {
            Button btn = new Button(type.getLabel());
            btn.setPrefWidth(200 * resolutionScale);
            btn.setPrefHeight(50 * resolutionScale);
            btn.setOnAction(e -> listener.onButtonClicked(type));
            layout.getChildren().add(btn);
        }

        scene = new Scene(layout, width, height);
    }

    public void show() {
        stage.setTitle("Prototype Panic");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
}
