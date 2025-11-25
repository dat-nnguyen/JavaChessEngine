package entities;

import core.Move;
import utils.BoardUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents the King piece on the chessboard.
 * Calculates legal moves including captures but not castling.
 */
public class King extends Piece {

    // Candidate moves for the King (all 8 surrounding squares)
    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(final int piecePosition, final Alliance pieceAlliance){
        super(piecePosition, pieceAlliance, PieceType.KING, true);
    }

    public King(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove){
        super(piecePosition, pieceAlliance, PieceType.KING, isFirstMove);
    }

    /**
     * Calculate all legal moves for the King.
     * @param board The current board state.
     * @return A collection of all legal moves (normal and attacks).
     */
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES) {

            final int candidateDestinationCoordinate = this.getPiecePosition() + currentCandidateOffset;

            // Ensure candidate square is within the board
            if (BoardUtils.isValidSquareCoordinate(candidateDestinationCoordinate)) {

                // Handle board edge exclusions to avoid wrapping
                if (isFirstColumnExclusion(this.getPiecePosition(), currentCandidateOffset) ||
                        isEighthColumnExclusion(this.getPiecePosition(), currentCandidateOffset)) {
                    continue;
                }

                final Square candidateSquare = board.getSquare(candidateDestinationCoordinate);

                if (!candidateSquare.isOccupied()) {
                    // Empty square: normal move
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                } else {
                    // Occupied square: can capture if opponent piece
                    final Piece pieceAtDestination = candidateSquare.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                    if (this.getPieceAlliance() != pieceAlliance) {
                        legalMoves.add(new Move.AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                }
            }
        }
        return legalMoves;
    }

    /**
     * Returns a new King piece moved to the destination square.
     * @param move The move to execute.
     * @return The new King piece after moving.
     */
    @Override
    public King movePiece(Move move) {
        return new King(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false);
    }

    @Override
    public String toString() {
        return PieceType.KING.toString();
    }

    // --- HELPER METHODS FOR EDGE CASES ---

    /**
     * Edge case exclusion for left edge of the board.
     */
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        // Block Left (-1), Up-Left (-9), Down-Left (7)
        return BoardUtils.FIRST_COLUMN[currentPosition] &&
                (candidateOffset == -9 || candidateOffset == -1 || candidateOffset == 7);
    }

    /**
     * Edge case exclusion for right edge of the board.
     */
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        // Block Right (1), Up-Right (-7), Down-Right (9)
        return BoardUtils.EIGHTH_COLUMN[currentPosition] &&
                (candidateOffset == -7 || candidateOffset == 1 || candidateOffset == 9);
    }
}
