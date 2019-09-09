package com.wangdaye.component.module;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.wangdaye.base.pager.ProfilePager;

public interface MeModule {

    void startMeActivity(Activity a, View avatar, View background,
                         @ProfilePager.ProfilePagerRule int page);

    Intent getMeActivityIntentForShortcut();

    void startLoginActivity(Activity a);

    void startMyFollowActivity(Activity a);

    void startUpdateMeActivity(Activity a);
}
