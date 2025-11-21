package entities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Square {
    protected final int squareCoordinate;

    private static final Map<Integer, EmptySquare> EMPTY_SQUARE_CACHE = createAllPossibleEmptySquares();

    private static Map<Integer, EmptySquare> createAllPossibleEmptySquares() {
        final Map<Integer, EmptySquare> emptyTileMap = new HashMap<>();
        for (int i = 0; i < 64; i++) {
            emptyTileMap.put(i, new EmptySquare(i));
        }
        return Collections.unmodifiableMap(emptyTileMap);
    }

    protected Square(final int squareCoordinate) {
        this.squareCoordinate = squareCoordinate;
    }

    // this is what Board calls
    public static Square createSquare(final int squareCoordinate, final Piece piece) {
        if (piece != null) {
            return new OccupiedSquare(squareCoordinate, piece);
        } else {
            return EMPTY_SQUARE_CACHE.get(squareCoordinate);
        }
    }

    public abstract boolean isOccupied();
    public abstract Piece getPiece();

    public static class EmptySquare extends Square {
        private EmptySquare(final int squareCoordinate) {
            super(squareCoordinate);
        }

        @Override
        public String toString() {
            return "-"; // Representation for console print
        }
        @Override
        public boolean isOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }
    }
    public static final class OccupiedSquare extends Square {

        private final Piece pieceOnSquare;

        private OccupiedSquare(int tileCoordinate, Piece pieceOnTile) {
            super(tileCoordinate);
            this.pieceOnSquare = pieceOnTile;
        }

        @Override
        public String toString() {
            // If piece is BLACK, print lowercase. If WHITE, print uppercase.
            return this.pieceOnSquare.getPieceAlliance().isBlack() ?
                    this.pieceOnSquare.toString().toLowerCase() :
                    this.pieceOnSquare.toString();
        }

        @Override
        public boolean isOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return this.pieceOnSquare;
        }
    }
}
