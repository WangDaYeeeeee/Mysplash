package com.wangdaye.mysplash.common.basic.fragment;

import java.util.List;

/**
 * Loadable fragment.
 * */

public abstract class LoadableFragment<T> extends MysplashFragment {

    /**
     * {@link com.wangdaye.mysplash.common.basic.activity.LoadableActivity#loadMoreData(List, int, boolean)}.
     * */
    public abstract List<T> loadMoreData(List<T> list, int headIndex, boolean headDirection);
}
