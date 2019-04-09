package com.wangdaye.mysplash.common.network.interceptor;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

/**
 * Napi interceptor.
 *
 * A interceptor for {@link retrofit2.Retrofit}, it can add header information into
 * HTTP request for Napi network service.
 *
 * */

public class NapiInterceptor extends ReportExceptionInterceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) {
        Request request = chain.request()
                .newBuilder()
                .addHeader("authority", "unsplash.com")
                .addHeader("method", "GET")
                .addHeader("scheme", "https")
                .addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .addHeader("accept-encoding", "gzip, deflate, br")
                .addHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8")
                .addHeader("cache-control", "max-age=0")
                .addHeader("upgrade-insecure-requests", "1")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                .build();
        try {
            return buildResponse(request, chain.proceed(request));
        } catch (Exception e) {
            handleException(e);
            return nullResponse(request);
        }
    }

    private Response buildResponse(Request request, Response response) throws IOException {
        return response;
    }
}
