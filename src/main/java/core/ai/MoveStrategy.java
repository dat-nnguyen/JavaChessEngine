package core.ai;

import core.Move;
import entities.Board;

public interface MoveStrategy {
    //as the AI: here is the board, what move should I make?
    Move execute(Board board);
}
