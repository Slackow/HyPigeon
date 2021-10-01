package com.slackow.hypigeon.games.core;

import com.slackow.hypigeon.games.core.AbstractGame;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractSession {
    protected final String opponent;
    private final boolean doIMoveFirst;

    private final AbstractGame game;
    protected int state;
    private boolean lock;

    public final List<String> messages = new LinkedList<>();

    protected AbstractSession(String opponent, boolean doIMoveFirst, AbstractGame game) {
        this.opponent = opponent;
        this.doIMoveFirst = doIMoveFirst;
        if (!doIMoveFirst) {
            lock();
        }
        this.game = game;
    }


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
