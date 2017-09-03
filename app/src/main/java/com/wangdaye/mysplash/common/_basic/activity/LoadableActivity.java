package com.wangdaye.mysplash.common._basic.activity;

import android.os.Bundle;

import java.util.List;

/**
 * Loadable activity.
 * */

public abstract class LoadableActivity<T> extends ReadWriteActivity {

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

    public abstract void updateData(T t);
}
