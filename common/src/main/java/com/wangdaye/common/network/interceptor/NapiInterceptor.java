package com.wangdaye.common.network.interceptor;

import androidx.annotation.NonNull;

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

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) {
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

    private Response buildResponse(Request request, Response response) throws Exception {
        // return response;

        ResponseBody body = response.body();
        if (body == null) {
            return response;
        }

        BufferedSource source = body.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();

        if ("gzip".equalsIgnoreCase(response.headers().get("Content-Encoding"))) {
            try (GzipSource gzippedResponseBody = new GzipSource(buffer.clone())) {
                buffer = new Buffer();
                buffer.writeAll(gzippedResponseBody);
            }
        }

        Charset charset = UTF8;
        MediaType contentType = body.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }

        String bodyString = "";
        if (charset != null) {
            bodyString = buffer.clone().readString(charset);
        }

        return new Response.Builder()
                .addHeader("Content-Type", "application/json")
                .code(response.code())
                .body(ResponseBody.create(body.contentType(), bodyString))
                .message(response.message())
                .request(request)
                .protocol(Protocol.HTTP_2)
                .build();
    }
}
