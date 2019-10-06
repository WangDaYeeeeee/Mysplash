package com.wangdaye.common.network.interceptor;

import com.wangdaye.common.utils.helper.CrashReportHelper;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

abstract class ReportExceptionInterceptor implements Interceptor {

    void handleException(Exception e) {
        e.printStackTrace();
        CrashReportHelper.report(e);
    }

    Response nullResponse(Request request) {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_2)
                .code(400)
                .message("Handle an error in Mysplash client.")
                .body(null)
                .build();
    }
}
