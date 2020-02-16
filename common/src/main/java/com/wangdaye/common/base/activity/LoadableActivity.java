package com.wangdaye.common.base.activity;

import java.util.List;

/**
 * Loadable activity.
 * */

public abstract class LoadableActivity<T> extends ReadWriteActivity {

    public abstract List<T> loadMoreData(int currentCount);

    public abstract boolean isValidProvider(Class clazz);
}
