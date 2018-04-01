package com.wangdaye.mysplash.common.utils.helper;

import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

/**
 * Custom tab helper.
 *
 * A helper class that make the operation of {@link CustomTabsIntent} easier.
 *
 * */

class CustomTabHelper {

    /**
     * Help user to visit web page by {@link CustomTabsIntent}.
     * */
    static void startCustomTabActivity(Context context, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        builder.setToolbarColor(ThemeManager.getPrimaryColor(context));
        builder.setStartAnimations(context, R.anim.activity_slide_in, R.anim.activity_fade_out);
        builder.setExitAnimations(context, R.anim.activity_slide_in, R.anim.activity_fade_out);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }
}
