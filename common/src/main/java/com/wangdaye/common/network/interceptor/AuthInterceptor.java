package com.wangdaye.common.network.interceptor;

import android.content.Context;

import androidx.annotation.NonNull;

import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.common.utils.manager.CustomApiManager;

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

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) {
        Request request;
        if (AuthManager.getInstance().isAuthorized()) {
            request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer " + AuthManager.getInstance().getAccessToken())
                    .build();
        } else {
            Context context = MysplashApplication.getInstance();
            String appId = CustomApiManager.getInstance(context).getAppId(context, false);
            request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Client-ID " + appId)
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
