package com.wangdaye.mysplash._common.utils.helper;

import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;

/**
 * Custom tab helper.
 * */

class CustomTabHelper {

    static void startCustomTabActivity(Context context, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        if (Mysplash.getInstance().isLightTheme()) {
            builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary_light));
        } else {
            builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary_dark));
        }
        builder.setStartAnimations(context, R.anim.activity_in, R.anim.activity_slide_out_bottom);
        builder.setExitAnimations(context, R.anim.activity_in, R.anim.activity_slide_out_bottom);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }
}
