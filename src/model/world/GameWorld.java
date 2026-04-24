package model.world;

import controller.InputHandler;
import javafx.scene.input.KeyCode;
import model.upgrades.UpgradeManager;
import model.upgrades.Upgrades;
import model.entities.Enemy;
import model.entities.Player;
import model.managers.EffectManager;
import model.managers.EnemyHandler;
import model.managers.ProjectileManager;
import model.managers.SoundManager;
import model.wave.WaveManager;
import model.weapon.Upgrade;
import model.weapon.Weapon;
import model.weapon.WeaponType;

public class GameWorld {
    public static final int WORLD_WIDTH = 3200;
    public static final int WORLD_HEIGHT = 1800;

    private final Player player;
    private final EnemyHandler enemyHandler;
    private final ProjectileManager projectileManager;
    private final EffectManager effectManager;
    private final WaveManager waveManager;
    private final Weapon currentWeapon;
    private final UpgradeManager upgradeManager;

    private GameState state = GameState.RUNNING;
    private double shootCooldown = 0;
    private boolean shooting = false;

    public GameWorld(WeaponType weaponType) {
        player = new Player(WORLD_WIDTH / 2.0, WORLD_HEIGHT / 2.0);
        enemyHandler = new EnemyHandler();
        projectileManager = new ProjectileManager(WORLD_WIDTH, WORLD_HEIGHT);
        effectManager = new EffectManager();
        waveManager = new WaveManager();
        upgradeManager = new UpgradeManager();
        currentWeapon = Weapon.fromType(weaponType);
    }

    public void update(double delta, InputHandler input, Camera camera, long now) {
        if (state != GameState.RUNNING) return;

        // Player movement
        player.setMoving(
            input.isMovingUp(),
            input.isMovingDown(),
            input.isMovingLeft(),
            input.isMovingRight()
        );

        // Blink
        if (input.wasPressed(KeyCode.SPACE)) {
            double oldX = player.getX();
            double oldY = player.getY();
            if (player.tryBlink(WORLD_WIDTH, WORLD_HEIGHT)) {
                effectManager.addEffect(oldX, oldY, 1, now);
                effectManager.addEffect(player.getX(), player.getY(), 2, now);
            }
        }
        player.update(delta, WORLD_WIDTH, WORLD_HEIGHT);

        // test for upgrades on f keys
        if (input.wasPressed(KeyCode.F1)) {
            player.levelUpgrade(Upgrades.Multishot);
        }
        if (input.wasPressed(KeyCode.F2)) {
            player.levelUpgrade(Upgrades.Nimble);
            player.setMovementSpeed(player.getMovementSpeed() + 10);
        }
        if (input.wasPressed(KeyCode.F3)) {
            player.levelUpgrade(Upgrades.Blink);
            player.setBLINK_DISTANCE(player.getBLINK_DISTANCE() + 20);
        }

        // Shooting
        shootCooldown -= delta;
        if (shooting && shootCooldown <= 0) {
            double worldMouseX = input.getMouseX() + camera.getOffsetX();
            double worldMouseY = input.getMouseY() + camera.getOffsetY();

            int multiLevel = player.getUpgradeLevel(Upgrades.Multishot);
            if (multiLevel == 0) {
                currentWeapon.shoot(projectileManager,
                        player.getX(), player.getY(),
                        worldMouseX, worldMouseY);
            } else {
                currentWeapon.shootMultiple(projectileManager, player.getX(), player.getY(),
                        worldMouseX, worldMouseY, multiLevel + 1);
            }
            shootCooldown = currentWeapon.getFireInterval() * (Math.pow(0.9, player.getUpgradeLevel(Upgrades.Nimble)));
            SoundManager.playShoot();
        }

        // Update systems
        projectileManager.update(delta);
        effectManager.update(now);
        enemyHandler.update(delta, player.getX(), player.getY());
        checkGrenadeExplosions();
        checkCollisions();
        waveManager.update(delta, enemyHandler, player.getX(), player.getY());

        // TEMP: Upgrades activates with keybind
        if(input.wasPressed(KeyCode.U) && currentWeapon instanceof Upgrade) {
            ((Upgrade) currentWeapon).STAGE_ONE_UPGRADE_ONE();
        }
    }

    private void checkGrenadeExplosions() {
        for (int i = 0; i < projectileManager.getCount(); i++) {
            if (projectileManager.isGrenade(i) && projectileManager.getFuseTimer(i) <= 0) {
                double px = projectileManager.getX(i);
                double py = projectileManager.getY(i);
                int dmg = projectileManager.getDamage(i);
                enemyHandler.applyAoeDamage(px, py, projectileManager.getExplosionRadius(i), dmg);
                projectileManager.deleteProjectile(i--);
            }
        }
    }

    private void checkCollisions() {
        // Projectile vs Enemy (skip grenades — they only damage on explosion)
        for (int i = 0; i < projectileManager.getCount(); i++) {
            if (projectileManager.isGrenade(i)) continue;

            double px = projectileManager.getX(i);
            double py = projectileManager.getY(i);
            double pr = projectileManager.getRadius(i);
            int dmg = projectileManager.getDamage(i);

            if (enemyHandler.checkHit(px, py, pr, dmg)) {
                projectileManager.deleteProjectile(i--);
                effectManager.addEffect(px, py, 0, System.nanoTime());
            }
        }

        // Enemy vs Player
        if (player.getDamageCooldown() <= 0) {
            Enemy hitBy = enemyHandler.checkPlayerHit(
                player.getX(), player.getY(), player.getSize() / 2
            );
            if (hitBy != null) {
                player.takeDamage(hitBy.getContactDamage());
                if (player.isDead()) {
                    state = GameState.GAME_OVER;
                    shooting = false;
                }
            }
        }
    }

    public void toggleShooting() {
        if (state == GameState.RUNNING) {
            shooting = !shooting;
        }
    }

    public void pause() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
            shooting = false;
            player.setMoving(false, false, false, false);
        }
    }

    public void resume() {
        if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
        }
    }

    public void reset() {
        player.reset(WORLD_WIDTH / 2.0, WORLD_HEIGHT / 2.0);
        projectileManager.clear();
        enemyHandler.clear();
        waveManager.reset();
        shootCooldown = 0;
        shooting = false;
        state = GameState.RUNNING;
    }

    public void upgrade() {
        state = GameState.UPGRADE;
        shooting = false;
        player.setMoving(false, false, false, false);
    }

    public GameState getState() { return state; }
    public Player getPlayer() { return player; }
    public EnemyHandler getEnemyHandler() { return enemyHandler; }
    public ProjectileManager getProjectileManager() { return projectileManager; }
    public WaveManager getWaveManager() { return waveManager; }
    public Weapon getCurrentWeapon() { return currentWeapon; }
    public EffectManager getEffectManager() { return effectManager; }
    public UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }
}
