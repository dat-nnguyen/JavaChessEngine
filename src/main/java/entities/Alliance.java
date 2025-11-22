package entities;

import Players.BlackPlayer;
import Players.Player;
import Players.WhitePlayer;

public enum Alliance {
    WHITE {
        @Override
        public int getDirection() {
           return -1; // moving up means subtracting the coordinate index
        }

        @Override
        public boolean isWhite(){
            return true;
        }

        @Override
        public boolean isBlack(){
            return false;
        }
        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer,
                                   final BlackPlayer blackPlayer) {
            return whitePlayer;
        }
    },
    BLACK {
        @Override
        public int getDirection(){
            return 1; // moving down means adding the coordinate index
        }
        @Override
        public boolean isWhite(){
            return false;
        }
        @Override
        public boolean isBlack(){
            return true;
        }
        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer,
                                   final BlackPlayer blackPlayer) {
            return blackPlayer;
        }
    };

    // Every alliance must tell their direction
    public abstract int getDirection();
    public abstract boolean isWhite();
    public abstract boolean isBlack();

    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
