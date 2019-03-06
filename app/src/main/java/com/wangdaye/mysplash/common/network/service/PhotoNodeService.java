package com.wangdaye.mysplash.common.network.service;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.api.PhotoNodeApi;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.interceptor.AuthInterceptor;
import com.wangdaye.mysplash.common.network.interceptor.NapiInterceptor;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Photo node service.
 * */

public class PhotoNodeService {

    private PhotoNodeApi api;

    @Nullable private Call call;
    @Nullable private Callback callback;

    @Inject
    PhotoNodeService(OkHttpClient client, GsonConverterFactory factory) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(client.newBuilder()
                        .addInterceptor(new AuthInterceptor())
                        .addInterceptor(new NapiInterceptor())
                        .build())
                .addConverterFactory(factory)
                .build()
                .create((PhotoNodeApi.class));
        this.call = null;
        callback = null;
    }

    void requestAPhoto(String id, Callback<Photo> callback) {
        Call<Photo> getPhotoInfo = api.getAPhoto(id);
        getPhotoInfo.enqueue(callback);
        this.call = getPhotoInfo;
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
