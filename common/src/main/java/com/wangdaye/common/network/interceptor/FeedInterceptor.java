package com.wangdaye.common.network.interceptor;

import androidx.annotation.NonNull;

import com.wangdaye.common.BuildConfig;
import com.wangdaye.common.network.service.FeedService;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Feed interceptor.
 *
 * A interceptor for {@link retrofit2.Retrofit}, it can add authorization information into
 * HTTP request header for {@link FeedService}.
 *
 * */

public class FeedInterceptor extends ReportExceptionInterceptor {

    @NonNull
    @Override
    public Response intercept(Chain chain) {
        Request request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer " + BuildConfig.FEED_TOKEN)
                .build();
        try {
            return chain.proceed(request);
        } catch (Exception e) {
            handleException(e);
            return nullResponse(request);
        }
    }
}
