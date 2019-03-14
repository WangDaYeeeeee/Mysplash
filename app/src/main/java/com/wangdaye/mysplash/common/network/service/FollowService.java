package com.wangdaye.mysplash.common.network.service;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.di.annotation.ApplicationInstace;
import com.wangdaye.mysplash.common.network.NullResponseBody;
import com.wangdaye.mysplash.common.network.SchedulerTransformer;
import com.wangdaye.mysplash.common.network.api.FollowApi;
import com.wangdaye.mysplash.common.network.interceptor.AuthInterceptor;
import com.wangdaye.mysplash.common.network.interceptor.NapiInterceptor;
import com.wangdaye.mysplash.common.network.observer.NoBodyObserver;
import com.wangdaye.mysplash.common.network.observer.ObserverContainer;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Follow service.
 * */

public class FollowService {

    private FollowApi api;
    private CompositeDisposable compositeDisposable;

    @Inject
    public FollowService(@ApplicationInstace OkHttpClient client,
                         @ApplicationInstace GsonConverterFactory gsonConverterFactory,
                         @ApplicationInstace RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                         CompositeDisposable disposable) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(client.newBuilder()
                        .addInterceptor(new AuthInterceptor())
                        .addInterceptor(new NapiInterceptor())
                        .build())
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .build()
                .create((FollowApi.class));
        compositeDisposable = disposable;
    }

    public void followUser(String username, NoBodyObserver<ResponseBody> observer) {
        api.follow(username)
                .compose(SchedulerTransformer.create())
                .onExceptionResumeNext(Observable.create(emitter -> emitter.onNext(new NullResponseBody())))
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void cancelFollowUser(String username, NoBodyObserver<ResponseBody> observer) {
        api.cancelFollow(username)
                .compose(SchedulerTransformer.create())
                .onExceptionResumeNext(Observable.create(emitter -> emitter.onNext(new NullResponseBody())))
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void cancel() {
        compositeDisposable.clear();
    }
}
