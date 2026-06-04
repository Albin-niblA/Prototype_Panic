package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import model.entities.Enemy;
import model.entities.Player;
import model.managers.EffectManager;
import model.managers.ProjectileManager;
import model.managers.UpgradeManager;
import model.world.Camera;
import model.world.GameWorld;
import util.images.TextureAtlas;
import model.entities.Entity;

public class GameRenderer {

    private static final int EFFECT_FRAME_SIZE = 32;

    private final int viewportWidth;
    private final int viewportHeight;
    private final double resolutionScale;
    private final int gridSize;
    private final Camera camera;
    private final TextureAtlas textures;
    private final HUD hud;
    private final OverlayHandler overlay;

    public GameRenderer(int viewportWidth, int viewportHeight, double resolutionScale,
                        Camera camera, UpgradeManager upgradeManager) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.resolutionScale = resolutionScale;
        this.gridSize = (int) (60 * resolutionScale);
        this.camera = camera;
        this.textures = new TextureAtlas();
        this.hud = new HUD(viewportWidth, viewportHeight, resolutionScale, textures);
        this.overlay = new OverlayHandler(viewportWidth, viewportHeight, resolutionScale, upgradeManager, textures);
    }

    public void render(GraphicsContext gc, GameWorld world, double delta) {
        double ox = camera.getOffsetX();
        double oy = camera.getOffsetY();

        renderBackground(gc, ox, oy);
        renderProjectiles(gc, world.getProjectileManager(), ox, oy);
        renderPlayer(gc, world.getPlayer(), ox, oy);
        renderEnemies(gc, world, ox, oy);
        renderEffects(gc, world.getEffectManager(), ox, oy);
        hud.draw(gc, world, world.getUpgradeManager().getBLINK_COOLDOWN_DURATION(), world.getUpgradeManager().getBlinkCooldown(), delta);
        overlay.draw(gc, world.getState(), world.getShopManager(), world.getCoinManager());
    }

    private void renderBackground(GraphicsContext gc, double ox, double oy) {
        gc.setFill(Color.web("#a1a6ac"));
        gc.fillRect(0, 0, viewportWidth, viewportHeight);

        double startX = -(ox % gridSize);
        double startY = -(oy % gridSize);

        int col = (int) (ox / gridSize);
        int row = (int) (oy / gridSize);

        Image[] tiles = textures.getMapSheet();

        for (double y = startY; y < viewportHeight; y += gridSize) {
            for (double x = startX; x < viewportWidth; x += gridSize) {
                int gridCol = col + (int) ((x - startX) / gridSize);
                int gridRow = row + (int) ((y - startY) / gridSize);

                // Hash multiplies by 2 prime numbers resulting in a pseudo-random pattern instead of a diagonal pattern of the tiles
                int hash = (gridCol * 1619 + gridRow * 31337) ^ (gridCol * gridRow);

                if (Math.abs(hash) % 7 == 0) {
                    int tileIndex = Math.abs(hash) % tiles.length;
                    gc.drawImage(tiles[tileIndex], x, y, gridSize, gridSize);
                }
            }
        }
    }

    private void renderPlayer(GraphicsContext gc, Player p, double ox, double oy) {
        if (p.getUpgradeManager().isBlinking()) return;

        int dir = p.getMoveDir();
        double size = p.getSize();
        double drawX = p.getX() - size / 2 - ox;
        double drawY = p.getY() - size / 2 - oy;

        // Draw the normal sprite
        gc.drawImage(textures.getPlayerTexture(dir), drawX, drawY, size, size);

        // Red flash on damage: overlay a pre-baked solid-red version of the sprite
        // at fading alpha. Because tintImage() preserves per-pixel alpha, only the
        // opaque parts of the character light up — never the transparent background.
        double cooldown = p.getDamageCooldown();
        if (cooldown > 0) {
            double t = cooldown / Entity.DAMAGE_COOLDOWN_DURATION;
            gc.save();
            gc.setGlobalAlpha(t * 0.7);
            gc.drawImage(textures.getPlayerRedTexture(dir), drawX, drawY, size, size);
            gc.restore();
        }

        // Blue tint when frozen: same approach with a pre-baked blue sprite
        if (p.isFrozen()) {
            gc.save();
            gc.setGlobalAlpha(0.5);
            gc.drawImage(textures.getPlayerBlueTexture(dir), drawX, drawY, size, size);
            gc.restore();
        }
    }

    private void renderEnemies(GraphicsContext gc, GameWorld world, double ox, double oy) {
        for (Enemy e : world.getEnemyHandler().getEnemies()) {
            if (!camera.isVisible(e.getX(), e.getY(), e.getSize())) continue;
            Image tex = textures.getEnemyTexture(e.getTextureID());
            double size = e.getSize();
            gc.drawImage(tex, Math.round(e.getX() - size / 2 - ox), Math.round(e.getY() - size / 2 - oy), size, size);
        }
    }

    private void renderProjectiles(GraphicsContext gc, ProjectileManager pm, double ox, double oy) {
        for (int i = 0; i < pm.getCount(); i++) {
            double px = pm.getX(i) - ox;
            double py = pm.getY(i) - oy;
            if (isOffscreen(px, py)) continue;

            double r = pm.getRadius(i);
            Image tex = textures.getProjectileTexture(pm.getTextureID(i));

            if (pm.isGrenade(i)) {
                drawSprite(gc, tex, px, py, r, 0);
                drawGrenadeFuseIndicator(gc, pm, i, px, py);
            } else {
                double angle = Math.toDegrees(Math.atan2(pm.getVelY(i), pm.getVelX(i)));
                drawSprite(gc, tex, px, py, r, angle);
            }
        }
    }

    private void drawGrenadeFuseIndicator(GraphicsContext gc, ProjectileManager pm,
                                          int i, double px, double py) {
        double fuseRemaining = pm.getFuseTimer(i);
        if (fuseRemaining >= 1.0) return;

        double alpha = 0.15 + 0.25 * (1.0 - fuseRemaining);
        gc.setStroke(Color.rgb(255, 60, 30, alpha));
        gc.setLineWidth(2);
        double explosionR = 150;
        gc.strokeOval(px - explosionR, py - explosionR, explosionR * 2, explosionR * 2);
    }

    /*
    private void renderEnemyProjectiles(GraphicsContext gc, EnemyProjectileManager epm,
                                         double ox, double oy) {
        for (int i = 0; i < epm.getCount(); i++) {
            double px = epm.getX(i) - ox;
            double py = epm.getY(i) - oy;
            if (isOffscreen(px, py)) continue;

            double r = epm.getRadius(i);
            Image tex = textures.getProjectileTexture(epm.getTextureID(i));
            double angle = Math.toDegrees(Math.atan2(epm.getVelY(i), epm.getVelX(i)));
            drawSprite(gc, tex, px, py, r, angle);
        }
    }*/

    private void renderEffects(GraphicsContext gc, EffectManager em, double ox, double oy) {
        for (int i = 0; i < em.getCount(); i++) {
            double x = em.getX(i) - ox;
            double y = em.getY(i) - oy;
            int frame = em.getFrame(i);
            int fxID = em.getEffectID(i);
            Image sheet = textures.getEffectTexture(fxID);

            int cols = (int) (sheet.getWidth() / EFFECT_FRAME_SIZE);
            int sx = (frame % cols) * EFFECT_FRAME_SIZE;
            int sy = (frame / cols) * EFFECT_FRAME_SIZE;
            int size = (int) (em.getEffectSize(fxID));

            gc.drawImage(sheet,
                    sx, sy, EFFECT_FRAME_SIZE, EFFECT_FRAME_SIZE,
                    x - size / 2.0, y - size / 2.0, size, size);
        }
    }

    private void drawSprite(GraphicsContext gc, Image tex, double px, double py,
                            double r, double angleDeg) {
        gc.save();
        gc.translate(px, py);
        if (angleDeg != 0) gc.rotate(angleDeg);
        gc.drawImage(tex, -r, -r, r * 2, r * 2);
        gc.restore();
    }

    private boolean isOffscreen(double px, double py) {
        return px < -200 || px > viewportWidth + 200
                || py < -200 || py > viewportHeight + 200;
    }

    public HUD getHud() {
        return hud;
    }

    public OverlayHandler getOverlay() {
        return overlay;
    }
}
