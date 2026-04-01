package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainMenu {
    private Stage stage;
    private Scene scene;

    public MainMenu(Stage stage, GameListener listener) {
        this.stage = stage;

        Text title = new Text("Prototype Panic");
        title.setFont(new Font(40));

        VBox layout = new VBox(20); // Spacing mellan varje knapp
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(title);

        for (ButtonType type : ButtonType.values()) {
            Button btn = new Button(type.getLabel());
            btn.setPrefWidth(200);
            btn.setPrefHeight(50);
            btn.setOnAction(e -> listener.onButtonClicked(type));
            layout.getChildren().add(btn);
        }

        scene = new Scene(layout, 800, 600);
    }

    public void show() {
        stage.setTitle("Prototype Panic");
        stage.setScene(scene);
        stage.show();
    }
}