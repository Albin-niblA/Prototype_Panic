package model.wave;

import model.managers.EnemyHandler;
import model.entities.Enemy;
import model.world.GameWorld;
import model.entities.enemies.Slime;
import model.entities.enemies.DemonSlime;

import java.util.Random;

public class WaveManager {
    private int currentWave = 0;
    private double spawnTimer = 0;
    private int enemiesSpawnedThisWave = 0;
    private int enemiesPerWave = 5;
    private double spawnInterval = 3.0;
    private boolean waveActive = false;
    private double betweenWaveTimer = 0;
    private static final double BETWEEN_WAVE_DELAY = 3.0;
    private final Random rand = new Random();

    public void update(double delta, EnemyHandler eh, double playerX, double playerY) {
        if (!waveActive) {
            betweenWaveTimer += delta;
            if (betweenWaveTimer >= BETWEEN_WAVE_DELAY) {
                startNextWave();
            }
            return;
        }

        spawnTimer += delta;
        if (spawnTimer >= spawnInterval && enemiesSpawnedThisWave < enemiesPerWave) {
            spawnEnemy(eh, playerX, playerY);
            spawnTimer = 0;
            enemiesSpawnedThisWave++;
        }

        if (enemiesSpawnedThisWave >= enemiesPerWave && eh.getCount() == 0) {
            waveActive = false;
            betweenWaveTimer = 0;
        }
    }

    private void startNextWave() {
        currentWave++;
        enemiesSpawnedThisWave = 0;
        enemiesPerWave = 5 + (currentWave * 2);
        spawnInterval = Math.max(0.5, 3.0 - currentWave * 0.2);
        waveActive = true;
        spawnTimer = spawnInterval; // spawn first enemy immediately
    }

    private void spawnEnemy(EnemyHandler eh, double playerX, double playerY) {
        double angle = rand.nextDouble() * 2 * Math.PI;
        double dist = 400 + rand.nextDouble() * 300;
        double x = playerX + Math.cos(angle) * dist;
        double y = playerY + Math.sin(angle) * dist;

        x = Math.max(50, Math.min(GameWorld.WORLD_WIDTH - 50, x));
        y = Math.max(50, Math.min(GameWorld.WORLD_HEIGHT - 50, y));

        Enemy e;
        if (currentWave <= 2 || rand.nextDouble() < 0.6) {
            e = new Slime(x, y);
        } else {
            e = new DemonSlime(x, y);
        }
        eh.addEnemy(e);
    }

    public int getCurrentWave() { return currentWave; }
    public boolean isWaveActive() { return waveActive; }

    public void reset() {
        currentWave = 0;
        spawnTimer = 0;
        enemiesSpawnedThisWave = 0;
        waveActive = false;
        betweenWaveTimer = 0;
    }
}
