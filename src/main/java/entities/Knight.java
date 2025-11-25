package entities;

import core.Move;
import core.Move.AttackMove;
import core.Move.MajorMove;
import utils.BoardUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents the Knight piece on the chessboard.
 * Calculates all legal moves including attacks, while handling edge cases to prevent wrapping around the board.
 */
public class Knight extends Piece {

    // All 8 possible L-shaped moves a Knight can make
    private final static int[] CANDIDATE_MOVES_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(final int piecePosition, final Alliance pieceAlliance){
        super(piecePosition, pieceAlliance, PieceType.KNIGHT, true);
    }

    public Knight(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove){
        super(piecePosition, pieceAlliance, PieceType.KNIGHT, isFirstMove);
    }

    // --- EDGE EXCLUSION HELPERS ---
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        // Prevent Knight from wrapping from column 1 to the left
        return BoardUtils.FIRST_COLUMN[currentPosition] &&
                (candidateOffset == -17 || candidateOffset == -10 || candidateOffset == 6 || candidateOffset == 15);
    }

    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset) {
        // Prevent Knight from wrapping from column 2 to the left
        return BoardUtils.SECOND_COLUMN[currentPosition] &&
                (candidateOffset == -10 || candidateOffset == 6);
    }

    private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset) {
        // Prevent Knight from wrapping from column 7 to the right
        return BoardUtils.SEVENTH_COLUMN[currentPosition] &&
                (candidateOffset == -6 || candidateOffset == 10);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        // Prevent Knight from wrapping from column 8 to the right
        return BoardUtils.EIGHTH_COLUMN[currentPosition] &&
                (candidateOffset == -15 || candidateOffset == -6 || candidateOffset == 10 || candidateOffset == 17);
    }

    /**
     * Calculate all legal moves for the Knight on the given board.
     * @param board Current board state.
     * @return Collection of all legal moves, including attacks.
     */
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (int currentCandidateOffset : CANDIDATE_MOVES_COORDINATES) {

            final int candidateDestinationCoordinate = this.getPiecePosition() + currentCandidateOffset;

            // Skip invalid coordinates (off-board)
            if (!BoardUtils.isValidSquareCoordinate(candidateDestinationCoordinate)) {
                continue;
            }

            // Skip moves that would "teleport" the Knight across the board edges
            if (isFirstColumnExclusion(this.getPiecePosition(), currentCandidateOffset) ||
                    isSecondColumnExclusion(this.getPiecePosition(), currentCandidateOffset) ||
                    isSeventhColumnExclusion(this.getPiecePosition(), currentCandidateOffset) ||
                    isEighthColumnExclusion(this.getPiecePosition(), currentCandidateOffset)) {
                continue;
            }

            final Square candidateSquare = board.getSquare(candidateDestinationCoordinate);

            if (!candidateSquare.isOccupied()) {
                // Empty square: normal move
                legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
            } else {
                // Occupied square: attack if opponent piece
                final Piece pieceAtDestination = candidateSquare.getPiece();
                final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                if (this.getPieceAlliance() != pieceAlliance) {
                    legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                }
            }
        }
        return legalMoves;
    }

    /**
     * Returns a new Knight piece moved to the destination square.
     * @param move The move to execute.
     * @return New Knight instance at destination.
     */
    @Override
    public Knight movePiece(final Move move){
        return new Knight(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }
}
