package com.wangdaye.mysplash.common.utils.widget.interceptor;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
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

public class NapiInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder().build();
        return buildResponse(request, chain.proceed(request));
    }

    private Response buildResponse(Request request, Response response) throws IOException {
        return response;
    }
}
