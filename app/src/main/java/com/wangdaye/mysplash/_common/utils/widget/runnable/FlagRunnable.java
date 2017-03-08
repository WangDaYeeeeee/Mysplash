package com.wangdaye.mysplash._common.utils.widget.runnable;

/**
 * Flag thread.
 * */

public abstract class FlagRunnable implements Runnable {
    // data
    private boolean running = true;

    public FlagRunnable(boolean running) {
        this.running = running;
    }

    /** <br> life cycle. */

    public void setRunning(boolean b) {
        this.running = b;
    }

    public boolean isRunning() {
        return running;
    }
}
