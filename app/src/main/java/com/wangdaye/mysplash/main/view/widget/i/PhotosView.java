package com.wangdaye.mysplash.main.view.widget.i;

import android.view.View;

/**
 * Photos view.
 * */

public interface PhotosView {

    View getPhotosView();

    void scrollToTop();

    void setRefreshing(boolean refreshing);
    void setLoading(boolean loading);
    void setPermitLoad(boolean permit);
    void resetRefreshLayout();

    boolean checkNeedRefresh();
    boolean checkNeedChangOrder(String order);
}
