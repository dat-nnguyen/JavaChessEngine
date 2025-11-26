package core;

import entities.*;
import entities.Board.Builder;

/**
 * Abstract class representing a chess move.
 * <p>
 * Handles move execution, equality, hashCode, and special moves like castling, pawn promotion, and en passant.
 */

public abstract class Move {

    // Factory Class for creating moves easily in Tests/AI
    public static class MoveFactory {

        // This handles the "Null Object Pattern" if a move is invalid
        private static final Move NULL_MOVE = new NullMove();

        public static Move createMove(final Board board, final int currentCoordinate, final int destinationCoordinate) {
            for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
                if (move.getMovedPiece().getPiecePosition() == currentCoordinate &&
                        move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }

    // Helper Null Move Class
    public static class NullMove extends Move {
        public NullMove() {
            super(null, null, -1); // Invalid state
        }
        @Override
        public Board execute() {
            throw new RuntimeException("Cannot execute the null move!");
        }
        @Override
        public int getDestinationCoordinate() {
            return -1;
        }
    }

    // Class Field
    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    /**
     * Constructor for all moves.
     *
     * @param board               current board
     * @param movedPiece          piece being moved
     * @param destinationCoordinate target square coordinate
     */
    private Move(final Board board, final Piece movedPiece, final int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    /**
     * Executes the move and returns the resulting board.
     *
     * @return new board after move execution
     */
    public abstract Board execute();

    // --- GETTERS ---
    public int getDestinationCoordinate() { return this.destinationCoordinate; }
    public Piece getMovedPiece() { return this.movedPiece; }
    public boolean isAttack() { return false; }
    public boolean isCastlingMove() { return false; }
    public Piece getAttackedPiece() { return null; }

    // --- IDENTITY ---
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.destinationCoordinate;
        result = prime * result + this.movedPiece.hashCode();
        result = prime * result + this.movedPiece.getPiecePosition();
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (!(other instanceof Move)) return false;
        final Move otherMove = (Move) other;
        return this.destinationCoordinate == otherMove.getDestinationCoordinate() &&
                this.movedPiece.equals(otherMove.getMovedPiece());
    }

    // ---------------------------------------------------------------------------
    // --- MAJOR MOVE (NORMAL MOVE) ---
    // ---------------------------------------------------------------------------

    public static class MajorMove extends Move {
        public MajorMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            // Copy current player's pieces except the moved piece
            for (final Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece)) builder.setPiece(piece);
            }
            // Copy opponent's pieces
            for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setNextMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    // ---------------------------------------------------------------------------
    // --- ATTACK MOVE ---
    // ---------------------------------------------------------------------------

    public static class AttackMove extends Move {
        final Piece attackedPiece;

        public AttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) return true;
            if (!(other instanceof AttackMove)) return false;
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            // Copy current player's pieces except the moved piece
            for (final Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece)) builder.setPiece(piece);
            }
            // Copy opponent's pieces except the attacked piece
            for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                if (!piece.equals(this.attackedPiece)) builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setNextMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isAttack() { return true; }
        @Override
        public Piece getAttackedPiece() { return this.attackedPiece; }
    }

    // ---------------------------------------------------------------------------
    // --- CASTLING MOVES ---
    // ---------------------------------------------------------------------------

    static abstract class CastleMove extends Move {
        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        public CastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                          final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook() { return this.castleRook; }
        @Override
        public boolean isCastlingMove() { return true; }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) builder.setPiece(piece);
            }
            for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceAlliance(), false));
            builder.setNextMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) return true;
            if (!(other instanceof CastleMove)) return false;
            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }
    }

    public static final class KingSideCastleMove extends CastleMove {
        public KingSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                                  final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }
        @Override public String toString() { return "O-O"; }
    }

    public static final class QueenSideCastleMove extends CastleMove {
        public QueenSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                                   final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }
        @Override public String toString() { return "O-O-O"; }
    }

    // ---------------------------------------------------------------------------
    // --- PAWN MOVES ---
    // ---------------------------------------------------------------------------

    public static class PawnMove extends MajorMove {
        public PawnMove(final Board board, final Piece movedPiece, final int destinationCoordinate) { super(board, movedPiece, destinationCoordinate); }
        @Override public boolean equals(final Object other) { return this == other || other instanceof PawnMove && super.equals(other); }
        @Override public String toString() { return this.destinationCoordinate + ""; }
    }

    public static class PawnAttackMove extends AttackMove {
        public PawnAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }
        @Override public boolean equals(final Object other) { return this == other || other instanceof PawnAttackMove && super.equals(other); }
        @Override public String toString() { return "x" + this.destinationCoordinate; }
    }

    public static final class PawnEnPassantAttackMove extends PawnAttackMove {
        public PawnEnPassantAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override public boolean equals(final Object other) { return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other); }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.getCurrentPlayer().getActivePieces()) if (!this.movedPiece.equals(piece)) builder.setPiece(piece);
            for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) if (!piece.equals(this.getAttackedPiece())) builder.setPiece(piece);
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setNextMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    public static final class PawnJump extends Move {
        public PawnJump(final Board board, final Piece movedPiece, final int destinationCoordinate) { super(board, movedPiece, destinationCoordinate); }
        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.getCurrentPlayer().getActivePieces()) if (!this.movedPiece.equals(piece)) builder.setPiece(piece);
            for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) builder.setPiece(piece);
            final Piece movedPawn = this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn((Pawn) movedPawn);
            builder.setNextMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
        @Override public String toString() { return "Jump"; }
    }

    // ---------------------------------------------------------------------------
    // --- PAWN PROMOTION ---
    // ---------------------------------------------------------------------------

    /**
     * Wraps a pawn move and executes promotion to Queen.
     */
    public static class PawnPromotion extends Move {
        final Move decoratedMove;
        final Pawn promotedPawn;

        public PawnPromotion(final Move decoratedMove) {
            super(decoratedMove.board, decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
        }

        @Override public int hashCode() { return decoratedMove.hashCode() + 31; }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnPromotion && (this.decoratedMove.equals(((PawnPromotion) other).decoratedMove));
        }

        @Override
        public Board execute() {
            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Builder builder = new Builder();

            for (final Piece piece : pawnMovedBoard.getCurrentPlayer().getActivePieces())
                if (!this.promotedPawn.equals(piece) && piece.getPiecePosition() != this.destinationCoordinate) builder.setPiece(piece);

            for (final Piece piece : pawnMovedBoard.getCurrentPlayer().getOpponent().getActivePieces())
                if (!this.promotedPawn.equals(piece) && piece.getPiecePosition() != this.destinationCoordinate) builder.setPiece(piece);

            builder.setPiece(new Queen(this.destinationCoordinate, this.promotedPawn.getPieceAlliance(), false));
            builder.setNextMoveMaker(pawnMovedBoard.getCurrentPlayer().getAlliance());
            return builder.build();
        }

        @Override public boolean isAttack() { return this.decoratedMove.isAttack(); }
        @Override public Piece getAttackedPiece() { return this.decoratedMove.getAttackedPiece(); }
        @Override public String toString() { return ""; }
    }
}