package model;

public class EffectManager {
    private static final int MAX_EFFECTS = 1000;

    private int count = 0;

    private final double[] posX = new double[MAX_EFFECTS];
    private final double[] posY = new double[MAX_EFFECTS];
    private final int[] frame = new int[MAX_EFFECTS];
    private final long[] lastUpdate = new long[MAX_EFFECTS];
    private final int[] fxID = new int[MAX_EFFECTS];

    public void update(long now) {
        for (int i = 0; i < count; i++) {
            if (now - lastUpdate[i] > 30_000_000) {
                frame[i]++;
                lastUpdate[i] = now;

                // t.ex. 8 frames per effect
                if (frame[i] >= getMaxFrames(fxID[i])) {
                    delete(i--);
                }
            }
        }
    }

    public void addEffect(double x, double y, int effID, long now) {
        if (count >= MAX_EFFECTS) return;

        posX[count] = x;
        posY[count] = y;
        frame[count] = 0;
        lastUpdate[count] = now;
        fxID[count] = effID;

        count++;
    }

    private void delete(int i) {
        int last = count - 1;

        posX[i] = posX[last];
        posY[i] = posY[last];
        frame[i] = frame[last];
        lastUpdate[i] = lastUpdate[last];
        fxID[i] = fxID[last];

        count--;
    }

    private int getMaxFrames(int fxID) {
        return switch (fxID) {
            case 0 -> 12;
            case 1, 2 -> 7;
            default -> 0;
        };
    }

    // getters
    public int getCount() { return count; }
    public double getX(int i) { return posX[i]; }
    public double getY(int i) { return posY[i]; }
    public int getFrame(int i) { return frame[i]; }
    public int getEffectID(int i) { return fxID[i]; }
    public int getEffectSize(int fxID) {
        return switch (fxID) {
            case 0 -> 32;
            case 1, 2 -> 96;
            default -> 1;
        };
    }
}