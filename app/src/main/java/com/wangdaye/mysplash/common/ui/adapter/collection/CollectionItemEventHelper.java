package com.wangdaye.mysplash.common.ui.adapter.collection;

import android.view.View;

import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.user.ui.UserActivity;

public class CollectionItemEventHelper implements CollectionAdapter.ItemEventCallback {

    private MysplashActivity activity;

    public CollectionItemEventHelper(MysplashActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onCollectionClicked(View avatar, View background, Collection c) {
        if (activity != null) {
            IntentHelper.startCollectionActivity(activity, avatar, background, c);
        }
    }

    @Override
    public void onUserClicked(View avatar, View background, User u) {
        if (activity != null) {
            IntentHelper.startUserActivity(activity, avatar, background, u, UserActivity.PAGE_PHOTO);
        }
    }
}
