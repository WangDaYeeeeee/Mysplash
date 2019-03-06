package com.wangdaye.mysplash.common.basic;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Safe handler.
 *
 * A handler that can not be static.
 *
 * */

public class SafeHandler<T extends SafeHandler.HandlerContainer> extends Handler {

    private WeakReference<T> mRef;

    public SafeHandler(T obj) {
        mRef = new WeakReference<>(obj);
    }

    public T getContainer() {
        return mRef.get();
    }

    @Override
    public void handleMessage(android.os.Message msg) {
        super.handleMessage(msg);
        HandlerContainer container = getContainer();
        if (container != null) {
            container.handleMessage(msg);
        }
    }

    public interface HandlerContainer {
        void handleMessage(Message message);
    }
}
