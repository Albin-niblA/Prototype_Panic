package model.world;

import controller.InputHandler;
import javafx.scene.input.KeyCode;
import java.util.Random;
import model.managers.UpgradeManager;
import model.upgrades.Upgrades;
import model.entities.Enemy;
import model.entities.Player;
import model.managers.EffectManager;
import model.managers.EnemyHandler;
import model.managers.ProjectileManager;
import model.managers.SoundManager;
import model.managers.WaveManager;
import model.weapon.Arrow;
import model.weapon.Rocket;
import model.weapon.Weapon;
import model.weapon.WeaponType;
import model.managers.CoinManager;

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

    private final CoinManager coinManager;
    private Random rand = new Random();

    private GameState state = GameState.RUNNING;
    private double shootCooldown = 0;
    private boolean shooting = false;

    public GameWorld(WeaponType weaponType) {
        player = new Player(WORLD_WIDTH / 2.0, WORLD_HEIGHT / 2.0);
        effectManager = new EffectManager();
        enemyHandler = new EnemyHandler(effectManager);
        waveManager = new WaveManager();
        upgradeManager = player.getUpgradeManager();
        coinManager = new CoinManager();
        projectileManager = new ProjectileManager(WORLD_WIDTH, WORLD_HEIGHT, upgradeManager);
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
            if (upgradeManager.tryBlink(WORLD_WIDTH, WORLD_HEIGHT)) {
                effectManager.addEffect(oldX, oldY, 1, now);
                effectManager.addEffect(player.getX(), player.getY(), 2, now);
                SoundManager.playTeleport();
            }
        }
        player.update(delta, WORLD_WIDTH, WORLD_HEIGHT);

        // Shooting
        int bounce = upgradeManager.getBounceAmount();
        shootCooldown -= delta;
        if (shooting && shootCooldown <= 0) {
            double worldMouseX = input.getMouseX() + camera.getOffsetX();
            double worldMouseY = input.getMouseY() + camera.getOffsetY();

            int shotAmount = upgradeManager.getShotAmount();
            if (shotAmount == 1) {
                currentWeapon.shoot(projectileManager,
                        player.getX(), player.getY(),
                        worldMouseX, worldMouseY, bounce);
            } else {
                currentWeapon.shootMultiple(projectileManager, player.getX(), player.getY(),
                        worldMouseX, worldMouseY, shotAmount, bounce);
            }
            shootCooldown = currentWeapon.getFireInterval() * player.getFirerateMultiplier();
            if (currentWeapon instanceof Arrow) {
                SoundManager.playArrowShoot();
            } else if (currentWeapon instanceof Rocket) {
                SoundManager.playRocketSound();
            } else {
                SoundManager.playShoot();
            }
        }

        // Update systems
        projectileManager.update(delta);
        effectManager.update(now);
        enemyHandler.update(delta, player.getX(), player.getY(), projectileManager);
        checkGrenadeExplosions();
        checkCollisions();
        checkEnemyProjectileCollisions();
        waveManager.update(delta, enemyHandler, player.getX(), player.getY());
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

    private void checkEnemyProjectileCollisions() {
        if (player.getDamageCooldown() > 0) return;

        int dmg = projectileManager.checkPlayerHit(
                player.getX(), player.getY(), player.getSize() / 2
        );
        if (dmg > 0) {
            player.takeDamage(dmg);
            if (projectileManager.getLastHitEffectID() == 1) {
                player.applyFreeze(1.5);
            }
            if (player.isDead()) {
                state = GameState.GAME_OVER;
                shooting = false;
            }
        }
    }

    private void checkCollisions() {
        // Projectile vs Enemy (skip grenades - they only damage on explosion)
        for (int i = 0; i < projectileManager.getCount(); i++) {
            if (projectileManager.isGrenade(i)) continue;

            double px = projectileManager.getX(i);
            double py = projectileManager.getY(i);
            double pr = projectileManager.getRadius(i);
            boolean isEnemy = projectileManager.getIsEnemy(i);
            int dmg = (int) (projectileManager.getDamage(i) * upgradeManager.getDamageMultiplier());

            if (!isEnemy) {
                Enemy e = enemyHandler.checkHit(px, py, pr, dmg + upgradeManager.getOnHitDamage(), upgradeManager.getSlowMultiplier(),
                        upgradeManager.getPoisonDamage(), upgradeManager.getEFFECT_OVER_TIME_TICK_INTERVAL(),
                        upgradeManager.getElectricDamageMultiplier());
                if (e != null) {
                    projectileManager.deleteProjectile(i--);
                    effectManager.addEffect(px, py, 0, System.nanoTime());
                    if (upgradeManager.getSlowMultiplier() != 1) {
                        if (!e.isHasLoadedPoisonEffect()) {
                            effectManager.addEffect(e.getX(), e.getY(), 5, System.nanoTime());
                            e.setHasLoadedPoisonEffect(true);
                        }
                    }
                    if (e.isDead()) {
                        coinManager.earn(e.getCoinDropAmount());   // ← mynt-drop
                        if (player.addXp((int) (e.getXpDropAmount() * upgradeManager.getFortunateMultiplier()))) {
                            upgrade();
                        }
                    }
                    player.addHealth((int) (dmg * upgradeManager.getLifesteal()));
                }
            }
        }

        // Enemy vs Player
        if (player.getDamageCooldown() <= 0) {
            Enemy hitBy = enemyHandler.checkPlayerHit(
                player.getX(), player.getY(), player.getSize() / 2
            );
            if (hitBy != null) {
                SoundManager.playHit();
                if (rand.nextDouble() > upgradeManager.getAngeltouchChance()) {
                    player.takeDamage(hitBy.getContactDamage());
                    if (player.isDead()) {
                        state = GameState.GAME_OVER;
                        shooting = false;
                    }
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

    public void applyCardUpgrade(Upgrades u) {
        if (u != null) {
            player.getUpgradeManager().levelUpgrade(u);
            state = GameState.RUNNING;
        }
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
    public CoinManager getCoinManager(){
        return coinManager;
    }

    public int getScore() {
        return (waveManager.getCurrentWave() * 1000)
                + coinManager.getBalance()
                + (player.getLevel() * 500);
    }
}
