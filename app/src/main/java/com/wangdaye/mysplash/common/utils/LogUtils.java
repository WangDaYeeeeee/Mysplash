package com.wangdaye.mysplash.common.utils;

import android.util.Log;

public class LogUtils {

    private static final String DEFAULT_TAG = "testing";
    private static final boolean DEBUG = true;

    public static void log(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void log(String msg) {
        log(DEFAULT_TAG, msg);
    }
}
