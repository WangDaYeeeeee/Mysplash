package com.wangdaye.mysplash.common.network.service;

import android.text.TextUtils;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.api.SearchApi;
import com.wangdaye.mysplash.common.network.json.SearchCollectionsResult;
import com.wangdaye.mysplash.common.network.json.SearchPhotosResult;
import com.wangdaye.mysplash.common.network.json.SearchUsersResult;
import com.wangdaye.mysplash.common.network.interceptor.AuthInterceptor;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Search service.
 * */

public class SearchService {

    private SearchApi api;

    @Nullable private Call call;
    @Nullable private Callback callback;

    private SearchNodeService nodeService;

    @Inject
    public SearchService(OkHttpClient client, GsonConverterFactory factory) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_API_BASE_URL)
                .client(client.newBuilder()
                        .addInterceptor(new AuthInterceptor())
                        .build())
                .addConverterFactory(factory)
                .build()
                .create((SearchApi.class));
        call = null;
        callback = null;
        nodeService = TextUtils.isEmpty(Mysplash.UNSPLASH_NODE_API_URL)
                ? null : new SearchNodeService(client, factory);
    }

    public void searchPhotos(String query, int page, Callback<SearchPhotosResult> callback) {
        Call<SearchPhotosResult> searchPhotos = api.searchPhotos(query, page, Mysplash.DEFAULT_PER_PAGE);
        searchPhotos.enqueue(callback);
        this.call = searchPhotos;
        this.callback = callback;
    }

    public void searchUsers(String query, int page, Callback<SearchUsersResult> callback) {
        if (nodeService == null) {
            Call<SearchUsersResult> searchUsers = api.searchUsers(query, page, Mysplash.DEFAULT_PER_PAGE);
            searchUsers.enqueue(callback);
            this.call = searchUsers;
            this.callback = callback;
        } else {
            nodeService.searchUsers(query, page, callback);
        }
    }

    public void searchCollections(String query, int page, Callback<SearchCollectionsResult> callback) {
        if (nodeService == null) {
            Call<SearchCollectionsResult> searchCollections = api.searchCollections(query, page, Mysplash.DEFAULT_PER_PAGE);
            searchCollections.enqueue(callback);
            this.call = searchCollections;
            this.callback = callback;
        } else {
            nodeService.searchCollections(query, page, callback);
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
