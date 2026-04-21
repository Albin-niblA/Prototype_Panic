package view;

public enum ButtonType {
    START("Start Game"),
    SETTINGS("Settings"),
    SCOREBOARD("Scoreboard (Inte implementerat)"),
    EXIT("Exit");

    private final String label;

    ButtonType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
