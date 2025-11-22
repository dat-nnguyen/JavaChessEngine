package entities;

import core.Move;
import utils.BoardUtils;
import core.Move.MajorMove;
import core.Move.AttackMove;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Rook extends Piece {
    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-8, -1, 1, 8};

    public Rook(final int piecePosition, final Alliance pieceAlliance){
        super(piecePosition, pieceAlliance, PieceType.ROOK, true);
    }

    public Rook(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove){
        super(piecePosition, pieceAlliance, PieceType.ROOK, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for(final int candidateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES){

            int candidateDestinationCoordinate = this.getPiecePosition();

            // logic: keep moving until we hit a square that is occupied or the edge of the board
            while(BoardUtils.isValidSquareCoordinate(candidateDestinationCoordinate)){
                // edge case: if we are on the edge, we can't go any further
                if(isFirstColumnExclusion(candidateDestinationCoordinate, candidateOffset) ||
                        isEighthColumnExclusion(candidateDestinationCoordinate, candidateOffset)) {
                    break;
                }

                candidateDestinationCoordinate += candidateOffset;

                if(!BoardUtils.isValidSquareCoordinate(candidateDestinationCoordinate)) {
                    break;
                }

                //check the square
                final Square candidateSquare = board.getSquare(candidateDestinationCoordinate);

                if(!candidateSquare.isOccupied()) {
                    // if empty, keep moving
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                } else {
                    // check for captured if not empty
                    final Piece pieceAtDestination = candidateSquare.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                    if(this.getPieceAlliance() != pieceAlliance) {
                        legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                    // hit a piece then stop moving
                    break;
                }
            }
        }
        return legalMoves;
    }

    @Override
    public Rook movePiece(final Move move){
        return new Rook(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString() {
        return PieceType.ROOK.toString();
    }
    //--HELPER METHODS--
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        // if on first column, we can't go further
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1);
    }
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == 1);
    }
}
