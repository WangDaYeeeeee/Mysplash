package com.wangdaye.mysplash.common.network.interceptor;

import com.wangdaye.mysplash.common.network.service.NotificationService;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;

import okhttp3.Interceptor;
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

    @Override
    public Response intercept(Interceptor.Chain chain) {
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
