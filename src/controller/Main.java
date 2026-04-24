package controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import model.weapon.WeaponType;
import util.settings.ControlScheme;
import util.settings.ControlSettings;
import view.ButtonType;
import view.GameListener;
import view.MainMenu;
import util.settings.SettingsListener;
import view.SettingsMenu;
import view.WeaponSelectDialog;

public class Main extends Application implements GameListener, SettingsListener {

    private Stage stage;
    private static final int WIDTH = 1600;
    private static final int HEIGHT = 900;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        showMainMenu();
    }

    private void showMainMenu() {
        new MainMenu(stage, this, WIDTH, HEIGHT).show();
    }

    @Override
    public void onButtonClicked(ButtonType type) {
        switch (type) {
            case START -> {
                WeaponSelectDialog dialog = new WeaponSelectDialog(stage);
                WeaponType weapon = dialog.showAndWait();
                GameController gc = new GameController(stage, WIDTH, HEIGHT, weapon);
                gc.setOnReturnToMenu(this::showMainMenu);
                gc.start();
            }
            case SETTINGS -> new SettingsMenu(stage, this, this::showMainMenu, WIDTH, HEIGHT).show();
            case EXIT -> Platform.exit();
            default -> { }
        }
    }

    @Override
    public void onWasdSelected() {
        ControlSettings.setScheme(ControlScheme.WASD);
    }

    @Override
    public void onArrowKeysSelected() {
        ControlSettings.setScheme(ControlScheme.ARROW_KEYS);
    }

    @Override
    public boolean isWasdActive() {
        return ControlSettings.getScheme() == ControlScheme.WASD;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
