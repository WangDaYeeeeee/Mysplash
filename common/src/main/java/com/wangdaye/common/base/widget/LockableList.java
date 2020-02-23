package com.wangdaye.common.base.widget;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockableList<T> {

    private List<T> list;
    private ReadWriteLock lock;

    private Setter<T> setter;

    public LockableList(List<T> list) {
        this.list = list;
        this.lock = new ReentrantReadWriteLock();

        this.setter = l -> this.list = l;
    }

    public void read(Reader<T> reader) {
        lock.readLock().lock();
        reader.read(Collections.unmodifiableList(list));
        lock.readLock().unlock();
    }

    public void write(Writer<T> writer) {
        lock.writeLock().lock();
        writer.write(list, setter);
        lock.writeLock().unlock();
    }

    public interface Reader<T> {
        void read(List<T> list);
    }

    public interface Writer<T> {
        void write(List<T> list, Setter<T> setter);
    }

    public interface Setter<T> {

        void setList(List<T> list);
    }
}
