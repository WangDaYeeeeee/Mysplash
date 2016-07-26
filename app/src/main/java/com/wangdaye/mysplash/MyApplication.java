package com.wangdaye.mysplash;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aspsine.multithreaddownload.DownloadConfiguration;
import com.aspsine.multithreaddownload.DownloadManager;
import com.wangdaye.mysplash.utils.LanguageUtils;

/**
 * My application.
 * */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DownloadConfiguration configuration = new DownloadConfiguration();
        configuration.setMaxThreadNum(10);
        configuration.setThreadNum(3);
        DownloadManager.getInstance().init(getApplicationContext(), configuration);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString(
                getString(R.string.key_language),
                "follow_system");
        LanguageUtils.setLanguage(this, language);
    }
}
