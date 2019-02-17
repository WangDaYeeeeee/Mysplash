package com.wangdaye.mysplash.common.basic.activity;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Request load activity.
 * */

public abstract class RequestLoadActivity<T> extends ReadWriteActivity {

    @Nullable
    private List<T> waitingForUpdate;
    private boolean visible;

    @Override
    protected void onStart() {
        super.onStart();
        visible = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (waitingForUpdate != null) {
            for (T t : waitingForUpdate) {
                updateData(t);
            }
            waitingForUpdate = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        visible = false;
    }

    public void receiveUpdate(T t) {
        if (visible) {
            updateData(t);
        } else {
            if (waitingForUpdate == null) {
                waitingForUpdate = new ArrayList<>();
            }
            waitingForUpdate.add(t);
        }
    }

    public abstract void updateData(T t);
}
