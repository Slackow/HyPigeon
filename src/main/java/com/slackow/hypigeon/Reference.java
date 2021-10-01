package com.slackow.hypigeon;

public class Reference {
    public static final byte TIC_TAC_TOE = 0;
    public static final byte CHESS = 1;
    public static final byte CHECKERS = 2;
    public static final byte BATTLESHIP = 3;
    public static final byte CONNECT4 = 4;
    public static final byte GOMOKU = 5;
    public static final byte FILLER = 6;
    public static final byte MANCALA = 7;

    public static final byte INVITE = 0;
    public static final byte CANCEL_INVITE = 1;
    public static final byte ACCEPT = 2;
    public static final byte DENY = 3;
    public static final byte NEXT_MOVE = 4;
    public static final byte RESUME = 5;
    public static final byte SAVE = 6;
    public static final byte REMATCH = 7;
    public static final byte ACCEPT_REMATCH = 8;
    public static final byte DENY_REMATCH = 9;
    public static final byte RESIGN = 10;

    public static final byte HOST_MOVES_FIRST = 0;
    public static final byte HOST_MOVES_SECOND = 1;
    public static final byte RANDOM = 2;

    public static final int GAME_INDEX = 0;
    public static final int MESSAGE_TYPE = 1;
    public static final int WHO_GOES_FIRST = 2;
}
