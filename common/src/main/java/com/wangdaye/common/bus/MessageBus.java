package com.wangdaye.common.bus;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Message bus
 * */
public class MessageBus {

    private static class Inner{
        private static MessageBus instance = new MessageBus();
    }

    public static MessageBus getInstance() {
        return Inner.instance;
    }

    private final Subject<Object> bus;

    private MessageBus() {
        bus = PublishSubject.create().toSerialized();
    }

    public void post(Object action) {
        bus.onNext(action);
    }

    public <T> Observable<T> toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }
}
