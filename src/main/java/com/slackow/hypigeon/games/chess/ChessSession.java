package com.slackow.hypigeon.games.chess;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import com.slackow.hypigeon.games.core.AbstractSession;

import java.util.List;

import static com.github.bhlangonijr.chesslib.Side.BLACK;
import static com.github.bhlangonijr.chesslib.Side.WHITE;

public class ChessSession extends AbstractSession<ChessSession> {


    private final Board board = new Board();

    private List<Move> legalMoves = board.legalMoves();

    // centiseconds
    private long myTimer;
    private long theirTimer;

    private long lastMoveTime = 0;


    protected ChessSession(String opponent, boolean doIMoveFirst) {
        this(opponent, doIMoveFirst, -1);
    }

    public void updateTimer(long newTime) {
        if (isTimeControlled()) {
            myTimer = newTime;
            lastMoveTime = System.currentTimeMillis() / 10;
        }
    }

    public void setTheirTimer(long theirTimer) {
        this.theirTimer = theirTimer;
    }

    public boolean isTimeControlled() {
        return myTimer >= 0;
    }

    protected ChessSession(String opponent, boolean doIMoveFirst, long timeControl) {
        super(opponent, doIMoveFirst, ChessGame.INSTANCE);
        myTimer = timeControl;
        theirTimer = timeControl;
    }


    /**
     * <p> state == 1 means white wins </p>
     * <p> state == 2 means black wins </p>
     */
    public void checkWin() {

        if (myTimer == 0) {
            state = 1;
            return;
        }

        legalMoves = board.legalMoves();
        if (board.isDraw()) {
            state = -1;
        } else {
            if (board.isMated()) {
                state = board.getSideToMove().ordinal() + 1;
            }
        }
    }


    public Side getMySide(){
        return doIMoveFirst() ? WHITE : BLACK;
    }

    public List<Move> legalMoves() {
        return legalMoves;
    }

    public Board getBoard() {
        return board;
    }

    public long displayTimer() {
        if (isTimeControlled()) {
            return Math.max(myTimer - (System.currentTimeMillis() / 10 - lastMoveTime), 0);
        } else {
            return -1;
        }
    }

    public void updateLast() {
        // current centisecond
        lastMoveTime = System.currentTimeMillis() / 10;
    }

    public long sinceLast(){
        return System.currentTimeMillis() / 10 - lastMoveTime;
    }

    public long getTheirTimer() {
        return theirTimer >= 0 && waitingOnOpponent() ? theirTimer - sinceLast() : theirTimer;
    }
}
