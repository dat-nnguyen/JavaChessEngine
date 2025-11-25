package entities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Square {

    protected final int squareCoordinate;

    private static final Map<Integer, EmptySquare> EMPTY_SQUARE_CACHE = createAllPossibleEmptySquares();

    private static Map<Integer, EmptySquare> createAllPossibleEmptySquares() {
        final Map<Integer, EmptySquare> emptySquares = new HashMap<>();
        for (int i = 0; i < 64; i++) {
            emptySquares.put(i, new EmptySquare(i));
        }
        return Collections.unmodifiableMap(emptySquares);
    }

    protected Square(final int squareCoordinate) {
        this.squareCoordinate = squareCoordinate;
    }

    /**
     * Factory method for creating a square.
     * Returns an OccupiedSquare if piece is non-null, else an EmptySquare from the cache.
     */
    public static Square createSquare(final int squareCoordinate, final Piece piece) {
        if (piece != null) {
            return new OccupiedSquare(squareCoordinate, piece);
        } else {
            return EMPTY_SQUARE_CACHE.get(squareCoordinate);
        }
    }

    public abstract boolean isOccupied();
    public abstract Piece getPiece();

    public int getSquareCoordinate() {
        return this.squareCoordinate;
    }

    //-- INNER CLASSES --

    public static final class EmptySquare extends Square {

        private EmptySquare(final int squareCoordinate) {
            super(squareCoordinate);
        }

        @Override
        public boolean isOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public String toString() {
            return "-";
        }
    }

    public static final class OccupiedSquare extends Square {

        private final Piece pieceOnSquare;

        private OccupiedSquare(final int squareCoordinate, final Piece pieceOnSquare) {
            super(squareCoordinate);
            this.pieceOnSquare = pieceOnSquare;
        }

        @Override
        public boolean isOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return this.pieceOnSquare;
        }

        @Override
        public String toString() {
            // Uppercase for white, lowercase for black
            return this.pieceOnSquare.getPieceAlliance().isBlack() ?
                    this.pieceOnSquare.toString().toLowerCase() :
                    this.pieceOnSquare.toString();
        }
    }
}
