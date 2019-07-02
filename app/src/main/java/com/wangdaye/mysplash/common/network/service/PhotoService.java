package com.wangdaye.mysplash.common.network.service;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.SchedulerTransformer;
import com.wangdaye.mysplash.common.network.api.PhotoNodeApi;
import com.wangdaye.mysplash.common.network.api.PhotoApi;
import com.wangdaye.mysplash.common.network.interceptor.NapiInterceptor;
import com.wangdaye.mysplash.common.network.json.LikePhotoResult;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.interceptor.AuthInterceptor;
import com.wangdaye.mysplash.common.network.observer.BaseObserver;
import com.wangdaye.mysplash.common.network.observer.ObserverContainer;

import java.io.IOException;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Photo service.
 * */

public class PhotoService {

    private PhotoApi api;
    private PhotoNodeApi nodeApi;
    private CompositeDisposable compositeDisposable;

    public PhotoService(OkHttpClient client,
                        GsonConverterFactory gsonConverterFactory,
                        RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                        CompositeDisposable disposable) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_API_BASE_URL)
                .client(
                        client.newBuilder()
                                .addInterceptor(new AuthInterceptor())
                                // .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                                .build()
                ).addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .build()
                .create((PhotoApi.class));
        nodeApi = TextUtils.isEmpty(Mysplash.UNSPLASH_NODE_API_URL)
                ? null
                : new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(
                        client.newBuilder()
                                .addInterceptor(new AuthInterceptor())
                                .addInterceptor(new NapiInterceptor())
                                .build()
                ).addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .build()
                .create((PhotoNodeApi.class));
        compositeDisposable = disposable;
    }

    public void requestPhotos(@Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                              String order_by, BaseObserver<List<Photo>> observer) {
        api.getPhotos(page, per_page, order_by)
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void requestCuratePhotos(@Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                    String order_by, BaseObserver<List<Photo>> observer) {
        api.getCuratedPhotos(page, per_page, order_by)
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void likePhoto(String id, BaseObserver<LikePhotoResult> observer) {
        api.likeAPhoto(id)
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void cancelLikePhoto(String id, BaseObserver<LikePhotoResult> observer) {
        api.unlikeAPhoto(id)
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void requestAPhoto(String id, BaseObserver<Photo> observer) {
        if (nodeApi == null) {
            api.getAPhoto(id)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        } else {
            nodeApi.getAPhoto(id)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        }
    }

    public void requestUserPhotos(String username,
                                  @Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                  String order_by, BaseObserver<List<Photo>> observer) {
        api.getUserPhotos(username, page, per_page, order_by)
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void requestUserLikes(String username,
                                 @Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                 String order_by, final BaseObserver<List<Photo>> observer) {
        api.getUserLikes(username, page, per_page, order_by)
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void requestCollectionPhotos(int collectionId,
                                        @Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                        BaseObserver<List<Photo>> observer) {
        api.getCollectionPhotos(collectionId, page, per_page)
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void requestCuratedCollectionPhotos(int collectionId,
                                               @Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                               BaseObserver<List<Photo>> observer) {
        api.getCuratedCollectionPhotos(collectionId, page, per_page)
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void requestRandomPhotos(List<Integer> collectionIdList,
                                    Boolean featured, String username, String query, String orientation,
                                    BaseObserver<List<Photo>> observer) {
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

        api.getRandomPhotos(idString, featured, username, query, orientation, Mysplash.DEFAULT_PER_PAGE)
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    @WorkerThread
    @Nullable
    public List<Photo> requestRandomPhotos(@Nullable List<Integer> collectionIdList, Boolean featured,
                                           String username, String query, String orientation) {
        StringBuilder collections = new StringBuilder();
        if (collectionIdList != null && collectionIdList.size() > 0) {
            collections.append(collectionIdList.get(0));
        }
        for (int i = 1; collectionIdList != null && i < collectionIdList.size(); i ++) {
            collections.append(",").append(collectionIdList.get(i));
        }

        try {
            return api.callRandomPhotos(
                    collections.toString(), featured, username, query, orientation, Mysplash.DEFAULT_PER_PAGE
            ).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void downloadPhoto(String id) {
        api.downloadPhoto(id)
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, null));
    }

    public void cancel() {
        compositeDisposable.clear();
    }
}
