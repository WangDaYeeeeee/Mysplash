package com.wangdaye.common.ui.adapter.collection;

import android.view.View;

import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.component.ComponentFactory;

public class CollectionItemEventHelper implements CollectionAdapter.ItemEventCallback {

    private MysplashActivity activity;

    public CollectionItemEventHelper(MysplashActivity a) {
        activity = a;
    }

    @Override
    public void onCollectionClicked(View avatar, View background, Collection c) {
        ComponentFactory.getCollectionModule()
                .startCollectionActivity(activity, avatar, background, c);
    }

    @Override
    public void onUserClicked(View avatar, View background, User u) {
        ComponentFactory.getUserModule()
                .startUserActivity(activity, avatar, background, u, ProfilePager.PAGE_PHOTO);
    }
}
