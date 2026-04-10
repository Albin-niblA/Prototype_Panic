package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import model.*;
import util.TextureAtlas;

public class GameRenderer {
    private final int viewportWidth;
    private final int viewportHeight;
    private final Camera camera;
    private final TextureAtlas textures;
    private final HUD hud;
    private final PauseOverlay pauseOverlay;

    private static final int GRID_SIZE = 60;

    public GameRenderer(int viewportWidth, int viewportHeight, Camera camera) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.camera = camera;
        this.textures = new TextureAtlas();
        this.hud = new HUD(viewportWidth, viewportHeight);
        this.pauseOverlay = new PauseOverlay(viewportWidth, viewportHeight);
    }

    public void render(GraphicsContext gc, GameWorld world) {
        double ox = camera.getOffsetX();
        double oy = camera.getOffsetY();

        // Background
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, viewportWidth, viewportHeight);

        // Grid (camera-aware)
        gc.setStroke(Color.GREY);
        gc.setLineWidth(1);
        double startX = -(ox % GRID_SIZE);
        double startY = -(oy % GRID_SIZE);
        for (double x = startX; x < viewportWidth; x += GRID_SIZE) {
            gc.strokeLine(x, 0, x, viewportHeight);
        }
        for (double y = startY; y < viewportHeight; y += GRID_SIZE) {
            gc.strokeLine(0, y, viewportWidth, y);
        }

        // Projectiles
        renderProjectiles(gc, world.getProjectileManager(), ox, oy);

        // Player
        Player p = world.getPlayer();
        Image pTex = textures.getPlayerTexture(p.getMoveDir());
        double ps = p.getSize();
        gc.drawImage(pTex, p.getX() - ps / 2 - ox, p.getY() - ps / 2 - oy, ps, ps);

        // Enemies
        for (Enemy e : world.getEnemyHandler().getEnemies()) {
            if (!camera.isVisible(e.getX(), e.getY(), e.getSize())) continue;
            Image eTex = textures.getEnemyTexture(e.getTextureID());
            double es = e.getSize();
            gc.drawImage(eTex, e.getX() - es / 2 - ox, e.getY() - es / 2 - oy, es, es);
        }

        // Effects
        renderEffects(gc, world.getEffectManager(), ox, oy);

        // HUD (screen-space)
        hud.draw(gc, world);

        // Overlay (pause/game over)
        pauseOverlay.draw(gc, world.getState());
    }

    private void renderProjectiles(GraphicsContext gc, ProjectileManager pm,
                                    double ox, double oy) {
        for (int i = 0; i < pm.getCount(); i++) {
            double px = pm.getX(i) - ox;
            double py = pm.getY(i) - oy;

            if (px < -200 || px > viewportWidth + 200 ||
                py < -200 || py > viewportHeight + 200) continue;

            double r = pm.getRadius(i);
            int texID = pm.getTextureID(i);
            Image tex = textures.getProjectileTexture(texID);

            if (pm.isGrenade(i)) {
                // Draw grenade without velocity-based rotation
                gc.save();
                gc.translate(px, py);
                gc.drawImage(tex, -r, -r, r * 2, r * 2);
                gc.restore();

                // Draw pulsing explosion radius indicator when fuse < 1s
                double fuseRemaining = pm.getFuseTimer(i);
                if (fuseRemaining < 1.0) {
                    double alpha = 0.15 + 0.25 * (1.0 - fuseRemaining);
                    gc.setStroke(Color.rgb(255, 60, 30, alpha));
                    gc.setLineWidth(2);
                    double explosionR = 150;
                    gc.strokeOval(px - explosionR, py - explosionR,
                        explosionR * 2, explosionR * 2);
                }
            } else {
                // Normal projectile: rotate by velocity direction
                double angle = Math.toDegrees(Math.atan2(pm.getVelY(i), pm.getVelX(i)));
                gc.save();
                gc.translate(px, py);
                gc.rotate(angle);
                gc.drawImage(tex, -r, -r, r * 2, r * 2);
                gc.restore();
            }
        }
    }

    private void renderEffects(GraphicsContext gc, EffectManager em,
                               double ox, double oy) {

        for (int i = 0; i < em.getCount(); i++) {
            double x = em.getX(i) - ox;
            double y = em.getY(i) - oy;

            int frame = em.getFrame(i);
            int fxID = em.getEffectID(i);

            Image sheet = textures.getEffectTexture(fxID);

            int frameWidth = 32;
            int frameHeight = 32;

            int cols = (int)(sheet.getWidth() / frameWidth);

            int sx = (frame % cols) * frameWidth;
            int sy = (frame / cols) * frameHeight;

            gc.drawImage(sheet,
                    sx, sy, frameWidth, frameHeight,
                    x - frameWidth / 2.0, y - frameHeight / 2.0,
                    frameWidth, frameHeight);
        }
    }
}
