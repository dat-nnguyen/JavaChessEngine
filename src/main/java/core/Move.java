package core;

import entities.Board;
import entities.Piece;
import entities.Board.Builder;

public abstract class Move {

    // 'protected' allows the subclasses (MajorMove/AttackMove) to see these fields directly.
    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    private Move(final Board board,
                 final Piece movedPiece,
                 final int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    // Return a new board with the move applied
    public abstract Board execute();

    // -- GETTERS --
    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public Piece getMovedPiece() {
        return this.movedPiece;
    }

    public boolean isAttack() {
        return false;
    }

    public boolean isCastlingMove() {
        return false;
    }

    public Piece getAttackedPiece() {
        return null;
    }

    // -- Inner Classes --

    public static final class MajorMove extends Move {
        public MajorMove(final Board board,
                         final Piece movedPiece,
                         final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();

            // Copy all active pieces of the current player (EXCEPT the moved piece)
            for (final Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            // Copy all active pieces of the opponent (No pieces captured here)
            for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }

            //  Move the piece to the new location
            builder.setPiece(this.movedPiece.movePiece(this));

            // 4. Switch the turn
            builder.setNextMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());

            return builder.build();
        }
    }

    public static class AttackMove extends Move {
        final Piece attackedPiece;

        public AttackMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinate,
                          final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AttackMove)) {
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();

            // Copy friends (minus moved piece)
            for (final Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            // Copy enemies (MINUS the attacked piece)
            for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                if(!piece.equals(this.attackedPiece)) {
                    builder.setPiece(piece);
                }
            }

            // 3. Move the piece
            builder.setPiece(this.movedPiece.movePiece(this));

            // 4. Switch turn
            builder.setNextMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());

            return builder.build();
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }
    }
}