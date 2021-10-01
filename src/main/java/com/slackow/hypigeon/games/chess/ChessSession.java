package com.slackow.hypigeon.games.chess;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.slackow.hypigeon.games.core.AbstractSession;

import java.util.List;

public class ChessSession extends AbstractSession {


    private final Board board = new Board();

    private List<Move> legalMoves = board.legalMoves();


    protected ChessSession(String opponent, boolean doIMoveFirst) {
        super(opponent, doIMoveFirst, ChessGame.INSTANCE);
    }

    public void checkWin(){
        legalMoves = board.legalMoves();
        if (board.isDraw() || board.isStaleMate() || board.isInsufficientMaterial() || board.isRepetition()) {
            state = -1;
        } else {
            if (board.isMated()) {
                state = board.getSideToMove().ordinal() + 1;
            }
        }
    }

    public List<Move> legalMoves(){
        return legalMoves;
    }

    public Board getBoard() {
        return board;
    }
}
