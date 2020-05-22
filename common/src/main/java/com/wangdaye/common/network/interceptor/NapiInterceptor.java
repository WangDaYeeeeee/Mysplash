package com.wangdaye.common.network.interceptor;

import androidx.annotation.NonNull;

import org.brotli.dec.BrotliInputStream;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;

/**
 * Napi interceptor.
 *
 * A interceptor for {@link retrofit2.Retrofit}, it can add header information into
 * HTTP request for Napi network service.
 *
 * */

public class NapiInterceptor extends ReportExceptionInterceptor {

    private static final Charset UTF8 = StandardCharsets.UTF_8;

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) {
        Request request = chain.request()
                .newBuilder()
                .addHeader("authority", "unsplash.com")
                .addHeader("method", "GET")
                .addHeader("scheme", "https")
                .addHeader("accept", "*/*")
                .addHeader("accept-encoding", "gzip, deflate, br")
                .addHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8")
                .addHeader("cache-control", "max-age=0")
                .addHeader("upgrade-insecure-requests", "1")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                .build();
        try {
            Response response = chain.proceed(request);
            response = decodeGZip(request, response);
            response = decodeBr(request, response);
            return response;
        } catch (Exception e) {
            handleException(e);
            return nullResponse(request);
        }
    }

    private Response decodeGZip(Request request, Response response) throws Exception {
        return response;
    }

    private Response decodeBr(Request request, Response response) throws Exception {
        return response;
    }
}
