package com.wangdaye.mysplash.common.network.service;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.api.SearchNodeApi;
import com.wangdaye.mysplash.common.network.json.SearchCollectionsResult;
import com.wangdaye.mysplash.common.network.json.SearchUsersResult;
import com.wangdaye.mysplash.common.network.interceptor.AuthInterceptor;
import com.wangdaye.mysplash.common.network.interceptor.NapiInterceptor;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Search node service.
 * */

public class SearchNodeService {

    private SearchNodeApi api;

    @Nullable private Call call;
    @Nullable private Callback callback;

    @Inject
    SearchNodeService(OkHttpClient client, GsonConverterFactory factory) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(client.newBuilder()
                        .addInterceptor(new AuthInterceptor())
                        .addInterceptor(new NapiInterceptor())
                        .build())
                .addConverterFactory(factory)
                .build()
                .create((SearchNodeApi.class));
        call = null;
        callback = null;
    }


    void searchUsers(String query, int page, Callback<SearchUsersResult> callback) {
        Call<SearchUsersResult> searchUsers = api.searchUsers(query, page, Mysplash.DEFAULT_PER_PAGE);
        searchUsers.enqueue(callback);
        this.call = searchUsers;
        this.callback = callback;
    }

    void searchCollections(String query, int page, Callback<SearchCollectionsResult> callback) {
        Call<SearchCollectionsResult> searchCollections = api.searchCollections(query, page, Mysplash.DEFAULT_PER_PAGE);
        searchCollections.enqueue(callback);
        this.call = searchCollections;
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
