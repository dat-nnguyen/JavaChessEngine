package entities;

import core.Move;
import utils.BoardUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a Queen chess piece.
 * A queen combines the movement capabilities of a rook and bishop:
 * it can move any number of squares along a rank, file, or diagonal.
 */
public class Queen extends Piece {

    // Offsets for all 8 directions a queen can move
    private static final int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    /**
     * Constructs a new Queen.
     *
     * @param piecePosition the position of the queen on the board (0-63)
     * @param pieceAlliance the alliance/color of the queen
     */
    public Queen(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance, PieceType.QUEEN, true);
    }

    /**
     * Constructs a Queen with information about whether it has moved before.
     *
     * @param piecePosition the position of the queen on the board (0-63)
     * @param pieceAlliance the alliance/color of the queen
     * @param isFirstMove   whether this queen has moved before
     */
    public Queen(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(piecePosition, pieceAlliance, PieceType.QUEEN, isFirstMove);
    }

    /**
     * Calculates all legal moves for this queen from its current position.
     *
     * @param board the board on which to calculate moves
     * @return a collection of legal moves
     */
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final int candidateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES) {

            int candidateDestinationCoordinate = this.getPiecePosition();

            // keep moving in the current direction until an obstruction or board edge is reached
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
                    if (this.getPieceAlliance() != pieceAtDestination.getPieceAlliance()) {
                        legalMoves.add(new Move.AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                    // stop further movement in this direction after hitting a piece
                    break;
                }
            }
        }

        return legalMoves;
    }

    /**
     * Creates a new queen representing this piece after a move.
     *
     * @param move the move to execute
     * @return a new Queen at the destination coordinate
     */
    @Override
    public Queen movePiece(final Move move) {
        return new Queen(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString() {
        return PieceType.QUEEN.toString();
    }

    // --- HELPER METHODS ---

    /**
     * Handles left-edge exclusions to prevent wraparound on the board.
     */
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] &&
                (candidateOffset == -1 || candidateOffset == -9 || candidateOffset == 7);
    }

    /**
     * Handles right-edge exclusions to prevent wraparound on the board.
     */
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] &&
                (candidateOffset == 1 || candidateOffset == -7 || candidateOffset == 9);
    }
}
