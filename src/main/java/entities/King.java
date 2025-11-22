package entities;

import core.Move;
import utils.BoardUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King extends Piece {
    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(final int piecePosition, final Alliance pieceAlliance){
        super(piecePosition, pieceAlliance, PieceType.KING, true);
    }
    public King(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove){
        super(piecePosition, pieceAlliance, PieceType.KING, isFirstMove);
    }
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES) {

            final int candidateDestinationCoordinate = this.getPiecePosition() + currentCandidateOffset;

            if (BoardUtils.isValidSquareCoordinate(candidateDestinationCoordinate)) {

                if (isFirstColumnExclusion(this.getPiecePosition(), currentCandidateOffset) ||
                        isEighthColumnExclusion(this.getPiecePosition(), currentCandidateOffset)) {
                    continue;
                }

                final Square candidateSquare = board.getSquare(candidateDestinationCoordinate);

                if (!candidateSquare.isOccupied()) {
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = candidateSquare.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                    if (this.getPieceAlliance() != pieceAlliance) {
                        legalMoves.add(new Move.AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                }
            }
        }
        return legalMoves;
    }
    @Override
    public King movePiece(Move move) {
        return new King(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false);
    }

    @Override
    public String toString() {
        return PieceType.KING.toString();
    }

    // --- HELPERS ---
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        // Block Left (-1), Up-Left (-9), Down-Left (7)
        return BoardUtils.FIRST_COLUMN[currentPosition] &&
                (candidateOffset == -9 || candidateOffset == -1 || candidateOffset == 7);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        // Block Right (1), Up-Right (-7), Down-Right (9)
        return BoardUtils.EIGHTH_COLUMN[currentPosition] &&
                (candidateOffset == -7 || candidateOffset == 1 || candidateOffset == 9);
    }
}
