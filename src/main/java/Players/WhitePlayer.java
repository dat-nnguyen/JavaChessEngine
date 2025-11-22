package Players;

import core.Move;
import entities.Alliance;
import entities.Board;
import entities.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhitePlayer extends Player {

    public WhitePlayer(final Board board,
                       final Collection<Move> whiteStandardLegalMoves,
                       final Collection<Move> blackStandardLegalMoves) {

        // Pass the specific lists to the parent constructor
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.getBlackPlayer();
    }

    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                    final Collection<Move> opponentLegals) {
        final List<Move> kingCastles = new ArrayList<>();
        // KING SIDE CASTLING
        // WHITE KING START AT 60, TARGET TO MOVE TO 62
        if (this.playerKing.isFirstMove() && !this.isInCheck()) {
            // check if squares 61 and 62 are empty
            if (!this.board.getSquare(61).isOccupied()
            && !this.board.getSquare(62).isOccupied()) {
                // check squares 61 and 62 are not attacked
                final Collection<Move> attackOn61 = Player.calculateAttacksOnSquare(61, opponentLegals);
                final Collection<Move> attackOn62 = Player.calculateAttacksOnSquare(62, opponentLegals);

                if (attackOn61.isEmpty() && attackOn62.isEmpty()) {
                    // need to check for the Rook at 63
                    if (this.board.getSquare(63).isOccupied()) {
                        //TODO: uncomment after the Rook complete
                        // final Piece pieceAt63 = this.board.getSquare(63).getPiece();
                        // if (pieceAt63.isFirstMove() && pieceAt63.getPieceType().isRook()) {
                        //      kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, 62, (Rook)pieceAt63, 61, 63));
                        // }
                    }
                }
            }
        }
        // QUEEN SIDE CASTLING
        // WHITE KING START AT 60, TARGET TO MOVE TO 58
        if (this.playerKing.isFirstMove() && !this.isInCheck()) {
            // check if squares 57, 58, 59 are empty
            if (!this.board.getSquare(57).isOccupied()
            && !this.board.getSquare(58).isOccupied()
            && !this.board.getSquare(59).isOccupied()) {

                final Collection<Move> attackOn58 = Player.calculateAttacksOnSquare(58, opponentLegals);
                final Collection<Move> attackOn59 = Player.calculateAttacksOnSquare(59, opponentLegals);

                if (attackOn59.isEmpty() && attackOn58.isEmpty()) {
                    // we need to check for the Rook at 56
                    if(this.board.getSquare(56).isOccupied()) {
                        // TODO: Uncomment once Rook class is created
                        // final Piece pieceAt56 = this.board.getSquare(56).getPiece();
                        // if (pieceAt56.isFirstMove() && pieceAt56.getPieceType().isRook()) {
                        //      kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing, 58, (Rook)pieceAt56, 59, 56));
                        // }
                    }
                }
            }
        }
        return kingCastles;
    }

}