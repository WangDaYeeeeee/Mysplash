package com.wangdaye.mysplash;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.wangdaye.mysplash.utils.LanguageUtils;

/**
 * My application.
 * */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        loadLanguage();
    }

    private void loadLanguage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString(
                getString(R.string.key_language),
                "follow_system");
        LanguageUtils.setLanguage(this, language);
    }
}
