package com.wangdaye.mysplash.common.network.interceptor;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Auth interceptor.
 *
 * A interceptor for {@link retrofit2.Retrofit}, it can add authorization information into the
 * HTTP request header.
 *
 * */

public class AuthInterceptor extends ReportExceptionInterceptor {

    @Override
    public Response intercept(Chain chain) {
        Request request;
        if (AuthManager.getInstance().isAuthorized()) {
            request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer " + AuthManager.getInstance().getAccessToken())
                    .build();
        } else {
            request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Client-ID " + Mysplash.getAppId(Mysplash.getInstance(), false))
                    .build();
        }
        try {
            return chain.proceed(request);
        } catch (Exception e) {
            handleException(e);
            return new Response.Builder().build();
        }
    }
}
