package com.wangdaye.mysplash.common.network.service;

import android.text.TextUtils;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.api.CollectionApi;
import com.wangdaye.mysplash.common.network.json.ChangeCollectionPhotoResult;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.interceptor.AuthInterceptor;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Collection service.
 * */

public class CollectionService {

    private CollectionApi api;

    @Nullable private Call call;
    @Nullable private Callback callback;

    private CollectionNodeService nodeService;

    @Inject
    public CollectionService(OkHttpClient client, GsonConverterFactory factory) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_API_BASE_URL)
                .client(client.newBuilder()
                        .addInterceptor(new AuthInterceptor())
                        .build())
                .addConverterFactory(factory)
                .build()
                .create((CollectionApi.class));
        call = null;
        callback = null;
        nodeService = TextUtils.isEmpty(Mysplash.UNSPLASH_NODE_API_URL)
                ? null : new CollectionNodeService(client, factory);
    }

    public void requestAllCollections(@Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                      Callback<List<Collection>> callback) {
        if (nodeService == null) {
            Call<List<Collection>> getAllCollections = api.getAllCollections(page, per_page);
            getAllCollections.enqueue(callback);
            this.call = getAllCollections;
            this.callback = callback;
        } else {
            nodeService.requestAllCollections(page, per_page, callback);
        }
    }

    public void requestCuratedCollections(@Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                          Callback<List<Collection>> callback) {
        if (nodeService == null) {
            Call<List<Collection>> getCuratedCollections = api.getCuratedCollections(page, per_page);
            getCuratedCollections.enqueue(callback);
            this.call = getCuratedCollections;
            this.callback = callback;
        } else {
            nodeService.requestCuratedCollections(page, per_page, callback);
        }
    }

    public void requestFeaturedCollections(@Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                           Callback<List<Collection>> callback) {
        if (nodeService == null) {
            Call<List<Collection>> getFeaturedCollections = api.getFeaturedCollections(page, per_page);
            getFeaturedCollections.enqueue(callback);
            this.call = getFeaturedCollections;
            this.callback = callback;
        } else {
            nodeService.requestFeaturedCollections(page, per_page, callback);
        }
    }

    public void requestACollections(String id, Callback<Collection> callback) {
        if (nodeService == null) {
            Call<Collection> getACollection = api.getACollection(id);
            getACollection.enqueue(callback);
            this.call = getACollection;
            this.callback = callback;
        } else {
            nodeService.requestACollections(id, callback);
        }
    }

    @Nullable
    public Collection requestACollections(String id) {
        Call<Collection> getACollection = api.getACollection(id);
        try {
            Response<Collection> response = getACollection.execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void requestACuratedCollections(String id, Callback<Collection> callback) {
        if (nodeService == null) {
            Call<Collection> getACuratedCollection = api.getACuratedCollection(id);
            getACuratedCollection.enqueue(callback);
            this.call = getACuratedCollection;
            this.callback = callback;
        } else {
            nodeService.requestACuratedCollections(id, callback);
        }
    }

    @Nullable
    public Collection requestACuratedCollections(String id) {
        Call<Collection> getACuratedCollection = api.getACuratedCollection(id);
        try {
            Response<Collection> response = getACuratedCollection.execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void requestUserCollections(String username,
                                       @Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                       Callback<List<Collection>> callback) {
        if (nodeService == null) {
            Call<List<Collection>> getUserCollections = api.getUserCollections(username, page, per_page);
            getUserCollections.enqueue(callback);
            this.call = getUserCollections;
            this.callback = callback;
        } else {
            nodeService.requestUserCollections(username, page, per_page, callback);
        }
    }

    public void createCollection(String title, @Nullable String description, boolean privateX,
                                 Callback<Collection> callback) {
        Call<Collection> createCollection;
        if (description == null) {
            createCollection = api.createCollection(title, privateX);
        } else {
            createCollection = api.createCollection(title, description, privateX);
        }
        createCollection.enqueue(callback);
        this.call = createCollection;
        this.callback = callback;
    }

    public void addPhotoToCollection(@IntRange(from = 0) int collectionId, String photoId,
                                     Callback<ChangeCollectionPhotoResult> callback) {
        Call<ChangeCollectionPhotoResult> addPhotoToCollection = api.addPhotoToCollection(collectionId, photoId);
        addPhotoToCollection.enqueue(callback);
        this.call = addPhotoToCollection;
        this.callback = callback;
    }

    public void deletePhotoFromCollection(@IntRange(from = 0) int collectionId, String photoId,
                                          Callback<ChangeCollectionPhotoResult> callback) {
        Call<ChangeCollectionPhotoResult> deletePhotoFromCollection = api.deletePhotoFromCollection(collectionId, photoId);
        deletePhotoFromCollection.enqueue(callback);
        this.call = deletePhotoFromCollection;
        this.callback = callback;
    }

    public void updateCollection(@IntRange(from = 0) int collectionId,
                                 String title, String description, boolean privateX,
                                 Callback<Collection> callback) {
        Call<Collection> updateCollection = api.updateCollection(collectionId, title, description, privateX);
        updateCollection.enqueue(callback);
        this.call = updateCollection;
        this.callback = callback;
    }

    public void deleteCollection(@IntRange(from = 0) int id, Callback<ResponseBody> callback) {
        Call<ResponseBody> deleteCollection = api.deleteCollection(id);
        deleteCollection.enqueue(callback);
        this.call = deleteCollection;
        this.callback = callback;
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
