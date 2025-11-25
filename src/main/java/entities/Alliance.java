package entities;

import players.BlackPlayer;
import players.Player;
import players.WhitePlayer;
import utils.BoardUtils;

/**
 * Enum representing the two sides in a chess game: WHITE and BLACK.
 * <p>
 * Contains logic for movement direction, pawn promotion rank, and selecting the corresponding player.
 */
public enum Alliance {

    WHITE {
        @Override
        public int getDirection() {
            return -1; // White moves up (subtracting from coordinate index)
        }

        @Override
        public boolean isWhite() { return true; }

        @Override
        public boolean isBlack() { return false; }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return whitePlayer;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return BoardUtils.EIGHTH_RANK[position]; // White promotes on 8th rank
        }

        @Override
        public int getOppositeDirection() {
            return 1; // Opposite of white movement direction
        }
    },

    BLACK {
        @Override
        public int getDirection() { return 1; } // Black moves down (adding to coordinate index)

        @Override
        public boolean isWhite() { return false; }

        @Override
        public boolean isBlack() { return true; }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return blackPlayer;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return BoardUtils.FIRST_RANK[position]; // Black promotes on 1st rank
        }

        @Override
        public int getOppositeDirection() { return -1; } // Opposite of black movement direction
    };

    // --- ABSTRACT METHODS FOR ALLIANCES ---
    public abstract int getDirection();
    public abstract boolean isWhite();
    public abstract boolean isBlack();
    public abstract boolean isPawnPromotionSquare(int position);
    public abstract int getOppositeDirection();
    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}