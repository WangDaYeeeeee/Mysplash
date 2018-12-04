package com.wangdaye.mysplash.common.i.model;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.service.network.CollectionService;
import com.wangdaye.mysplash.common.ui.adapter.SelectedAdapter;

/**
 * Selected model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.SelectedView}.
 *
 * */

public interface SelectedModel {

    SelectedAdapter getAdapter();
    CollectionService getService();

    // manage HTTP request parameters.

    Object getRequestKey();
    void setRequestKey(Object key);

    @Mysplash.PageRule
    int getCollectionsPage();
    void setCollectionsPage(@Mysplash.PageRule int page);

    // control load state.

    boolean isRefreshing();
    void setRefreshing(boolean refreshing);

    boolean isLoading();
    void setLoading(boolean loading);

    /** The flag to mark the photos already load over. */
    boolean isOver();
    void setOver(boolean over);
}
