package com.wangdaye.mysplash.common.utils.widget.interceptor;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Following interceptor.
 *
 * A interceptor for {@link retrofit2.Retrofit}, it can add authorization information into
 * HTTP request header for {@link com.wangdaye.mysplash.common.data.service.FollowingService}.
 *
 * */

public class FollowingInterceptor implements Interceptor {

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request;
        if (AuthManager.getInstance().isAuthorized()) {
            request = chain.request()
                    .newBuilder()
                    .addHeader("x-unsplash-client", "web")
                    .addHeader("accept-version", "v1")
                    .addHeader("Authorization", "Bearer " + AuthManager.getInstance().getAccessToken())
                    .addHeader("Accept", "*/*")
                    .addHeader("Referer", "https://unsplash.com/following?onboarding=true")
                    .build();
        } else {
            request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Client-ID " + Mysplash.getAppId(Mysplash.getInstance(), false))
                    .build();
        }

        return chain.proceed(request);
    }
}
