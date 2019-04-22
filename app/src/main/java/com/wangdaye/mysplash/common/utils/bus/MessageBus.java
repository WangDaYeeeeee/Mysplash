package com.wangdaye.mysplash.common.utils.bus;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Message bus
 * */
public class MessageBus {

    private static MessageBus instance;

    public static MessageBus getInstance() {
        if (instance == null) {
            synchronized (MessageBus.class) {
                if (instance == null) {
                    instance = new MessageBus();
                }
            }
        }
        return instance;
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
