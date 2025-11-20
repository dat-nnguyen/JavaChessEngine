package entities;

public enum PieceType {
    PAWN("P", 100),
    KNIGHT("N", 300),
    QUEEN("Q", 900),
    ROOK("R", 500),
    BISHOP("B", 300),
    KING("K", 1000);

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

}
