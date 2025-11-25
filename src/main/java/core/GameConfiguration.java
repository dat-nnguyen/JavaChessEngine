package core;

import entities.Alliance;

/**
 * Represents the configuration settings for a chess game session.
 * <p>
 * This class defines the selected game mode (PvP or PvAI), AI difficulty
 * level if applicable, the time control settings, and the player's chosen
 * color. It acts as an immutable configuration container that is passed
 * to the game engine at initialization.
 */
public class GameConfiguration {

    /**
     * Supported game modes for gameplay.
     */
    public enum GameMode {
        HUMAN_VS_HUMAN,
        HUMAN_VS_AI
    }

    /**
     * Difficulty levels for the AI engine. Higher difficulty typically
     * corresponds to deeper search or more advanced heuristics.
     */
    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    // --- CONFIGURATION FIELDS ---

    private final GameMode gameMode;
    private final Difficulty aiDifficulty;
    private final int timeControlMinutes;
    private final Alliance playerColor;

    /**
     * Creates a new game configuration defining all gameplay parameters.
     *
     * @param gameMode          the selected game mode (human vs human or vs AI)
     * @param aiDifficulty      the AI difficulty level; ignored if not in AI mode
     * @param timeControlMinutes number of minutes allocated per player
     * @param playerColor       the player's chosen alliance (WHITE or BLACK)
     */
    public GameConfiguration(final GameMode gameMode,
                             final Difficulty aiDifficulty,
                             final int timeControlMinutes,
                             final Alliance playerColor) {
        this.gameMode = gameMode;
        this.aiDifficulty = aiDifficulty;
        this.timeControlMinutes = timeControlMinutes;
        this.playerColor = playerColor;
    }

    // --- GETTERS ---

    /**
     * Returns the selected game mode.
     *
     * @return the game mode
     */
    public GameMode getGameMode() {
        return this.gameMode;
    }

    /**
     * Returns the AI difficulty setting.
     * <p>
     * Note: This value is only relevant when the game mode is HUMAN_VS_AI.
     *
     * @return the AI difficulty
     */
    public Difficulty getAiDifficulty() {
        return this.aiDifficulty;
    }

    /**
     * Returns the configured time control value.
     *
     * @return minutes allocated per player
     */
    public int getTimeControlMinutes() {
        return this.timeControlMinutes;
    }

    /**
     * Returns the player's chosen alliance.
     *
     * @return WHITE or BLACK
     */
    public Alliance getPlayerColor() {
        return this.playerColor;
    }
}