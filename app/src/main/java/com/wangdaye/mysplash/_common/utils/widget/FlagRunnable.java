package com.wangdaye.mysplash._common.utils.widget;

/**
 * Flag thread.
 * */

public abstract class FlagRunnable extends PriorityRunnable {
    // data
    private boolean running = true;

    /** <br> life cycle. */

    public FlagRunnable(boolean runNow) {
        super(runNow);
    }

    public void setRunning(boolean b) {
        this.running = b;
    }

    public boolean isRunning() {
        return running;
    }
}
