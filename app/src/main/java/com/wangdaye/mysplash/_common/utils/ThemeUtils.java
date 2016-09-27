package com.wangdaye.mysplash._common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.wangdaye.mysplash.R;

/**
 * Mode utils.
 * */

public class ThemeUtils {
    // data.
    private boolean lightTheme;

    /** <br> life cycle. */

    private ThemeUtils(Context c) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        this.lightTheme = sharedPreferences.getBoolean(c.getString(R.string.key_light_theme), true);
    }

    /** <br> data. */

    public boolean isLightTheme() {
        return lightTheme;
    }

    boolean isNeedSetStatusBarTextDark() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && isLightTheme();
    }

    public boolean isNeedSetStatusBarMask() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                && isLightTheme();
    }

    public void refresh(Context c) {
        instance = new ThemeUtils(c);
    }

    /** <br> singleton. */

    private static ThemeUtils instance;

    public static ThemeUtils getInstance(Context c) {
        if (instance == null) {
            instance = new ThemeUtils(c);
        }
        return instance;
    }
}
