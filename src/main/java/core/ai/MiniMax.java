package core.ai;

import core.Move;
import entities.Board;
import entities.MoveTransition;

public class MiniMax implements MoveStrategy {
    private final BoardEvaluator evaluator;
    private final int searchDepth;

    public MiniMax(final int searchDepth) {
        this.evaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }

    /**
     * The Entry Point.
     * This method is called by the GameEngine. It looks at the current board
     * and kicks off the recursive chain to find the best move.
     */

    @Override
    public Move execute(Board board) {
        final long startTIme = System.currentTimeMillis();

        Move bestMove = null;

        // initial values are set to the worst possible so any real move will be better
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;

        System.out.println(board.getCurrentPlayer() + "Thinking with depth " + this.searchDepth);
        // now loop through available move
        int numMoves = board.getCurrentPlayer().getLegalMoves().size();
        int moveCounter = 1;

        for (final Move move : board.getCurrentPlayer().getLegalMoves()) {

            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            //if the move is legal, analyze it
            if (moveTransition.getMoveStatus().isDone()) {
                // RECURSION
                // if white, white move then white ask: 'what is the minimum score black can force?'
                // vice versa with black
                if (board.getCurrentPlayer().getAlliance().isWhite()) {
                    currentValue = min(moveTransition.getTransitionBoard(), this.searchDepth - 1);
                } else {
                    currentValue = max(moveTransition.getTransitionBoard(), this.searchDepth - 1);
                }
                // compare the results
                if (board.getCurrentPlayer().getAlliance().isWhite() && currentValue > highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.getCurrentPlayer().getAlliance().isBlack() && currentValue < lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
            moveCounter++;
        }
        long executionTime = System.currentTimeMillis() - startTIme;
        System.out.println("AI selected move: " + bestMove + " Score: " +
                (board.getCurrentPlayer().getAlliance().isWhite() ? highestSeenValue : lowestSeenValue) +
                " Time: " + executionTime + "ms");

        return bestMove;
    }
    // --- RECURSIVE HELPERS: MIN for BLACK ----
    public int min(final Board board, final int depth) {
        // base: if we hit depth 0 or game over, stop looking and score the board
        if (depth == 0 || isEndGameScenario(board)) {
            return this.evaluator.evaluate(board,depth);
        }

        int lowestSeenValue = Integer.MAX_VALUE;
        for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                // after black moves, calls max (white's turn)
                int currentValue = max(moveTransition.getTransitionBoard(), depth - 1);

                if (currentValue < lowestSeenValue) {
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;
    }

    // --- RECURSIVE HELPERS: MAX for BLACK ----
    public int max(final Board board, final int depth) {
        // base case
        if (depth == 0 || isEndGameScenario(board)) {
            return this.evaluator.evaluate(board,depth);
        }

        int highestSeenValue = Integer.MIN_VALUE;
        for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                int currentValue = min(moveTransition.getTransitionBoard(), depth - 1);

                if (currentValue > highestSeenValue) {
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }

    private boolean isEndGameScenario(Board board) {
        return board.getCurrentPlayer().isInCheckMate() ||
                board.getCurrentPlayer().isInStaleMate();
    }
}
