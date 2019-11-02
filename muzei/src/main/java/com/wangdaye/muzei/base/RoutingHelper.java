package com.wangdaye.muzei.base;

import android.app.Activity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.wangdaye.common.R;
import com.wangdaye.muzei.activity.MuzeiCollectionSourceConfigActivity;
import com.wangdaye.muzei.activity.MuzeiSettingsActivity;

public class RoutingHelper extends com.wangdaye.common.utils.helper.RoutingHelper {

    public static void startMuzeiSettingsActivity(Activity a) {
        ARouter.getInstance()
                .build(MuzeiSettingsActivity.MUZEI_SETTINGS_ACTIVITY)
                // .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a);
    }

    public static void startMuzeiCollectionSourceConfigActivity(Activity a) {
        ARouter.getInstance()
                .build(MuzeiCollectionSourceConfigActivity.MUZEI_COLLECTION_SOURCE_CONFIG_ACTIVITY)
                // .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a);
    }
}
