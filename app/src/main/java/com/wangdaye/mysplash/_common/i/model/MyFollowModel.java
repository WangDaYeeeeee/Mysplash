package com.wangdaye.mysplash._common.i.model;

import com.wangdaye.mysplash._common.data.service.UserService;
import com.wangdaye.mysplash._common.ui.adapter.MyFollowAdapter;

/**
 * My follow model.
 * */

public interface MyFollowModel {

    MyFollowAdapter getAdapter();
    UserService getService();

    int getFollowType();

    int getDeltaValue();
    void setDeltaValue(int delta);

    int getUsersPage();
    void setUsersPage(int page);

    boolean isRefreshing();
    void setRefreshing(boolean refreshing);

    boolean isLoading();
    void setLoading(boolean loading);

    boolean isOver();
    void setOver(boolean over);
}
