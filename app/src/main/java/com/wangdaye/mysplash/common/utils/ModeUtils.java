package com.wangdaye.mysplash.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.wangdaye.mysplash.R;

/**
 * Mode utils.
 * */

public class ModeUtils {
    // data.
    private boolean lightTheme;
    private boolean normalMode;

    /** <br> life cycle. */

    private ModeUtils(Context c) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);

        this.lightTheme = sharedPreferences.getBoolean(c.getString(R.string.key_light_theme), true);
        this.normalMode = sharedPreferences.getBoolean(c.getString(R.string.key_normal_mode), true);
    }

    /** <br> data. */

    public boolean isLightTheme() {
        return lightTheme;
    }

    public boolean isNormalMode() {
        return normalMode;
    }

    public boolean isNeedSetStatusBarTextDark() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && isLightTheme();
    }

    public boolean isNeedSetStatusBarMask() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                && isLightTheme();
    }

    public void refresh(Context c) {
        instance = new ModeUtils(c);
    }

    /** <br> singleton. */

    private static ModeUtils instance;

    public static ModeUtils getInstance(Context c) {
        if (instance == null) {
            instance = new ModeUtils(c);
        }
        return instance;
    }
}
