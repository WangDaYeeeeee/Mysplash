package com.wangdaye.mysplash.common.network.service;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.SchedulerTransformer;
import com.wangdaye.mysplash.common.network.api.FeedApi;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.interceptor.FeedInterceptor;
import com.wangdaye.mysplash.common.network.interceptor.NapiInterceptor;
import com.wangdaye.mysplash.common.network.observer.BaseObserver;
import com.wangdaye.mysplash.common.network.observer.ObserverContainer;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Feed service.
 * */

public class FeedService {

    private FeedApi api;
    private CompositeDisposable compositeDisposable;

    @Inject
    public FeedService(OkHttpClient client,
                       GsonConverterFactory gsonConverterFactory,
                       RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                       CompositeDisposable disposable) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(client.newBuilder()
                        .addInterceptor(new FeedInterceptor())
                        .addInterceptor(new NapiInterceptor())
                        .build())
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .build()
                .create((FeedApi.class));
        compositeDisposable = disposable;
    }

    public void requestFollowingFeed(@Mysplash.PageRule int page, @Mysplash.PerPageRule int per_page,
                                     BaseObserver<List<Photo>> observer) {
        api.getFollowingFeed(page, per_page)
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void cancel() {
        compositeDisposable.clear();
    }
}
