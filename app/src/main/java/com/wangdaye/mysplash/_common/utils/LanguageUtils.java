package com.wangdaye.mysplash._common.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;

import java.util.Locale;

/**
 * Language utils.
 * */

public class LanguageUtils {

    public static void setLanguage(Context c) {
        String language = Mysplash.getInstance().getLanguage();
        if (language.equals(c.getResources().getStringArray(R.array.language_values)[0])) {
            return;
        }
        Resources resources = c.getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        switch (language) {
            case "english":
                configuration.setLocale(Locale.US);
                break;

            case "chinese":
                configuration.setLocale(Locale.SIMPLIFIED_CHINESE);
                break;

            case "italian":
                configuration.setLocale(Locale.ITALIAN);
                break;

            default:
                configuration.setLocale(Locale.US);
                break;
        }
        resources.updateConfiguration(configuration, metrics);
    }
}
