package entities;

import core.Move;
import core.Move.*; // Import all inner move classes
import utils.BoardUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a Pawn piece in chess.
 * Handles all legal moves including single-step, double jump, attacks, en passant, and promotions.
 */
public class Pawn extends Piece {

    /** Candidate offsets for pawn movement:
     * 8 = single step forward
     * 16 = double step forward from starting position
     * 7 and 9 = diagonal captures (also used for en passant)
     */
    private final static int[] CANDIDATE_MOVE_COORDINATES = {8, 16, 7, 9};

    /** Constructs a Pawn with default first move as true */
    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance, PieceType.PAWN, true);
    }

    /** Constructs a Pawn with explicit first-move status */
    public Pawn(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(piecePosition, pieceAlliance, PieceType.PAWN, isFirstMove);
    }

    /**
     * Calculates all legal moves for this pawn on the given board.
     *
     * @param board the board to calculate moves on
     * @return a collection of legal moves
     */
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {

            // Compute destination considering pawn direction (up for WHITE, down for BLACK)
            final int candidateDestinationCoordinate =
                    this.getPiecePosition() + (this.getPieceAlliance().getDirection() * currentCandidateOffset);

            if (!BoardUtils.isValidSquareCoordinate(candidateDestinationCoordinate)) {
                continue; // Skip invalid squares
            }

            // --- Single Step Move (Forward by 1) ---
            if (currentCandidateOffset == 8 && !board.getSquare(candidateDestinationCoordinate).isOccupied()) {
                if (this.getPieceAlliance().isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    // Wrap in a promotion if reaching last rank
                    legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate)));
                } else {
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                }
            }

            // --- Double Step Move (Forward by 2) ---
            else if (currentCandidateOffset == 16 && this.isFirstMove() &&
                    ((BoardUtils.SEVENTH_RANK[this.getPiecePosition()] && this.getPieceAlliance().isBlack()) ||
                            (BoardUtils.SECOND_RANK[this.getPiecePosition()] && this.getPieceAlliance().isWhite()))) {

                final int behindCandidateDestinationCoordinate =
                        this.getPiecePosition() + (this.getPieceAlliance().getDirection() * 8);

                // Only allowed if both intermediate and destination squares are empty
                if (!board.getSquare(behindCandidateDestinationCoordinate).isOccupied() &&
                        !board.getSquare(candidateDestinationCoordinate).isOccupied()) {
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }
            }

            // --- Diagonal Captures & En Passant ---
            else if (currentCandidateOffset == 7 &&
                    !((BoardUtils.EIGHTH_COLUMN[this.getPiecePosition()] && this.getPieceAlliance().isWhite()) ||
                            (BoardUtils.FIRST_COLUMN[this.getPiecePosition()] && this.getPieceAlliance().isBlack()))) {

                handlePawnAttackOrEnPassant(board, legalMoves, candidateDestinationCoordinate, 7);
            }
            else if (currentCandidateOffset == 9 &&
                    !((BoardUtils.FIRST_COLUMN[this.getPiecePosition()] && this.getPieceAlliance().isWhite()) ||
                            (BoardUtils.EIGHTH_COLUMN[this.getPiecePosition()] && this.getPieceAlliance().isBlack()))) {

                handlePawnAttackOrEnPassant(board, legalMoves, candidateDestinationCoordinate, 9);
            }
        }

        return legalMoves;
    }

    /**
     * Handles diagonal pawn attacks and en passant logic.
     *
     * @param board the current board
     * @param legalMoves the list to append legal moves
     * @param candidateDestinationCoordinate the destination square
     * @param offset the current candidate offset (7 or 9)
     */
    private void handlePawnAttackOrEnPassant(final Board board, final List<Move> legalMoves,
                                             final int candidateDestinationCoordinate, final int offset) {
        // Capture on occupied square
        if (board.getSquare(candidateDestinationCoordinate).isOccupied()) {
            final Piece pieceOnCandidate = board.getSquare(candidateDestinationCoordinate).getPiece();
            if (this.getPieceAlliance() != pieceOnCandidate.getPieceAlliance()) {
                if (this.getPieceAlliance().isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                } else {
                    legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                }
            }
        }
        // En Passant
        else if (board.getEnPassantPawn() != null) {
            final int enPassantTarget = offset == 7
                    ? this.getPiecePosition() + (this.getPieceAlliance().getOppositeDirection() * 1)
                    : this.getPiecePosition() - (this.getPieceAlliance().getOppositeDirection() * 1);

            if (board.getEnPassantPawn().getPiecePosition() == enPassantTarget) {
                final Piece pieceOnCandidate = board.getEnPassantPawn();
                if (this.getPieceAlliance() != pieceOnCandidate.getPieceAlliance()) {
                    legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                }
            }
        }
    }

    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

    /**
     * Returns the piece that this pawn promotes to (default is Queen).
     *
     * @return a new Queen piece for promotion
     */
    public Piece getPromotionPiece() {
        return new Queen(this.getPiecePosition(), this.getPieceAlliance(), false);
    }
}
