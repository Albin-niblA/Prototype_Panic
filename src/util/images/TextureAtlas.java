package util.images;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class TextureAtlas {

    private final Image[] playerTextures = new Image[8];
    private final Image[] playerRedTextures = new Image[8];
    private final Image[] playerBlueTextures = new Image[8];
    private final Image[] enemyTextures = new Image[6];
    private final Image[] projectileTextures = new Image[5];
    private final Image[] fxTextures = new Image[3];
    private final Image[] upgradeIcons = new Image[12];
    private Image[] mapSheet = new Image[6];
    private Image[] assetTextures = new Image[1];

    public TextureAtlas() {
        playerTextures[0] = load("/util/images/player/pFront.png");
        playerTextures[1] = load("/util/images/player/pBack.png");
        playerTextures[2] = load("/util/images/player/pLeft.png");
        playerTextures[3] = load("/util/images/player/pRight.png");
        playerTextures[4] = load("/util/images/player/pFL.png");
        playerTextures[5] = load("/util/images/player/pFR.png");
        playerTextures[6] = load("/util/images/player/pBL.png");
        playerTextures[7] = load("/util/images/player/pBR.png");

        for (int i = 0; i < 8; i++) {
            playerRedTextures[i]  = tintImage(playerTextures[i], 1.0, 0.0, 0.0);
            playerBlueTextures[i] = tintImage(playerTextures[i], 0.0, 0.8, 1.0);
        }

        enemyTextures[0] = load("/util/images/enemies/slime.png");
        enemyTextures[1] = load("/util/images/enemies/demonslime.png");
        enemyTextures[2] = load("/util/images/enemies/Bandit.png");
        enemyTextures[3] = load("/util/images/enemies/frostMage.png");
        enemyTextures[4] = load("/util/images/enemies/earthMage.png");
        enemyTextures[5] = load("/util/images/enemies/fireMage.png");

        projectileTextures[0] = load("/util/images/projectiles/bullet1.png");
        projectileTextures[1] = load("/util/images/projectiles/arrow.png");
        projectileTextures[2] = load("/util/images/projectiles/rocket.png");
        projectileTextures[3] = load("/util/images/projectiles/grenade.png");
        projectileTextures[4] = load("/util/images/projectiles/snowflake.png");

        fxTextures[0] = load("/util/images/fx/explosion.png");
        fxTextures[1] = load("/util/images/fx/blinkStart.png");
        fxTextures[2] = load("/util/images/fx/blinkEnd.png");

        upgradeIcons[0]  = load("/util/images/upgradeIcons/nimbleIcon.png");
        upgradeIcons[1]  = load("/util/images/upgradeIcons/healthyIcon.png");
        upgradeIcons[2]  = load("/util/images/upgradeIcons/sharpIcon.png");
        upgradeIcons[3]  = load("/util/images/upgradeIcons/vampireIcon.png");
        upgradeIcons[4]  = load("/util/images/upgradeIcons/blinkIcon.png");
        upgradeIcons[5]  = load("/util/images/upgradeIcons/fortunateIcon.png");
        upgradeIcons[6]  = load("/util/images/upgradeIcons/frostbulletIcon.png");
        upgradeIcons[7]  = load("/util/images/upgradeIcons/poisonbulletIcon.png");
        upgradeIcons[8]  = load("/util/images/upgradeIcons/angeltouchIcon.png");
        upgradeIcons[9]  = load("/util/images/upgradeIcons/multishotIcon.png");
        upgradeIcons[10] = load("/util/images/upgradeIcons/bounceIcon.png");
        upgradeIcons[11] = load("/util/images/upgradeIcons/electricIcon.png");

        assetTextures[0] = load("/util/images/assets/coin.png");

        mapSheet = loadSheet("/util/images/map.png", 32, 32);
    }

    /**
     * Creates a copy of the image where every pixel keeps its original alpha
     * but its RGB is replaced with the given tint colour. Fully transparent
     * pixels stay fully transparent, so the result is always sprite-shaped.
     */
    private Image tintImage(Image src, double r, double g, double b) {
        int w = (int) src.getWidth();
        int h = (int) src.getHeight();
        WritableImage out = new WritableImage(w, h);
        PixelReader reader = src.getPixelReader();
        PixelWriter writer = out.getPixelWriter();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = reader.getColor(x, y);
                writer.setColor(x, y, new Color(r, g, b, c.getOpacity()));
            }
        }
        return out;
    }

    private Image load(String path) {
        return new Image(getClass().getResourceAsStream(path));
    }

    private Image[] loadSheet(String path, int frameWidth, int frameHeight) {
        Image sheet = load(path);
        int cols = (int) (sheet.getWidth() / frameWidth);
        int rows = (int) (sheet.getHeight() / frameHeight);
        Image[] frames = new Image[cols * rows];

        int index = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                frames[index++] = new WritableImage(
                        sheet.getPixelReader(),
                        x * frameWidth, y * frameHeight,
                        frameWidth, frameHeight);
            }
        }
        return frames;
    }

    public Image getPlayerTexture(int dir)     { return playerTextures[dir]; }
    public Image getPlayerRedTexture(int dir)  { return playerRedTextures[dir]; }
    public Image getPlayerBlueTexture(int dir) { return playerBlueTextures[dir]; }
    public Image getEnemyTexture(int id)       { return enemyTextures[id]; }
    public Image getProjectileTexture(int id)  { return projectileTextures[id]; }
    public Image getEffectTexture(int fxID)    { return fxTextures[fxID]; }
    public Image getUpgradeIcon(int iconID)    { return upgradeIcons[iconID]; }
    public Image getAsset(int assetID) { return assetTextures[assetID]; }
    public Image[] getMapSheet()               { return mapSheet; }
}
