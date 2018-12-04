package com.wangdaye.mysplash.common.i.model;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.service.network.UserService;
import com.wangdaye.mysplash.common.ui.adapter.MyFollowAdapter;

/**
 * My follow model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.MyFollowView}.
 *
 * */

public interface MyFollowModel {

    MyFollowAdapter getAdapter();
    UserService getService();

    // manage HTTP request parameters.

    int getFollowType();

    int getUsersPage();
    void setUsersPage(@Mysplash.PageRule int page);

    // control load state.

    boolean isRefreshing();
    void setRefreshing(boolean refreshing);

    boolean isLoading();
    void setLoading(boolean loading);

    /** The flag to mark the photos already load over. */
    boolean isOver();
    void setOver(boolean over);

    // record.

    /**
     * +2 --> added 2 followers / followed 2 user.
     * -2 --> reduced 2 followers / canceled follow 2 user.
     * */
    int getDeltaValue();
    void setDeltaValue(int delta);
}
