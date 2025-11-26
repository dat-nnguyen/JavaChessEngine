package core.ai;

import entities.Board;
import entities.Piece;
import players.Player;

/**
 * this is what we define what makes a good position
 */
public class StandardBoardEvaluator implements BoardEvaluator {
    private static final int CHECK_BONUS = 50;
    private static final int CHECK_MATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 300;

    private static final int MOBILITY_MULTIPLIER = 2;
    @Override
    public int evaluate(final Board board, int depth){
        //if white has 1000 points and black has 800 points, then board score is +200
        return scorePlayer(board, board.getWhitePlayer(), depth) -
                scorePlayer(board, board.getBlackPlayer(), depth);
    }
    private int scorePlayer(final Board board, final Player player, final int depth){
        return pieceValue(player) +
                mobility(player) +
                check(player) +
                checkmate(player, depth) +
                castled(player);
    }

    // MATERIAL SCORE
    // sum up the value of every piece currently on the board
    private static int pieceValue(final Player player){
        int pieceValueScore = 0;
        for (final Piece piece : player.getActivePieces()) {
            pieceValueScore += piece.getPieceType().getPieceValue();
        }
        return pieceValueScore;
    }
    //MOBILITY
    // count how many legals move a player has
    private static int mobility(final Player player){
        return player.getLegalMoves().size() * MOBILITY_MULTIPLIER;
    }

    // CHECK_BONUS
    // if the opponent is in check, add a bonus for current player
    private static int check(final Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    //CHECKMATE_BONUS
    // if checkmated, get massive points
    // add depth bonus to prefer checkmating sooner rather than later
    private static int checkmate(final Player player, int depth){
        if (!player.getOpponent().isInCheck()) {
           return 0;
        }
        return player.getOpponent().isInCheckMate() ? CHECK_MATE_BONUS + DEPTH_BONUS * depth : 0;

    }

    private static int getDepthBonus(int depth){
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    // CASTLE_BONUS
    // encourage AI to castle early
    private static int castled(final Player player){
        return player.isCastled() ? CASTLE_BONUS : 0;
    }
}
