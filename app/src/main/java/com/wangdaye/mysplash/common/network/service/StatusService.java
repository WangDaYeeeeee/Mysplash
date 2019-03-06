package com.wangdaye.mysplash.common.network.service;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.api.StatusApi;
import com.wangdaye.mysplash.common.network.json.Total;
import com.wangdaye.mysplash.common.network.interceptor.AuthInterceptor;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Status service.
 * */

public class StatusService {

    private StatusApi api;

    @Nullable private Call call;
    @Nullable private Callback callback;

    @Inject
    public StatusService(OkHttpClient client, GsonConverterFactory factory) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_API_BASE_URL)
                .client(client.newBuilder()
                        .addInterceptor(new AuthInterceptor())
                        .build())
                .addConverterFactory(factory)
                .build()
                .create((StatusApi.class));
        call = null;
        callback = null;
    }

    public void requestTotal(Callback<Total> callback) {
        Call<Total> getTotal = api.getTotal();
        getTotal.enqueue(callback);
        this.call = getTotal;
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
