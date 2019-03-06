package com.wangdaye.mysplash.common.network.service;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.api.UserNodeApi;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.network.interceptor.AuthInterceptor;
import com.wangdaye.mysplash.common.network.interceptor.NapiInterceptor;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * User node service.
 * */

public class UserNodeService {

    private UserNodeApi api;

    @Nullable private Call call;
    @Nullable private Callback callback;

    @Inject
    UserNodeService(OkHttpClient client, GsonConverterFactory factory) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(client.newBuilder()
                        .addInterceptor(new AuthInterceptor())
                        .addInterceptor(new NapiInterceptor())
                        .build())
                .addConverterFactory(factory)
                .build()
                .create((UserNodeApi.class));
        call = null;
        callback = null;
    }

    void requestUserProfile(String username, Callback<User> callback) {
        Call<User> getUserProfile = api.getUserProfile(username, 256, 256);
        getUserProfile.enqueue(callback);
        this.call = getUserProfile;
        this.callback = callback;
    }

    void requestFollowers(String username, int page, int perPage, Callback<List<User>> callback) {
        Call<List<User>> requestFollowers = api.getFollowers(username, page, perPage);
        requestFollowers.enqueue(callback);
        this.call = requestFollowers;
        this.callback = callback;
    }

    void requestFollowing(String username, int page, int perPage, Callback<List<User>> callback) {
        Call<List<User>> requestFollowing = api.getFolloweing(username, page, perPage);
        requestFollowing.enqueue(callback);
        this.call = requestFollowing;
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
