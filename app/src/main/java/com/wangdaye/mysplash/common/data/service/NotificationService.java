package com.wangdaye.mysplash.common.data.service;

import com.google.gson.GsonBuilder;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.api.NotificationApi;
import com.wangdaye.mysplash.common.data.entity.unsplash.NotificationFeed;
import com.wangdaye.mysplash.common.utils.widget.interceptor.NotificationInterceptor;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * NotificationFeed service.
 * */

public class NotificationService {

    private Call call;

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
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder().setLenient().create()))
                .build()
                .create((NotificationApi.class));
    }

    public void requestNotificationFeed(String enrich, final OnRequestNotificationListener l) {
        Call<NotificationFeed> getNotification = buildApi(buildClient())
                .getNotification(
                        RequestBody.create(
                                MediaType.parse("text/plain"),
                                enrich));
        getNotification.enqueue(new Callback<NotificationFeed>() {
            @Override
            public void onResponse(Call<NotificationFeed> call, retrofit2.Response<NotificationFeed> response) {
                if (l != null) {
                    l.onRequestNotificationSucceed(call, response);
                }
            }

            @Override
            public void onFailure(Call<NotificationFeed> call, Throwable t) {
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

    // interface.

    public interface OnRequestNotificationListener {
        void onRequestNotificationSucceed(Call<NotificationFeed> call, Response<NotificationFeed> response);
        void onRequestNotificationFailed(Call<NotificationFeed> call, Throwable t);
    }
}
