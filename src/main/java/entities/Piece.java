package entities;

import core.Move;
import java.util.Collection;

/**
 * Abstract base class for all chess pieces.
 *
 * <p>Key responsibilities:
 * <ul>
 *     <li>Store fundamental attributes: position, alliance, type, and first-move status.</li>
 *     <li>Provide methods for calculating legal moves.</li>
 *     <li>Provide an immutable move method that returns a new piece with updated position.</li>
 * </ul>
 * </p>
 */
public abstract class Piece {

    private final int piecePosition;
    private final PieceType pieceType;
    private final Alliance pieceAlliance;
    private final boolean isFirstMove;
    private final int cachedHashCode;

    /**
     * Constructs a chess piece.
     *
     * @param piecePosition the 0-63 index of the square
     * @param pieceAlliance the alliance (WHITE or BLACK)
     * @param pieceType the type of the piece (PAWN, KNIGHT, etc.)
     * @param isFirstMove whether the piece has moved yet
     */
    public Piece(final int piecePosition,
                 final Alliance pieceAlliance,
                 final PieceType pieceType,
                 final boolean isFirstMove) {
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.pieceType = pieceType;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }

    /**
     * Calculates all legal moves for this piece on the given board.
     *
     * <p>Important for AI: instead of checking one move at a time,
     * returns all moves that are currently legal.</p>
     *
     * @param board the board to evaluate
     * @return a collection of legal moves
     */
    public abstract Collection<Move> calculateLegalMoves(final Board board);

    /**
     * Returns a new piece with updated position after a move.
     *
     * <p>Immutability ensures the current piece remains unchanged,
     * which is essential for undo/redo and AI simulations.</p>
     *
     * @param move the move to apply
     * @return a new Piece instance at the new position
     */
    public abstract Piece movePiece(Move move);

    // --- GETTERS ---
    public int getPiecePosition() {
        return this.piecePosition;
    }

    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }

    public PieceType getPieceType() {
        return this.pieceType;
    }

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    // --- HASHCODE / EQUALITY ---
    /**
     * Computes a hash code for the piece based on type, alliance, position, and first-move status.
     * Caches the result to make hash-based collections efficient.
     */
    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Piece)) return false;
        final Piece otherPiece = (Piece) other;
        return piecePosition == otherPiece.getPiecePosition()
                && pieceType == otherPiece.getPieceType()
                && pieceAlliance == otherPiece.getPieceAlliance()
                && isFirstMove == otherPiece.isFirstMove();
    }
}
