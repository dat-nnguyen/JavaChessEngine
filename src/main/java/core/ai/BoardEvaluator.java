package core.ai;

import entities.Board;

public interface BoardEvaluator {

    // return integer score
    // positive = white
    // negative = black
    int evaluate (Board board, int depth);

}
