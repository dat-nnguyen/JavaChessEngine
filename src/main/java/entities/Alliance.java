package entities;

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
    };

    // Every alliance must tell their direction
    public abstract int getDirection();
    public abstract boolean isWhite();
    public abstract boolean isBlack();
}
