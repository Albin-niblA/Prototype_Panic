package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WeaponSelectDialog {

    private final Stage dialog;
    private int selectedWeapon = 0;

    public WeaponSelectDialog(Stage owner) {
        dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);

        Label title = new Label("Choose your weapon:");

        ToggleGroup group = new ToggleGroup();
        String[] names = {"Bullet", "Arrow", "Rocket"};
        RadioButton[] buttons = new RadioButton[names.length];

        for (int i = 0; i < names.length; i++) {
            buttons[i] = new RadioButton(names[i]);
            buttons[i].setToggleGroup(group);
            final int id = i;
            buttons[i].setOnAction(e -> selectedWeapon = id);
        }
        buttons[0].setSelected(true);

        Button confirm = new Button("Start");
        confirm.setOnAction(e -> dialog.close());

        VBox layout = new VBox(10, title, buttons[0], buttons[1], buttons[2], confirm);
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setPadding(new Insets(20));

        dialog.setScene(new Scene(layout, 200, 180));
        dialog.setTitle("Weapon Select");
    }

    public int showAndWait() {
        dialog.showAndWait();
        return selectedWeapon;
    }
}
