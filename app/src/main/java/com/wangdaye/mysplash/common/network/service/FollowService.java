package com.wangdaye.mysplash.common.network.service;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.api.FollowApi;
import com.wangdaye.mysplash.common.network.callback.NoBodyCallback;
import com.wangdaye.mysplash.common.network.interceptor.AuthInterceptor;
import com.wangdaye.mysplash.common.network.interceptor.NapiInterceptor;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Follow service.
 * */

public class FollowService {

    private FollowApi api;

    @Nullable private Call call;
    @Nullable private NoBodyCallback callback;

    @Inject
    public FollowService(OkHttpClient client, GsonConverterFactory factory) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(client.newBuilder()
                        .addInterceptor(new AuthInterceptor())
                        .addInterceptor(new NapiInterceptor())
                        .build())
                .addConverterFactory(factory)
                .build()
                .create((FollowApi.class));
        call = null;
        callback = null;
    }

    public void followUser(String username, NoBodyCallback<ResponseBody> callback) {
        Call<ResponseBody> followRequest = api.follow(username);
        followRequest.enqueue(callback);
        this.call = followRequest;
        this.callback = callback;
    }

    public void cancelFollowUser(String username, NoBodyCallback<ResponseBody> callback) {
        Call<ResponseBody> followRequest = api.cancelFollow(username);
        followRequest.enqueue(callback);
        this.call = followRequest;
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
