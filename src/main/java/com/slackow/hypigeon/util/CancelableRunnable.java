package com.slackow.hypigeon.util;

public class CancelableRunnable implements Runnable {

    private boolean isCancelled = false;

    public CancelableRunnable(Runnable run) {
        this.run = run;
    }

    private final Runnable run;

    @Override
    public void run() {
        if (!isCancelled) {
            run.run();
        }
    }

    public void cancel() {
        this.isCancelled = true;
    }

}
