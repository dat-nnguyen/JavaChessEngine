package Players;

import core.Move;
import entities.Alliance;
import entities.Board;
import entities.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlackPlayer extends Player {

    public BlackPlayer(final Board board,
                       final Collection<Move> whiteStandardLegalMoves,
                       final Collection<Move> blackStandardLegalMoves) {

        // Pass the specific lists to the parent constructor
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.getWhitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                    final Collection<Move> opponentsLegals) {

        final List<Move> kingCastles = new ArrayList<>();

        // KING SIDE CASTLE
        // Black King starts at 4. Target is 6.
        if (this.playerKing.isFirstMove() && !this.isInCheck()) {

            if (!this.board.getSquare(5).isOccupied() && !this.board.getSquare(6).isOccupied()) {

                final Collection<Move> attacksOn5 = Player.calculateAttacksOnSquare(5, opponentsLegals);
                final Collection<Move> attacksOn6 = Player.calculateAttacksOnSquare(6, opponentsLegals);

                if (attacksOn5.isEmpty() && attacksOn6.isEmpty()) {
                    if (this.board.getSquare(7).isOccupied()) {
                        // TODO: Uncomment once Rook is created
                        // final Piece pieceAt7 = this.board.getSquare(7).getPiece();
                        // if (pieceAt7.isFirstMove() && pieceAt7.getPieceType().isRook()) {
                        //      kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, 6, (Rook)pieceAt7, 5, 7));
                        // }
                    }
                }
            }
        }

        // QUEEN SIDE CASTLE
        // Black King starts at 4. Target is 2.
        if (this.playerKing.isFirstMove() && !this.isInCheck()) {

            if (!this.board.getSquare(1).isOccupied() &&
                    !this.board.getSquare(2).isOccupied() &&
                    !this.board.getSquare(3).isOccupied()) {

                final Collection<Move> attacksOn2 = Player.calculateAttacksOnSquare(2, opponentsLegals);
                final Collection<Move> attacksOn3 = Player.calculateAttacksOnSquare(3, opponentsLegals);

                if (attacksOn2.isEmpty() && attacksOn3.isEmpty()) {
                    if (this.board.getSquare(0).isOccupied()) {
                        // TODO: Uncomment once Rook is created
                        // final Piece pieceAt0 = this.board.getSquare(0).getPiece();
                        // if (pieceAt0.isFirstMove() && pieceAt0.getPieceType().isRook()) {
                        //      kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing, 2, (Rook)pieceAt0, 3, 0));
                        // }
                    }
                }
            }
        }

        return kingCastles;
    }
}