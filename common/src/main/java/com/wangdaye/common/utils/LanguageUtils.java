package com.wangdaye.common.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.wangdaye.component.ComponentFactory;
import com.wangdaye.component.service.SettingsService;

import java.util.Locale;

/**
 * Language utils.
 *
 * An utils class that makes operations of language easier.
 *
 * */

public class LanguageUtils {

    public static void setLanguage(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        configuration.setLocale(getLocale(ComponentFactory.getSettingsService().getLanguage()));
        resources.updateConfiguration(configuration, metrics);
    }

    public static Locale getLocale() {
        return getLocale(ComponentFactory.getSettingsService().getLanguage());
    }

    private static Locale getLocale(@SettingsService.LanguageRule String language) {
        switch (language) {
            case SettingsService.LANGUAGE_SYSTEM:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return Resources.getSystem().getConfiguration().getLocales().get(0);
                } else {
                    return Resources.getSystem().getConfiguration().locale;
                }

            case SettingsService.LANGUAGE_ENGLISH_US:
                return new Locale("en", "US");

            case SettingsService.LANGUAGE_ENGLISH_UK:
                return new Locale("en", "GB");

            case SettingsService.LANGUAGE_ENGLISH_AU:
                return new Locale("en", "AU");

            case SettingsService.LANGUAGE_CHINESE:
                return new Locale("zh");

            case SettingsService.LANGUAGE_ITALIAN:
                return new Locale("it");

            case SettingsService.LANGUAGE_TURKISH:
                return new Locale("tr");

            case SettingsService.LANGUAGE_GERMAN:
                return new Locale("de");

            case SettingsService.LANGUAGE_RUSSIAN:
                return new Locale("ru");

            case SettingsService.LANGUAGE_SPANISH:
                return new Locale("es");

            case SettingsService.LANGUAGE_JAPANESE:
                return new Locale("ja");

            case SettingsService.LANGUAGE_FRENCH:
                return new Locale("fr");

            case SettingsService.LANGUAGE_PORTUGUESE_BR:
                return new Locale("pt", "BR");

            default:
                return new Locale("en");
        }
    }
}
