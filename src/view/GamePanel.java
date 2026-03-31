package view;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GamePanel {
    private Stage stage;
    private Scene scene;

    public GamePanel(Stage stage) {
        this.stage = stage;

        Pane root = new Pane();
        scene = new Scene(root, 800, 600);
        scene.setFill(Color.GRAY);

        Text text = new Text("Test");
        text.setFont(new Font(20));
        text.setFill(Color.BLACK);
        text.relocate(10, 90);

        Circle circle = new Circle(60, 40, 30, Color.BLUE);

        scene.setOnMouseClicked(event -> {
            System.out.println("x: " + event.getX() + ", y: " + event.getY());
        });

        root.getChildren().addAll(circle, text);    }

    public void show() {
        stage.setScene(scene);
        stage.show();
    }
}