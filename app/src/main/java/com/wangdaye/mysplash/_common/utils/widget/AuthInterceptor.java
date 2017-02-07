package com.wangdaye.mysplash._common.utils.widget;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Client interceptor.
 * */

public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
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
        return chain.proceed(request);
    }
}
