package com.slackow.hypigeon.games.core;

import com.google.common.collect.Lists;
import com.slackow.hypigeon.games.core.AbstractGame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractSession<T extends AbstractSession<T>> {
    protected final String opponent;
    private final boolean doIMoveFirst;

    private final AbstractGame game;
    /**
     * Tells you about the state of the game, 0 if
     * it's still happening, every other value means
     * the game is over, it's up the game
     * what the specific number means.
     * -1 usually means tie and -2 usually means resigned.
     */
    protected int state;
    /**
     * stores whether it's your current move or not
     * and the gui/input should be blocked, or wide open.
     */
    private boolean lock;


    /**
     * This list is often added to at the front, so I made it a reverse
     * arraylist to make it faster.
     */
    public final List<String> messages = Lists.reverse(new ArrayList<>());

    protected AbstractSession(String opponent, boolean doIMoveFirst, AbstractGame game) {
        this.opponent = opponent;
        this.doIMoveFirst = doIMoveFirst;
        if (!doIMoveFirst) {
            lock();
        }
        this.game = game;
    }


    /**
     * Used to check if the game is over, runs on every sent
     * and received move. Can also be used to do things every move.
     */
    public void checkWin(){}

    @Override
    public String toString() {
        return game.getName() + " against " + opponent;
    }

    public boolean doIMoveFirst() {
        return doIMoveFirst;
    }

    public String getOpponent() {
        return opponent;
    }

    public AbstractGame getGame() {
        return game;
    }

    public int getState() {
        return state;
    }

    public boolean isGameOver() {
        return state != 0;
    }

    public boolean waitingOnOpponent() {
        return lock;
    }

    public boolean isLocked(){
        return waitingOnOpponent() || isGameOver();
    }

    public boolean didIWin(){
        return isGameOver() && waitingOnOpponent();
    }

    public void lock() {
        lock = true;
    }

    public void unlock() {
        lock = false;
    }

    public void resign() {
        state = -2;
    }
}
