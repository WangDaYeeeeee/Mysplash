package com.wangdaye.common.base.activity;

import java.util.List;

/**
 * Loadable activity.
 * */

public abstract class LoadableActivity<T> extends ReadWriteActivity {

    /**
     * Load more data. Called from outside.
     *
     * @param list The list in outside from this activity.
     * @param headIndex The index of outside list's first item in inner list.
     * @param headDirection Set true if outside activity is requesting the photos that are closer
     *                      to the head index than 'headerIndex'.
     *
     * @return The list that has been loaded.
     * */
    public abstract List<T> loadMoreData(List<T> list, int headIndex, boolean headDirection);
}
