package com.wangdaye.mysplash._common.utils.widget;

/**
 * Flag thread.
 * */

public class FlagThread extends Thread {
    // data
    private boolean running = true;

    /** <br> life cycle. */

    public FlagThread(Runnable target) {
        super(target);
    }

    /** <br> data. */

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }
}
