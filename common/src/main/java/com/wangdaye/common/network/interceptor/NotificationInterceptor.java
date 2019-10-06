package com.wangdaye.common.network.interceptor;

import androidx.annotation.NonNull;

import com.wangdaye.common.network.service.NotificationService;
import com.wangdaye.common.utils.manager.AuthManager;

import okhttp3.Request;
import okhttp3.Response;

/**
 * NotificationFeed interceptor.
 *
 * A interceptor for {@link retrofit2.Retrofit}, it is used to get notification data by a HTTP
 * request from {@link NotificationService}.
 *
 * */

public class NotificationInterceptor extends ReportExceptionInterceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) {
        Request request;
        if (AuthManager.getInstance().isAuthorized()) {
            request = chain.request()
                    .newBuilder()
                    .addHeader("Origin", "https://unsplash.com")
                    .addHeader("x-unsplash-client", "web")
                    .addHeader("accept-version", "v1")
                    .addHeader("authorization", "Bearer " + AuthManager.getInstance().getAccessToken())
                    .addHeader("Accept", "*/*")
                    .addHeader("Referer", "https://unsplash.com/")
                    .build();
        } else {
            request = chain.request()
                    .newBuilder()
                    .build();
        }

        try {
            return chain.proceed(request);
        } catch (Exception e) {
            handleException(e);
            return nullResponse(request);
        }
    }
}
