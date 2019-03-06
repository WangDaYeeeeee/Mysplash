package com.wangdaye.mysplash.common.network.service;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.api.NotificationApi;
import com.wangdaye.mysplash.common.network.json.NotificationFeed;
import com.wangdaye.mysplash.common.network.interceptor.NotificationInterceptor;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Notification service.
 * */

public class NotificationService {

    private NotificationApi api;

    @Nullable private Call call;
    @Nullable private Callback callback;

    @Inject
    public NotificationService(OkHttpClient client, GsonConverterFactory factory) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(client.newBuilder()
                        .addInterceptor(new NotificationInterceptor())
                        .build())
                .addConverterFactory(factory)
                .build()
                .create((NotificationApi.class));
        call = null;
        callback = null;
    }

    public void requestNotificationFeed(String enrich, Callback<NotificationFeed> callback) {
        Call<NotificationFeed> getNotification = api.getNotification(
                RequestBody.create(
                        MediaType.parse("text/plain"),
                        enrich));
        getNotification.enqueue(callback);
        this.call = getNotification;
        this.callback = callback;
    }

    public void cancel() {
        if (callback != null) {
            callback.cancel();
        }
        if (call != null) {
            call.cancel();
        }
    }
}
