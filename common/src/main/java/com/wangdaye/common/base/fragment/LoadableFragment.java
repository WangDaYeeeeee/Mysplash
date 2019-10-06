package com.wangdaye.common.base.fragment;

import com.wangdaye.common.base.activity.LoadableActivity;

import java.util.List;

/**
 * Loadable fragment.
 * */

public abstract class LoadableFragment<T> extends MysplashFragment {

    /**
     * {@link LoadableActivity#loadMoreData(List, int, boolean)}.
     * */
    public abstract List<T> loadMoreData(List<T> list, int headIndex, boolean headDirection);
}
