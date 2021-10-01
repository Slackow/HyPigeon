package com.slackow.hypigeon.util;

import com.slackow.hypigeon.HyPigeon;

public class Task {
    private final CancelableRunnable runnable;

    public int getDelay() {
        return delay;
    }

    private int delay;

    public Task(CancelableRunnable runnable, int delay) {
        this.runnable = runnable;
        this.delay = delay;
        HyPigeon.scheduler.add(this);
    }

    public boolean decrementOrRun() {
        if (delay > 0) {
            delay--;
            return false;
        } else {
            runnable.run();
            return true;
        }
    }
}
