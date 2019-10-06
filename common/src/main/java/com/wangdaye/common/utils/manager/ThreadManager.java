package com.wangdaye.common.utils.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread manager.
 *
 * A manager that is used to manage threads.
 *
 * */

public class ThreadManager {

    private static class Inner{
        private static ThreadManager instance = new ThreadManager();
    }

    public static ThreadManager getInstance() {
        return Inner.instance;
    }

    private ExecutorService threadPool;

    private ThreadManager() {
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void execute(Runnable runnable) {
        threadPool.execute(runnable);
    }
}
