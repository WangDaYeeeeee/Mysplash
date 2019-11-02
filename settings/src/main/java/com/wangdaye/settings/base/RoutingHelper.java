package com.wangdaye.settings.base;

import android.app.Activity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.wangdaye.settings.R;
import com.wangdaye.settings.activity.CustomApiActivity;
import com.wangdaye.settings.activity.SettingsActivity;

public class RoutingHelper {

    public static void startSettingsActivity(Activity a) {
        ARouter.getInstance()
                .build(SettingsActivity.SETTINGS_ACTIVITY)
                // .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a);
    }

    public static void startCustomApiActivity(Activity a, int requestCode) {
        ARouter.getInstance()
                .build(CustomApiActivity.CUSTOM_API_ACTIVITY)
                // .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a, requestCode);
    }
}
