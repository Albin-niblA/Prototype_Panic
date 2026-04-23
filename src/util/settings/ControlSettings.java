package util.settings;

public class ControlSettings {
    private static ControlScheme scheme = ControlScheme.WASD;

    public static ControlScheme getScheme() { return scheme; }
    public static void setScheme(ControlScheme s) { scheme = s; }
}
