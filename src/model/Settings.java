package model;

public class Settings {
    private static double volume = 1.0;
    private static int width = 1600;
    private static int height = 900;

    public static double getVolume() { return volume; }
    public static void setVolume(double v) { volume = v; }

    public static int getWidth() { return width; }
    public static int getHeight() { return height; }
    public static void setResolution(int w, int h) { width = w; height = h; }
}