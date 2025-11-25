package entities;

import core.Move;
import utils.BoardUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class representing a Bishop chess piece.
 * <p>
 * Bishops move diagonally in all four directions until blocked by a piece or the board edge.
 */
public class Bishop extends Piece {

    private static final int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-9, -7, 7, 9};

    /**
     * Constructs a Bishop with default first move status.
     *
     * @param piecePosition the initial position of the bishop
     * @param pieceAlliance the alliance (WHITE/BLACK) of the bishop
     */
    public Bishop(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance, PieceType.BISHOP, true);
    }

    /**
     * Constructs a Bishop with specified first move status.
     *
     * @param piecePosition the initial position of the bishop
     * @param pieceAlliance the alliance (WHITE/BLACK) of the bishop
     * @param isFirstMove   whether the bishop has moved before
     */
    public Bishop(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(piecePosition, pieceAlliance, PieceType.BISHOP, isFirstMove);
    }

    /**
     * Calculates all legal moves for this bishop on the given board.
     * Bishops move diagonally until blocked.
     *
     * @param board the board to evaluate moves on
     * @return a collection of legal moves
     */
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int candidateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES) {
            int candidateDestinationCoordinate = this.getPiecePosition();

            while (BoardUtils.isValidSquareCoordinate(candidateDestinationCoordinate)) {
                if (isFirstColumnExclusion(candidateDestinationCoordinate, candidateOffset) ||
                        isEighthColumnExclusion(candidateDestinationCoordinate, candidateOffset)) {
                    break;
                }

                candidateDestinationCoordinate += candidateOffset;

                if (!BoardUtils.isValidSquareCoordinate(candidateDestinationCoordinate)) {
                    break;
                }

                final Square candidateSquare = board.getSquare(candidateDestinationCoordinate);

                if (!candidateSquare.isOccupied()) {
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = candidateSquare.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                    if (this.getPieceAlliance() != pieceAlliance) {
                        legalMoves.add(new Move.AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                    break; // blocked by a piece
                }
            }
        }
        return legalMoves;
    }

    /**
     * Returns a new Bishop at the destination of the given move.
     *
     * @param move the move to execute
     * @return a new Bishop instance at the destination
     */
    @Override
    public Bishop movePiece(final Move move) {
        return new Bishop(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString() {
        return PieceType.BISHOP.toString();
    }

    // --- HELPER METHODS ---

    /**
     * Handles edge exclusion for the first column (left edge of board).
     */
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == 7);
    }

    /**
     * Handles edge exclusion for the eighth column (right edge of board).
     */
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 || candidateOffset == 9);
    }
}