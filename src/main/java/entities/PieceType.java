package entities;

/**
 * Enum representing all chess piece types.
 * Each piece has a string identifier and a standard value for AI evaluation.
 */
public enum PieceType {

    PAWN("P", 100),
    KNIGHT("N", 300),
    BISHOP("B", 300),
    ROOK("R", 500) {
        @Override
        public boolean isRook() {
            return true;
        }
    },
    QUEEN("Q", 900),
    KING("K", 1000) {
        @Override
        public boolean isKing() {
            return true;
        }
    };

    private final String pieceName;
    private final int pieceValue;

    /**
     * Constructor for piece type.
     *
     * @param pieceName  the single-letter representation of the piece (e.g., "P" for pawn)
     * @param pieceValue the numeric value of the piece for evaluation purposes
     */
    PieceType(String pieceName, int pieceValue) {
        this.pieceName = pieceName;
        this.pieceValue = pieceValue;
    }

    /**
     * Returns the string identifier of the piece.
     *
     * @return single-letter piece name
     */
    @Override
    public String toString() {
        return this.pieceName;
    }

    /**
     * Returns the value of the piece for evaluation in AI or scoring.
     *
     * @return the integer value of the piece
     */
    public int getPieceValue() {
        return this.pieceValue;
    }

    /**
     * Returns true if this piece type is a king.
     *
     * @return true if king, false otherwise
     */
    public boolean isKing() {
        return false;
    }

    /**
     * Returns true if this piece type is a rook.
     *
     * @return true if rook, false otherwise
     */
    public boolean isRook() {
        return false;
    }
}
