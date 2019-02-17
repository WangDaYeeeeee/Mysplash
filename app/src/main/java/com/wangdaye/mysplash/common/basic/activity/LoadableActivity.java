package com.wangdaye.mysplash.common.basic.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Loadable activity.
 * */

public abstract class LoadableActivity<T> extends ReadWriteActivity {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        visible = false;
    }

    /**
     * Load more data. Called from outside by {@link RequestLoadActivity}.
     *
     * @param list The list of {@link RequestLoadActivity}.
     * @param headIndex The index of outside list's first item in inner list.
     * @param headDirection Set true if the {@link RequestLoadActivity} is requesting the photos
     *                      that are closer to the head index than 'headerIndex'.
     * @param bundle A bundle is sent to the {@link RequestLoadActivity}. It was recorded some
     *               keyword associated with the list.
     *
     * @return The list that has been loaded.
     * */
    public abstract List<T> loadMoreData(List<T> list, int headIndex, boolean headDirection,
                                         Bundle bundle);

    public abstract Bundle getBundleOfList();

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
