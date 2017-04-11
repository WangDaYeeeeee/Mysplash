package com.wangdaye.mysplash.common.utils.widget.interceptor;

import com.wangdaye.mysplash.BuildConfig;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Get stream interceptor.
 *
 * A interceptor for {@link retrofit2.Retrofit}, it can
 * get notification feed code by {@link com.wangdaye.mysplash.common.data.service.GetStreamService}.
 *
 * */

public class GetStreamInterceptor implements Interceptor {

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request;
        if (AuthManager.getInstance().isAuthorized()) {
            request = chain.request()
                    .newBuilder()
                    .addHeader("accept", "application/json")
                    .addHeader("stream-auth-type", "jwt")
                    .addHeader("Origin", "https://unsplash.com")
                    .addHeader("X-Stream-Client", "stream-javascript-client-browser-unknown")
                    .addHeader("Authorization", BuildConfig.GET_STREAM_AUTH_CODE)
                    .addHeader("Referer", "https://unsplash.com/")
                    .build();
        } else {
            request = chain.request()
                    .newBuilder()
                    .build();
        }

        return chain.proceed(request);
    }
}