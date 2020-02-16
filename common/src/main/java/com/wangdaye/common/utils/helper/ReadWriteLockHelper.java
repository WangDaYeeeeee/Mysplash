package com.wangdaye.common.utils.helper;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockHelper<T> {

    private ReadWriteLock lock;

    public ReadWriteLockHelper() {
        this.lock = new ReentrantReadWriteLock();
    }

    public void read(List<T> list, DataListExecutor<T> reader) {
        lock.readLock().lock();
        reader.execute(list);
        lock.readLock().unlock();
    }

    public void write(List<T> list, DataListExecutor<T> writer) {
        lock.writeLock().lock();
        writer.execute(list);
        lock.writeLock().unlock();
    }

    public interface DataListExecutor<T> {
        void execute(List<T> list);
    }
}
