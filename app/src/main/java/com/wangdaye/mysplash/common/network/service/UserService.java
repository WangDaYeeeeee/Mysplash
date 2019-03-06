package com.wangdaye.mysplash.common.network.service;

import android.text.TextUtils;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.api.UserApi;
import com.wangdaye.mysplash.common.network.json.Me;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.network.interceptor.AuthInterceptor;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * User service.
 * */

public class UserService {

    private UserApi api;

    @Nullable private Call call;
    @Nullable private Callback callback;

    private UserNodeService nodeService;

    @Inject
    public UserService(OkHttpClient client, GsonConverterFactory factory) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_API_BASE_URL)
                .client(client.newBuilder()
                        .addInterceptor(new AuthInterceptor())
                        .build())
                .addConverterFactory(factory)
                .build()
                .create((UserApi.class));
        call = null;
        callback = null;
        nodeService = TextUtils.isEmpty(Mysplash.UNSPLASH_NODE_API_URL)
                ? null : new UserNodeService(client, factory);
    }

    public void requestUserProfile(String username, Callback<User> callback) {
        if (nodeService == null) {
            Call<User> getUserProfile = api.getUserProfile(username, 256, 256);
            getUserProfile.enqueue(callback);
            this.call = getUserProfile;
            this.callback = callback;
        } else {
            nodeService.requestUserProfile(username, callback);
        }
    }

    public void requestMeProfile(Callback<Me> callback) {
        Call<Me> getMeProfile = api.getMeProfile();
        getMeProfile.enqueue(callback);
        this.call = getMeProfile;
        this.callback = callback;
    }

    public void updateMeProfile(String username, String first_name, String last_name,
                                String email, String url, String location, String bio,
                                Callback<Me> callback) {
        Call<Me> updateMeProfile = api.updateMeProfile(
                username, first_name, last_name,
                email, url, location, bio);
        updateMeProfile.enqueue(callback);
        this.call = updateMeProfile;
        this.callback = callback;
    }

    public void requestFollowers(String username, int page, int perPage, Callback<List<User>> callback) {
        if (nodeService == null) {
            Call<List<User>> requestFollowers = api.getFollowers(username, page, perPage);
            requestFollowers.enqueue(callback);
            this.call = requestFollowers;
            this.callback = callback;
        } else {
            nodeService.requestFollowers(username, page, perPage, callback);
        }
    }

    public void requestFollowing(String username, int page, int perPage, Callback<List<User>> callback) {
        if (nodeService == null) {
            Call<List<User>> requestFollowing = api.getFolloweing(username, page, perPage);
            requestFollowing.enqueue(callback);
            this.call = requestFollowing;
            this.callback = callback;
        } else {
            nodeService.requestFollowing(username, page, perPage, callback);
        }
    }

    public void cancel() {
        if (nodeService != null) {
            nodeService.cancel();
        }
        if (callback != null) {
            callback.cancel();
        }
        if (call != null) {
            call.cancel();
        }
    }
}
