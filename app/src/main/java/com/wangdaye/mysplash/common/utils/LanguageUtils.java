package com.wangdaye.mysplash.common.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.wangdaye.mysplash.common.utils.manager.SettingsOptionManager;

import java.util.Locale;

/**
 * Language utils.
 *
 * An utils class that makes operations of language easier.
 *
 * */

public class LanguageUtils {

    public static void setLanguage(Context c) {
        String language = SettingsOptionManager.getInstance(c).getLanguage();
        if (!language.equals("follow_system")) {
            Resources resources = c.getResources();
            Configuration configuration = resources.getConfiguration();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            configuration.setLocale(getLocale(c, language));
            resources.updateConfiguration(configuration, metrics);
        }
    }

    public static Locale getLocale(Context c) {
        return getLocale(c, SettingsOptionManager.getInstance(c).getLanguage());
    }

    private static Locale getLocale(Context c, String language) {
        switch (language) {
            case "follow_system":
                return Locale.getDefault();

            case "english_usa":
                return new Locale("en", "US");

            case "english_uk":
                return new Locale("en", "GB");

            case "english_au":
                return new Locale("en", "AU");

            case "chinese":
                return new Locale("zh");

            case "italian":
                return new Locale("it");

            case "turkish":
                return new Locale("tr");

            case "german":
                return new Locale("de");

            case "russian":
                return new Locale("ru");

            case "spanish":
                return new Locale("es");

            case "japanese":
                return new Locale("ja");

            case "french":
                return new Locale("fr");

            case "portuguese_brazil":
                return new Locale("pt", "BR");

            default:
                return new Locale("en");
        }
    }
}
