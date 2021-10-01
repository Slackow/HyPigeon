package com.slackow.hypigeon.games.debug;

import com.slackow.hypigeon.games.core.AbstractSession;

class DebugSession extends AbstractSession {
    public DebugSession(String name) {
        super(name, true, new DebugGame());
    }

    public void toggle(){
        state = (state + 2) % 3 - 1;
    }
}
