package com.wangdaye.mysplash._common.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Language utils.
 * */

public class LanguageUtils {

    public static void setLanguage(Context c, String key) {
        if (key.equals("follow_system")) {
            return;
        }
        Resources resources = c.getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        switch (key) {
            case "english":
                configuration.setLocale(Locale.US);
                break;

            case "chinese":
                configuration.setLocale(Locale.SIMPLIFIED_CHINESE);
                break;

            default:
                configuration.setLocale(Locale.US);
                break;
        }
        resources.updateConfiguration(configuration, metrics);
    }
}
