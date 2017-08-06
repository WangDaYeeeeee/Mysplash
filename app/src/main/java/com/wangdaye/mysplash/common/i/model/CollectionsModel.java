package com.wangdaye.mysplash.common.i.model;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.service.CollectionService;
import com.wangdaye.mysplash.common.ui.adapter.CollectionAdapter;

/**
 * Collections model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.CollectionsView}.
 *
 * */

public interface CollectionsModel {

    CollectionAdapter getAdapter();
    CollectionService getService();

    // manage HTTP request parameters.

    Object getRequestKey();
    void setRequestKey(Object key);

    @Mysplash.CollectionTypeRule
    int getCollectionsType();
    void setCollectionsType(int type);

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
