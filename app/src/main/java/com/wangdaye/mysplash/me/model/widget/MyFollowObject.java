package com.wangdaye.mysplash.me.model.widget;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.service.UserService;
import com.wangdaye.mysplash._common.i.model.MyFollowModel;
import com.wangdaye.mysplash._common.ui.adapter.MyFollowAdapter;

/**
 * My follow object.
 * */

public class MyFollowObject implements MyFollowModel {
    // data
    private MyFollowAdapter adapter;
    private UserService service;

    private int followType;
    private int userPage;
    private int deltaValue;

    private boolean refreshing;
    private boolean loading;
    private boolean over;

    public static final int FOLLOW_TYPE_FOLLOWERS = 0;
    public static final int FOLLOW_TYPE_FOLLOWING = 1;

    /** <br> life cycle. */

    public MyFollowObject(MyFollowAdapter adapter, int followType) {
        this.adapter = adapter;
        this.service = UserService.getService();

        this.followType = followType;
        this.userPage = adapter.getItemCount() / Mysplash.DEFAULT_PER_PAGE;
        this.deltaValue = 0;

        this.refreshing = false;
        this.loading = false;
        this.over = false;
    }

    /** <br> model. */

    @Override
    public MyFollowAdapter getAdapter() {
        return adapter;
    }

    @Override
    public UserService getService() {
        return service;
    }

    @Override
    public int getFollowType() {
        return followType;
    }

    @Override
    public int getDeltaValue() {
        return deltaValue;
    }

    @Override
    public void setDeltaValue(int delta) {
        deltaValue += delta;
    }

    @Override
    public int getUsersPage() {
        return userPage;
    }

    @Override
    public void setUsersPage(int page) {
        this.userPage = page;
    }

    @Override
    public boolean isRefreshing() {
        return refreshing;
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    @Override
    public boolean isOver() {
        return over;
    }

    @Override
    public void setOver(boolean over) {
        this.over = over;
    }
}
