package com.wangdaye.common.base.widget;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockableData<T> {

    private T t;
    private ReadWriteLock lock;

    private Setter<T> setter;

    public LockableData(T t) {
        this.t = t;
        this.lock = new ReentrantReadWriteLock();

        this.setter = d -> this.t = d;
    }

    public void read(Reader<T> reader) {
        lock.readLock().lock();
        reader.read(t);
        lock.readLock().unlock();
    }

    public void write(Writer<T> writer) {
        lock.writeLock().lock();
        writer.write(t, setter);
        lock.writeLock().unlock();
    }

    public interface Reader<T> {
        void read(T t);
    }

    public interface Writer<T> {
        void write(T t, Setter<T> setter);
    }

    public interface Setter<T> {

        void setData(T t);
    }
}
