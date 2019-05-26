package com.wangdaye.mysplash.common.presenter.event;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public abstract class BaseEventResponsePresenter {

    private static Executor executor;
    private static final Object executorLock = new Object();
    protected static Executor getExecutor() {
        if (executor == null) {
            synchronized (executorLock) {
                if (executor == null) {
                    executor = Executors.newSingleThreadExecutor();
                }
            }
        }
        return executor;
    }

    protected CompositeDisposable compositeDisposable;

    protected abstract class SimpleDisposableObserver<T> extends DisposableObserver<T> {
        @Override
        protected void onStart() {
            compositeDisposable.add(this);
        }

        @Override
        public void onError(Throwable e) {
            compositeDisposable.remove(this);
        }

        @Override
        public void onComplete() {
            compositeDisposable.remove(this);
        }
    }

    public BaseEventResponsePresenter(CompositeDisposable disposable) {
        compositeDisposable = disposable;
    }

    public void clearResponse() {
        compositeDisposable.clear();
    }
}

