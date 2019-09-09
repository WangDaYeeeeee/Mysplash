package com.wangdaye.component.module;

import android.app.Activity;
import android.view.View;

import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.base.unsplash.User;

public interface UserModule {

    void startUserActivity(Activity a, View avatar, View background,
                           User u, @ProfilePager.ProfilePagerRule int page);

    void startUserActivity(Activity a, String username);
}
