package com.slackow.hypigeon.games.tictactoe;

import com.slackow.hypigeon.games.core.AbstractSession;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.stream.IntStream;

public class TicTacToeSession extends AbstractSession<TicTacToeSession> {

    private final char[][] data = new char[3][3];

    {
        for (char[] datum : data) {
            Arrays.fill(datum, ' ');
        }
    }

    private final char player;

    public TicTacToeSession(String opponent, boolean doIMoveFirst) {
        super(opponent, doIMoveFirst, TicTacToeGame.INSTANCE);
        player = doIMoveFirst() ? 'X' : 'O';
        System.out.println(player + " " + opponent);
    }

    public char[][] getData() {
        return data;
    }

    /**
     * Checks for ties
     * <p>
     * will update state to a number corresponding to where to draw the line, unless there's a tie or there is no win present.
     * <p>
     * -1 -> tie
     * 0 -> No win (game still in progress)
     * 1-3 -> Vertical line
     * 4-6 -> Horizontal line
     * 7-8 -> Diagonal line
     */
    public void checkWin() {
        state = checkWin('X');
        if (getState() == 0) {
            state = checkWin('O');
        }
        if (getState() == 0) {
            state = Arrays.stream(data).anyMatch(chars -> ArrayUtils.contains(chars, ' ')) ? 0 : -1;
        }
    }

    /**
     * does not check for ties
     * <p>
     * will return a number corresponding to where to draw the line, unless there is no win present.
     * <p>
     * 0 -> No win (game still in progress)
     * 1-3 -> Vertical line
     * 4-6 -> Horizontal line
     * 7-8 -> Diagonal line
     *
     * @param player which player to check 'X' or 'O'
     * @return the win condition
     */
    private int checkWin(char player) {

        row:
        for (int i = 0; i < data.length; i++) {
            for (char c : data[i]) {
                if (c != player) {
                    continue row;
                }
            }
            return i + 1;
        }

        column:
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[j][i] != player) {
                    continue column;
                }
            }
            return i + 4;
        }

        if (IntStream.of(data[0][0], data[1][1], data[2][2]).allMatch(i -> i == player)) return 7;

        if (IntStream.of(data[0][2], data[1][1], data[2][0]).allMatch(i -> i == player)) return 8;

        return 0;
    }

    public boolean tryMove(int row, int column, char c) {
        if (data[row][column] == ' ') {
            data[row][column] = c;
            return true;
        }
        return false;
    }

    public char getPlayer() {
        return player;
    }
}
