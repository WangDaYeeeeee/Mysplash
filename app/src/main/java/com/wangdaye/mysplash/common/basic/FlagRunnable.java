package com.wangdaye.mysplash.common.basic;

/**
 * Flag runnable.
 *
 * A Runnable class with a running flag. The flag can be used as a loop condition.
 *
 * */

public abstract class FlagRunnable implements Runnable {

    private boolean running = true;

    public FlagRunnable(boolean running) {
        this.running = running;
    }

    public void setRunning(boolean b) {
        this.running = b;
    }

    public boolean isRunning() {
        return running;
    }
}
