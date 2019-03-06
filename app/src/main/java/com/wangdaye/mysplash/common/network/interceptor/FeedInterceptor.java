package com.wangdaye.mysplash.common.network.interceptor;

import com.wangdaye.mysplash.BuildConfig;
import com.wangdaye.mysplash.common.network.service.FeedService;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Feed interceptor.
 *
 * A interceptor for {@link retrofit2.Retrofit}, it can add authorization information into
 * HTTP request header for {@link FeedService}.
 *
 * */

public class FeedInterceptor implements Interceptor {

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer " + BuildConfig.FEED_TOKEN)
                .build();
        return chain.proceed(request);
    }
}
