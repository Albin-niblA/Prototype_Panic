package util.images;

import javafx.scene.image.Image;

public class TextureAtlas {
    private final Image[] playerTextures = new Image[8];
    private final Image[] enemyTextures = new Image[2];
    private final Image[] projectileTextures = new Image[4];
    private final Image[] fxTextures = new Image[3];
    private final Image[] upgradeIcons = new Image[3];

    public TextureAtlas() {
        playerTextures[0] = load("/util/images/player/pFront.png");
        playerTextures[1] = load("/util/images/player/pBack.png");
        playerTextures[2] = load("/util/images/player/pLeft.png");
        playerTextures[3] = load("/util/images/player/pRight.png");
        playerTextures[4] = load("/util/images/player/pFL.png");
        playerTextures[5] = load("/util/images/player/pFR.png");
        playerTextures[6] = load("/util/images/player/pBL.png");
        playerTextures[7] = load("/util/images/player/pBR.png");


        enemyTextures[0] = load("/util/images/enemies/slime.png");
        enemyTextures[1] = load("/util/images/enemies/demonslime.png");

        projectileTextures[0] = load("/util/images/projectiles/bullet1.png");
        projectileTextures[1] = load("/util/images/projectiles/arrow.png");
        projectileTextures[2] = load("/util/images/projectiles/rocket.png");
        projectileTextures[3] = load("/util/images/projectiles/grenade.png");

        fxTextures[0] = load("/util/images/fx/explosion.png");
        fxTextures[1] = load("/util/images/fx/blinkStart.png");
        fxTextures[2] = load("/util/images/fx/blinkEnd.png");

        upgradeIcons[0] = load("/util/images/upgradeIcons/multishotIcon.png");
        upgradeIcons[1] = load("/util/images/upgradeIcons/nimbleIcon.png");
        upgradeIcons[2] = load("/util/images/upgradeIcons/blinkIcon.png");
    }

    private Image load(String path) {
        return new Image(getClass().getResourceAsStream(path));
    }

    // laddar spritesheets
    private Image[] loadSheet(String path, int frameWidth, int frameHeight) {
        Image sheet = load(path);

        int cols = (int) (sheet.getWidth() / frameWidth);
        int rows = (int) (sheet.getHeight() / frameHeight);
        Image[] frames = new Image[cols * rows];

        int index = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                frames[index++] = new javafx.scene.image.WritableImage(
                        sheet.getPixelReader(),
                        x * frameWidth,
                        y * frameHeight,
                        frameWidth,
                        frameHeight
                );
            }
        }

        return frames;
    }

    public Image getPlayerTexture(int dir) { return playerTextures[dir]; }
    public Image getEnemyTexture(int id) { return enemyTextures[id]; }
    public Image getProjectileTexture(int id) { return projectileTextures[id]; }
    public Image getEffectTexture(int fxID) {
        return fxTextures[fxID];
    }
    public Image getUpgradeIcon(int iconID) { return upgradeIcons[iconID]; }
}
