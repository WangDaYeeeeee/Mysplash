package com.wangdaye.mysplash;

import android.app.Application;

import com.liulishuo.filedownloader.FileDownloader;

/**
 * My application.
 * */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileDownloader.init(getApplicationContext());
    }
}
