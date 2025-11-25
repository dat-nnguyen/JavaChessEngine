package entities;

/**
 * Represents the result of attempting to execute a move.
 * Used to indicate whether a move was successfully executed, illegal, or leaves the player in check.
 */
public enum MoveStatus {

    /** Move was successfully executed */
    DONE {
        @Override
        public boolean isDone() {
            return true;
        }
    },

    /** Move is illegal and cannot be performed */
    ILLEGAL_MOVE {
        @Override
        public boolean isDone() {
            return false;
        }
    },

    /** Move leaves the current player's king in check, hence not allowed */
    LEAVES_PLAYER_IN_CHECK {
        @Override
        public boolean isDone() {
            return false;
        }
    };

    /**
     * Returns whether this move status represents a successfully executed move.
     * @return true if move is done, false otherwise
     */
    public abstract boolean isDone();
}
