package model.world;

/**
 * Representerar spelets möjliga tillstånd.
 *
 * RUNNING  – spelet körs normalt.
 * PAUSED   – spelet är pausat (ESC).
 * GAME_OVER – spelaren dog.
 * UPGRADE  – pausat för uppgradering (ej implementerat ännu).
 */
public enum GameState {
    RUNNING,
    PAUSED,
    GAME_OVER,
    UPGRADE
}
