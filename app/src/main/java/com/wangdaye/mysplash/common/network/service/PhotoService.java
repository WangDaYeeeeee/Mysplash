package com.wangdaye.mysplash.common.network.service;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.api.PhotoApi;
import com.wangdaye.mysplash.common.network.json.LikePhotoResult;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.interceptor.AuthInterceptor;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Photo service.
 * */

public class PhotoService {

    private PhotoApi api;

    @Nullable private Call call;
    @Nullable private Callback callback;

    private PhotoNodeService nodeService;

    @Inject
    public PhotoService(OkHttpClient client, GsonConverterFactory factory) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_API_BASE_URL)
                .client(client.newBuilder()
                        .addInterceptor(new AuthInterceptor())
                        .build())
                .addConverterFactory(factory)
                .build()
                .create((PhotoApi.class));
        call = null;
        callback = null;
        nodeService = TextUtils.isEmpty(Mysplash.UNSPLASH_NODE_API_URL)
                ? null : new PhotoNodeService(client, factory);
    }

    public void requestPhotos(@Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                              String order_by, Callback<List<Photo>> callback) {
        Call<List<Photo>> getPhotos = api.getPhotos(page, per_page, order_by);
        getPhotos.enqueue(callback);
        this.call = getPhotos;
        this.callback = callback;
    }

    public void requestCuratePhotos(@Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                    String order_by, Callback<List<Photo>> callback) {
        Call<List<Photo>> getCuratePhotos = api.getCuratedPhotos(page, per_page, order_by);
        getCuratePhotos.enqueue(callback);
        this.call = getCuratePhotos;
        this.callback = callback;
    }

    public void likePhoto(String id, Callback<LikePhotoResult> callback) {
        Call<LikePhotoResult> likePhoto = api.likeAPhoto(id);
        likePhoto.enqueue(callback);
        this.call = likePhoto;
        this.callback = callback;
    }

    public void cancelLikePhoto(String id, Callback<LikePhotoResult> callback) {
        Call<LikePhotoResult> cancelLikePhoto = api.unlikeAPhoto(id);
        cancelLikePhoto.enqueue(callback);
        this.call = cancelLikePhoto;
        this.callback = callback;
    }

    public void requestAPhoto(String id, Callback<Photo> callback) {
        if (nodeService == null) {
            Call<Photo> getAPhoto = api.getAPhoto(id);
            getAPhoto.enqueue(callback);
            this.call = getAPhoto;
            this.callback = callback;
        } else {
            nodeService.requestAPhoto(id, callback);
        }
    }

    public void requestUserPhotos(String username,
                                  @Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                  String order_by, Callback<List<Photo>> callback) {
        Call<List<Photo>> getUserPhotos = api.getUserPhotos(username, page, per_page, order_by);
        getUserPhotos.enqueue(callback);
        this.call = getUserPhotos;
        this.callback = callback;
    }

    public void requestUserLikes(String username,
                                 @Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                 String order_by, final Callback<List<Photo>> callback) {
        Call<List<Photo>> getUserLikes = api.getUserLikes(username, page, per_page, order_by);
        getUserLikes.enqueue(callback);
        this.call = getUserLikes;
        this.callback = callback;
    }

    public void requestCollectionPhotos(int collectionId,
                                        @Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                        Callback<List<Photo>> callback) {
        Call<List<Photo>> getCollectionPhotos = api.getCollectionPhotos(collectionId, page, per_page);
        getCollectionPhotos.enqueue(callback);
        this.call = getCollectionPhotos;
        this.callback = callback;
    }

    public void requestCuratedCollectionPhotos(int collectionId,
                                               @Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                               Callback<List<Photo>> callback) {
        Call<List<Photo>> getCuratedCollectionPhotos = api.getCuratedCollectionPhotos(collectionId, page, per_page);
        getCuratedCollectionPhotos.enqueue(callback);
        this.call = getCuratedCollectionPhotos;
        this.callback = callback;
    }

    public void requestRandomPhotos(List<Integer> collectionIdList,
                                    Boolean featured, String username, String query, String orientation,
                                    Callback<List<Photo>> callback) {
        StringBuilder idBuilder = new StringBuilder();
        if (collectionIdList != null && collectionIdList.size() > 0) {
            idBuilder.append(collectionIdList.get(0));
        }
        for (int i = 1; collectionIdList != null && i < collectionIdList.size(); i ++) {
            idBuilder.append(",").append(collectionIdList.get(i));
        }

        String idString = idBuilder.toString();
        if (TextUtils.isEmpty(idString)) {
            idString = null;
        }

        if (TextUtils.isEmpty(username)) {
            username = null;
        }

        if (TextUtils.isEmpty(query)) {
            query = null;
        }

        if (TextUtils.isEmpty(orientation)) {
            orientation = null;
        }

        Call<List<Photo>> getRandomPhotos = api.getRandomPhotos(
                idString, featured, username, query, orientation, Mysplash.DEFAULT_PER_PAGE);
        getRandomPhotos.enqueue(callback);
        this.call = getRandomPhotos;
        this.callback = callback;
    }

    @WorkerThread
    @Nullable
    public List<Photo> requestRandomPhotos(@Nullable List<Integer> collectionIdList,
                                           Boolean featured,
                                           String username, String query, String orientation) {
        StringBuilder collections = new StringBuilder();
        if (collectionIdList != null && collectionIdList.size() > 0) {
            collections.append(collectionIdList.get(0));
        }
        for (int i = 1; collectionIdList != null && i < collectionIdList.size(); i ++) {
            collections.append(",").append(collectionIdList.get(i));
        }

        Call<List<Photo>> getRandomPhotos = api.getRandomPhotos(
                collections.toString(), featured, username, query, orientation, Mysplash.DEFAULT_PER_PAGE);
        try {
            Response<List<Photo>> response = getRandomPhotos.execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void downloadPhoto(String url) {
        api.downloadPhoto(url).enqueue(null);
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
