package com.wangdaye.mysplash.common.network.service;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.di.annotation.ApplicationInstace;
import com.wangdaye.mysplash.common.network.SchedulerTransformer;
import com.wangdaye.mysplash.common.network.api.NotificationApi;
import com.wangdaye.mysplash.common.network.json.NotificationFeed;
import com.wangdaye.mysplash.common.network.interceptor.NotificationInterceptor;
import com.wangdaye.mysplash.common.network.observer.BaseObserver;
import com.wangdaye.mysplash.common.network.observer.ObserverContainer;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Notification service.
 * */

public class NotificationService {

    private NotificationApi api;
    private CompositeDisposable compositeDisposable;

    @Inject
    public NotificationService(@ApplicationInstace OkHttpClient client,
                               @ApplicationInstace GsonConverterFactory gsonConverterFactory,
                               @ApplicationInstace RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                               CompositeDisposable disposable) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(client.newBuilder()
                        .addInterceptor(new NotificationInterceptor())
                        .build())
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .build()
                .create((NotificationApi.class));
        compositeDisposable = disposable;
    }

    public void requestNotificationFeed(String enrich, BaseObserver<NotificationFeed> observer) {
        api.getNotification(RequestBody.create(MediaType.parse("text/plain"), enrich))
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void cancel() {
        compositeDisposable.clear();
    }
}
