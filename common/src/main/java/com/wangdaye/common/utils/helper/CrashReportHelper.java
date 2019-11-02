package com.wangdaye.common.utils.helper;

import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Crash report helper.
 * */
public class CrashReportHelper {

    public static void init(Context context) {
        CrashReport.initCrashReport(context.getApplicationContext(), "c8ad99bd5d", false);
    }

    public static void report(Throwable t) {
        CrashReport.postCatchedException(t);
    }
}
