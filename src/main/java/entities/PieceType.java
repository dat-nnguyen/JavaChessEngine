package entities;

public enum PieceType {
    PAWN("P", 100),
    KNIGHT("N", 300),
    QUEEN("Q", 900),
    ROOK("R", 500){
        @Override
        public boolean isRook() {
            return true;
        }
    },
    BISHOP("B", 300),
    KING("K", 1000){
        @Override
        public boolean isKing() {
            return true;
        }
    };

    private final String pieceName;
    private final int pieceValue;

    PieceType(String pieceName, int pieceValue) {
        this.pieceName = pieceName;
        this.pieceValue = pieceValue;
    }

    public String toString() {
        return this.pieceName;
    }

    public int getPieceValue() {
        return this.pieceValue;
    }

    public boolean isKing() {
        return false;
    }
    public boolean isRook() {return false;}
}
