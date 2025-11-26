package players;

import core.Move;
import entities.Alliance;
import entities.Board;
import entities.King;
import entities.Piece;
import entities.MoveStatus;
import entities.MoveTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    // Constructor
    Player(final Board board,
           final Collection<Move> legalMoves,
           final Collection<Move> opponentMoves) {

        this.board = board;
        this.playerKing = establishKing();

        // 1. Combine standard moves with Castling moves
        // We use a new list because the incoming 'legalMoves' might be unmodifiable
        final List<Move> combinedMoves = new ArrayList<>(legalMoves);

        // Calculate castles (Implemented in WhitePlayer/BlackPlayer)
        combinedMoves.addAll(calculateKingCastles(legalMoves, opponentMoves));

        this.legalMoves = Collections.unmodifiableList(combinedMoves);

        // 2. Calculate Check ONLY (Safe operation)
        // Checks if any enemy move targets the King's current position
        this.isInCheck = !Player.calculateAttacksOnSquare(
                this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
    }

    // --- ABSTRACT METHODS ---
    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();

    // Implemented in subclasses to handle specific King/Queen side rules
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
                                                             Collection<Move> opponentsLegals);

    // --- STATIC UTILITY ---
    public static Collection<Move> calculateAttacksOnSquare(final int piecePosition,
                                                            final Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        for (final Move move : moves) {
            if (piecePosition == move.getDestinationCoordinate()) {
                attackMoves.add(move);
            }
        }
        return attackMoves;
    }

    // --- CORE LOGIC ---

    private King establishKing() {
        for (final Piece piece : getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Invalid board! No King found!");
    }

    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    // Calculated On-Demand to prevent Infinite Recursion in Constructor
    public boolean isInCheckMate() {
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStaleMate() {
        return !this.isInCheck && !hasEscapeMoves();
    }

    // Placeholder for castling status (can be expanded later with move history)
    public boolean isCastled() {
        return false;
    }

    protected boolean hasEscapeMoves() {
        for (final Move move : this.legalMoves) {
            // Try to make the move on a virtual board
            final MoveTransition transition = makeMove(move);
            // If the move was successful (didn't leave king in check), we can escape
            if (transition.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }

    public MoveTransition makeMove(final Move move) {
        // 1. Check if move is structurally legal
        if (!isMoveLegal(move)) {
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }

        // 2. Execute move on a new board
        final Board transitionBoard = move.execute();

        // 3. Check if the move left the player's OWN king in check
        // On the new board, the current player is the OPPONENT.
        // So we check attacks on the OPPONENT'S opponent (Original Mover).
        final Collection<Move> kingAttacks = Player.calculateAttacksOnSquare(
                transitionBoard.getCurrentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                transitionBoard.getCurrentPlayer().getLegalMoves());

        // 4. If King is attacked, the move is invalid
        if (!kingAttacks.isEmpty()) {
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }

        // 5. Move is valid
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    // --- GETTERS ---
    public King getPlayerKing() {
        return this.playerKing;
    }

    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }
}