package com.wangdaye.mysplash.common.network.service;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.api.FeedApi;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.interceptor.FeedInterceptor;
import com.wangdaye.mysplash.common.network.interceptor.NapiInterceptor;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Feed service.
 * */

public class FeedService {

    private FeedApi api;

    @Nullable private Call call;
    @Nullable private Callback callback;

    @Inject
    public FeedService(OkHttpClient client, GsonConverterFactory factory) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(client.newBuilder()
                        .addInterceptor(new FeedInterceptor())
                        .addInterceptor(new NapiInterceptor())
                        .build())
                .addConverterFactory(factory)
                .build()
                .create((FeedApi.class));
        call = null;
        callback = null;
    }

    public void requestFollowingFeed(@Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                     Callback<List<Photo>> callback) {
        Call<List<Photo>> getPhotos = api.getFollowingFeed(page, per_page);
        getPhotos.enqueue(callback);
        this.call = getPhotos;
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
