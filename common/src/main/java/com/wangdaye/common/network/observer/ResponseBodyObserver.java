package com.wangdaye.common.network.observer;

import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

public abstract class ResponseBodyObserver extends DisposableObserver<ResponseBody> {

    public abstract void onSucceed(ResponseBody responseBody);
    public abstract void onFailed();

    @Override
    public void onNext(ResponseBody responseBody) {
        onSucceed(responseBody);
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
