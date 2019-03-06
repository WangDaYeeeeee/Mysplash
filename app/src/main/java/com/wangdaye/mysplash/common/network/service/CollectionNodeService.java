package com.wangdaye.mysplash.common.network.service;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.api.CollectionNodeApi;
import com.wangdaye.mysplash.common.network.json.Collection;
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
 * Collection node service.
 * */

public class CollectionNodeService {

    private CollectionNodeApi api;

    @Nullable private Call call;
    @Nullable private Callback callback;

    @Inject
    CollectionNodeService(OkHttpClient client, GsonConverterFactory factory) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(client.newBuilder()
                        .addInterceptor(new AuthInterceptor())
                        .addInterceptor(new NapiInterceptor())
                        .build())
                .addConverterFactory(factory)
                .build()
                .create((CollectionNodeApi.class));
        call = null;
        callback = null;
    }

    void requestAllCollections(@Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                               Callback<List<Collection>> callback) {
        Call<List<Collection>> getAllCollections = api.getAllCollections(page, per_page);
        getAllCollections.enqueue(callback);
        this.call = getAllCollections;
        this.callback = callback;
    }

    void requestCuratedCollections(@Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                   Callback<List<Collection>> callback) {
        Call<List<Collection>> getCuratedCollections = api.getCuratedCollections(page, per_page);
        getCuratedCollections.enqueue(callback);
        this.call = getCuratedCollections;
        this.callback = callback;
    }

    void requestFeaturedCollections(@Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                    Callback<List<Collection>> callback) {
        Call<List<Collection>> getFeaturedCollections = api.getFeaturedCollections(page, per_page);
        getFeaturedCollections.enqueue(callback);
        this.call = getFeaturedCollections;
        this.callback = callback;
    }

    void requestACollections(String id, Callback<Collection> callback) {
        Call<Collection> getACollection = api.getACollection(id);
        getACollection.enqueue(callback);
        this.call = getACollection;
        this.callback = callback;
    }

    void requestACuratedCollections(String id, Callback<Collection> callback) {
        Call<Collection> getACuratedCollection = api.getACuratedCollection(id);
        getACuratedCollection.enqueue(callback);
        this.call = getACuratedCollection;
        this.callback = callback;
    }

    void requestUserCollections(String username,
                                @Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                Callback<List<Collection>> callback) {
        Call<List<Collection>> getUserCollections = api.getUserCollections(username, page, per_page);
        getUserCollections.enqueue(callback);
        this.call = getUserCollections;
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
