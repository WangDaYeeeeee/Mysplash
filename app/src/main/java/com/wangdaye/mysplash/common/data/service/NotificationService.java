package com.wangdaye.mysplash.common.data.service;

import com.google.gson.GsonBuilder;
import com.wangdaye.mysplash.common.data.api.NotificationApi;
import com.wangdaye.mysplash.common.data.entity.unsplash.Notification;
import com.wangdaye.mysplash.common.utils.widget.interceptor.NotificationInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Notification service.
 * */

public class NotificationService {
    // widget
    private Call call;

    /** <br> data. */

    public void requestFollowingFeed(String enrich, final OnRequestNotificationListener l) {
        Call<Notification> getNotification = buildApi(buildClient()).getNotification(enrich);
        getNotification.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, retrofit2.Response<Notification> response) {
                if (l != null) {
                    l.onRequestNotificationSucceed(call, response);
                }
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                if (l != null) {
                    l.onRequestNotificationFailed(call, t);
                }
            }
        });
        call = getNotification;
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

    /** <br> build. */

    public static NotificationService getService() {
        return new NotificationService();
    }

    private OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new NotificationInterceptor())
                .build();
    }

    private NotificationApi buildApi(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl("https://unsplash.com/")
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder().setLenient().create()))
                .build()
                .create((NotificationApi.class));
    }
    
    /** <br> interface. */

    public interface OnRequestNotificationListener {
        void onRequestNotificationSucceed(Call<Notification> call, Response<Notification> response);
        void onRequestNotificationFailed(Call<Notification> call, Throwable t);
    }
}
