package com.wangdaye.mysplash.common.network.observer;

import io.reactivex.observers.DisposableObserver;

public abstract class NoBodyObserver<T> extends DisposableObserver<T> {

    public abstract void onSucceed(T t);

    public abstract void onFailed();

    @Override
    public void onNext(T t) {
        onSucceed(t);
    }

    @Override
    public void onError(Throwable e) {
        onFailed();
    }

    @Override
    public void onComplete() {
        // do nothing.
    }
}
