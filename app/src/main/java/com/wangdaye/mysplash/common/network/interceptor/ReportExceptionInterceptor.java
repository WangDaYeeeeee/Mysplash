package com.wangdaye.mysplash.common.network.interceptor;

import com.tencent.bugly.crashreport.CrashReport;

import okhttp3.Interceptor;

abstract class ReportExceptionInterceptor implements Interceptor {

    void handleException(Exception e) {
        e.printStackTrace();
        CrashReport.postCatchedException(e);
    }
}
