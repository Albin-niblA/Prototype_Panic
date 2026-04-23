package util.settings;

public interface SettingsListener {
    void onWasdSelected();
    void onArrowKeysSelected();
    boolean isWasdActive();
}
