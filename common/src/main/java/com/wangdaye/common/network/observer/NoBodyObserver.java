package com.wangdaye.common.network.observer;

import androidx.annotation.Nullable;

import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

public class NoBodyObserver extends DisposableObserver<ResponseBody> {

    @Nullable private OnResultHandler handler;

    public NoBodyObserver(@Nullable OnResultHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        if (handler != null) {
            handler.onResult(true);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (handler != null) {
            handler.onResult(false);
        }
    }

    @Override
    public void onComplete() {
        // do nothing.
    }

    public interface OnResultHandler {
        void onResult(boolean succeed);
    }
}
