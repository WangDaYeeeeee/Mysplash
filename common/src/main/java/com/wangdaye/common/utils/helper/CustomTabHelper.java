package com.wangdaye.common.utils.helper;

import android.content.Context;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

import com.wangdaye.common.R;
import com.wangdaye.common.utils.manager.ThemeManager;

/**
 * Custom tab helper.
 *
 * A helper class that make the operation of {@link CustomTabsIntent} easier.
 *
 * */

public class CustomTabHelper {

    /**
     * Help user to visit web page by {@link CustomTabsIntent}.
     * */
    public static void startCustomTabActivity(Context context, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        builder.setToolbarColor(ThemeManager.getPrimaryColor(context));
        builder.setStartAnimations(context, R.anim.activity_slide_in, R.anim.activity_fade_out);
        builder.setExitAnimations(context, R.anim.activity_slide_in, R.anim.activity_fade_out);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }
}
