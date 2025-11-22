package Players;

import core.Move;
import entities.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    Player(final Board board,
           final Collection<Move> legalMoves,
           final Collection<Move> opponentMoves) {

        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = legalMoves;
        // logic: If the list of attacks on my King is NOT empty, I am in check.
        this.isInCheck = !Player.calculateAttacksOnSquare(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
    }

    // --- ABSTRACT METHODS ---
    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();

    // --- IMPLEMENTED METHODS ---

    public static Collection<Move> calculateAttacksOnSquare(final int piecePosition,
                                                            final Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        // Loop through all enemy moves. If a move lands on the specific square, it is an attack.
        for (final Move move : moves) {
            if (piecePosition == move.getDestinationCoordinate()) {
                attackMoves.add(move);
            }
        }
        return attackMoves;
    }

    private King establishKing() {
        for (final Piece piece : getActivePieces()) {
            // Ensure PieceType has this check, or use: piece.getPieceType() == PieceType.KING
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Not a valid board! No King found!");
    }

    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    public boolean isInCheckMate() {
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStaleMate() {
        return !this.isInCheck && !hasEscapeMoves();
    }

    protected boolean hasEscapeMoves() {
        for (final Move move : this.legalMoves) {
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }

    public MoveTransition makeMove(final Move move) {
        if (!isMoveLegal(move)) {
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }

        final Board transitionBoard = move.execute();

        // Calculate attacks on the King in the NEW board
        final Collection<Move> kingAttacks = Player.calculateAttacksOnSquare(
                transitionBoard.getCurrentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                transitionBoard.getCurrentPlayer().getLegalMoves());

        if (!kingAttacks.isEmpty()) {
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }

        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    public King getPlayerKing() {
        return this.playerKing;
    }

    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    // In Player.java

    // 1. Add this abstract method so WhitePlayer/BlackPlayer are forced to implement it.
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
                                                             Collection<Move> opponentsLegals);

    // 2. IMPORTANT: Update your constructor to CALL this method!
    // Change the line "this.legalMoves = legalMoves;" to this:
    /*
       this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves,
                         calculateKingCastles(legalMoves, opponentMoves)));
    */
    // OR, if you aren't using Guava (Google Lib), use standard Java:
    /*
       final List<Move> combinedMoves = new ArrayList<>(legalMoves);
       combinedMoves.addAll(calculateKingCastles(legalMoves, opponentMoves));
       this.legalMoves = Collections.unmodifiableList(combinedMoves);
    */
}