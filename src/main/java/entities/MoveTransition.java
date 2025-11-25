package entities;

import core.Move;

/**
 * Represents the result of executing a move on a chess board.
 * Encapsulates the new board state, the move that was attempted, and the status of that move.
 */
public class MoveTransition {

    /** The board state after executing the move */
    private final Board transitionBoard;

    /** The move that was attempted */
    private final Move move;

    /** The result status of the move (DONE, ILLEGAL_MOVE, LEAVES_PLAYER_IN_CHECK) */
    private final MoveStatus moveStatus;

    /**
     * Constructs a MoveTransition object.
     *
     * @param transitionBoard the board resulting from the move
     * @param move the move that was attempted
     * @param moveStatus the result of the move execution
     */
    public MoveTransition(final Board transitionBoard,
                          final Move move,
                          final MoveStatus moveStatus) {
        this.transitionBoard = transitionBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    /**
     * Returns the status of the move.
     *
     * @return the move status
     */
    public MoveStatus getMoveStatus() {
        return this.moveStatus;
    }

    /**
     * Returns the board after the move has been executed.
     *
     * @return the board state after the move
     */
    public Board getTransitionBoard() {
        return this.transitionBoard;
    }
}
