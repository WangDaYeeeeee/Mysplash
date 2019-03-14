package com.wangdaye.mysplash.common.network.service;

import android.text.TextUtils;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.di.annotation.ApplicationInstace;
import com.wangdaye.mysplash.common.network.NullResponseBody;
import com.wangdaye.mysplash.common.network.SchedulerTransformer;
import com.wangdaye.mysplash.common.network.api.CollectionNodeApi;
import com.wangdaye.mysplash.common.network.api.CollectionApi;
import com.wangdaye.mysplash.common.network.interceptor.NapiInterceptor;
import com.wangdaye.mysplash.common.network.json.ChangeCollectionPhotoResult;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.interceptor.AuthInterceptor;
import com.wangdaye.mysplash.common.network.observer.BaseObserver;
import com.wangdaye.mysplash.common.network.observer.NoBodyObserver;
import com.wangdaye.mysplash.common.network.observer.ObserverContainer;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Collection service.
 * */

public class CollectionService {

    private CollectionApi api;
    private CollectionNodeApi nodeApi;
    private CompositeDisposable compositeDisposable;

    @Inject
    public CollectionService(@ApplicationInstace OkHttpClient client,
                             @ApplicationInstace GsonConverterFactory gsonConverterFactory,
                             @ApplicationInstace RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                             CompositeDisposable disposable) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_API_BASE_URL)
                .client(client.newBuilder()
                        .addInterceptor(new AuthInterceptor())
                        .build())
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .build()
                .create((CollectionApi.class));
        nodeApi = TextUtils.isEmpty(Mysplash.UNSPLASH_NODE_API_URL)
                ? null
                : new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(client.newBuilder()
                        .addInterceptor(new AuthInterceptor())
                        .addInterceptor(new NapiInterceptor())
                        .build())
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .build()
                .create((CollectionNodeApi.class));
        compositeDisposable = disposable;
    }

    public void requestAllCollections(@Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                      BaseObserver<List<Collection>> observer) {
        if (nodeApi == null) {
            api.getAllCollections(page, per_page)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        } else {
            nodeApi.getAllCollections(page, per_page)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        }
    }

    public void requestCuratedCollections(@Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                          BaseObserver<List<Collection>> observer) {
        if (nodeApi == null) {
            api.getCuratedCollections(page, per_page)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        } else {
            nodeApi.getCuratedCollections(page, per_page)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        }
    }

    public void requestFeaturedCollections(@Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                           BaseObserver<List<Collection>> observer) {
        if (nodeApi == null) {
            api.getFeaturedCollections(page, per_page)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        } else {
            nodeApi.getFeaturedCollections(page, per_page)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        }
    }

    public void requestACollections(String id, BaseObserver<Collection> observer) {
        if (nodeApi == null) {
            api.getACollection(id)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        } else {
            nodeApi.getACollection(id)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        }
    }

    public void requestACuratedCollections(String id, BaseObserver<Collection> observer) {
        if (nodeApi == null) {
            api.getACuratedCollection(id)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        } else {
            nodeApi.getACuratedCollection(id)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        }
    }

    public void requestUserCollections(String username,
                                       @Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                       BaseObserver<List<Collection>> observer) {
        if (nodeApi == null) {
            api.getUserCollections(username, page, per_page)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        } else {
            nodeApi.getUserCollections(username, page, per_page)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        }
    }

    public void createCollection(String title, @Nullable String description, boolean privateX,
                                 BaseObserver<Collection> observer) {
        Observable<Collection> observable = description == null
                ? api.createCollection(title, privateX)
                : api.createCollection(title, description, privateX);
        observable.compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void addPhotoToCollection(@IntRange(from = 0) int collectionId, String photoId,
                                     BaseObserver<ChangeCollectionPhotoResult> observer) {
        api.addPhotoToCollection(collectionId, photoId)
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void deletePhotoFromCollection(@IntRange(from = 0) int collectionId, String photoId,
                                          BaseObserver<ChangeCollectionPhotoResult> observer) {
        api.deletePhotoFromCollection(collectionId, photoId)
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void updateCollection(@IntRange(from = 0) int collectionId,
                                 String title, String description, boolean privateX,
                                 BaseObserver<Collection> observer) {
        api.updateCollection(collectionId, title, description, privateX)
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void deleteCollection(@IntRange(from = 0) int id, NoBodyObserver<ResponseBody> observer) {
        api.deleteCollection(id)
                .compose(SchedulerTransformer.create())
                .onExceptionResumeNext(Observable.create(emitter -> emitter.onNext(new NullResponseBody())))
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void cancel() {
        compositeDisposable.clear();
    }
}
