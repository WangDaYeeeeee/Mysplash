package com.wangdaye.mysplash.data.unslpash.tools;

import com.wangdaye.mysplash.data.constant.Mysplash;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Client interceptor.
 * */

public class ClientInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader("Authorization", "Client-ID " + Mysplash.APPLICATION_ID)
                .build();
        return chain.proceed(request);
    }
}
