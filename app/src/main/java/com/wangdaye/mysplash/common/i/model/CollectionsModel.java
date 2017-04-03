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

    Object getRequestKey();
    void setRequestKey(Object key);

    String getCollectionsType();
    void setCollectionsType(String type);

    @Mysplash.PageRule
    int getCollectionsPage();
    void setCollectionsPage(@Mysplash.PageRule int page);

    boolean isRefreshing();
    void setRefreshing(boolean refreshing);

    boolean isLoading();
    void setLoading(boolean loading);

    /** The flag to mark the photos already load over. */
    boolean isOver();
    void setOver(boolean over);
}
