package entities;

import core.Move;

import java.util.Collection;

public abstract class Piece {
    private int piecePosition;
    private PieceType pieceType;
    private Alliance pieceAlliance;
    private boolean isFirstMove;
    private final int cachedHashCode;

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
     * For AI: This is the most important method.
     * Instead of checking if ONE move is valid, it returns ALL valid moves.
     */
    public abstract Collection<Move> calculateLegalMoves(final Board board);

    /**
     * For Movement: Returns a NEW Piece with updated coordinates.
     * We do not change 'this.coordinate'. We create a new version of the piece.
     * This is crucial for "Undo" functionality.
     */
    public abstract Piece movePiece(Move move);

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

    // Essential for comparing pieces in collections

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
        return piecePosition == otherPiece.getPiecePosition() &&
                pieceType == otherPiece.getPieceType() &&
                pieceAlliance == otherPiece.getPieceAlliance() &&
                isFirstMove == otherPiece.isFirstMove();
    }
}
