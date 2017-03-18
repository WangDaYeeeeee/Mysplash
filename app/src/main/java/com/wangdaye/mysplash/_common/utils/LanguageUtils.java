package com.wangdaye.mysplash._common.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.wangdaye.mysplash.Mysplash;

import java.util.Locale;

/**
 * Language utils.
 * */

public class LanguageUtils {

    public static void setLanguage(Context c) {
        String language = Mysplash.getInstance().getLanguage();
        if (!language.equals("follow_system")) {
            Resources resources = c.getResources();
            Configuration configuration = resources.getConfiguration();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            switch (language) {
                case "chinese":
                    configuration.setLocale(new Locale("zh"));
                    break;

                case "italian":
                    configuration.setLocale(new Locale("it"));
                    break;

                case "turkish":
                    configuration.setLocale(new Locale("tr"));
                    break;

                case "german":
                    configuration.setLocale(new Locale("de"));
                    break;

                case "russian":
                    configuration.setLocale(new Locale("ru"));
                    break;

                case "spanish":
                    configuration.setLocale(new Locale("es"));
                    break;

                default:
                    configuration.setLocale(new Locale("en"));
                    break;
            }
            resources.updateConfiguration(configuration, metrics);
        }
    }
}
