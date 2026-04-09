package controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import view.ButtonType;
import view.GameListener;
import view.GamePanel;
import view.MainMenu;
import view.WeaponSelectDialog;



public class Main extends Application implements GameListener {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        showMainMenu();
    }

    private void showMainMenu() {
        new MainMenu(stage, this).show();
    }

    @Override
    public void onButtonClicked(ButtonType type) {
        switch (type) {
            case START -> {
                WeaponSelectDialog dialog = new WeaponSelectDialog(stage);
                int weaponId = dialog.showAndWait();
                GamePanel gp = new GamePanel(stage, weaponId);
                gp.setOnReturnToMenu(this::showMainMenu);
                gp.show();
            }
            case EXIT  -> Platform.exit();

            default    -> { } // Settings och Statistics
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
