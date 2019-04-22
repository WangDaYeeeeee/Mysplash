package com.wangdaye.mysplash.common.network.service;

import android.text.TextUtils;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.SchedulerTransformer;
import com.wangdaye.mysplash.common.network.api.UserNodeApi;
import com.wangdaye.mysplash.common.network.api.UserApi;
import com.wangdaye.mysplash.common.network.interceptor.NapiInterceptor;
import com.wangdaye.mysplash.common.network.json.Me;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.network.interceptor.AuthInterceptor;
import com.wangdaye.mysplash.common.network.observer.BaseObserver;
import com.wangdaye.mysplash.common.network.observer.ObserverContainer;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * User service.
 * */

public class UserService {

    private UserApi api;
    private UserNodeApi nodeApi;
    private CompositeDisposable compositeDisposable;

    public UserService(OkHttpClient client,
                       GsonConverterFactory gsonConverterFactory,
                       RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                       CompositeDisposable disposable) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_API_BASE_URL)
                .client(
                        client.newBuilder()
                                .addInterceptor(new AuthInterceptor())
                                .build()
                ).addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .build()
                .create((UserApi.class));
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
                .create((UserNodeApi.class));
        compositeDisposable = disposable;
    }

    public void requestUserProfile(String username, BaseObserver<User> observer) {
        if (nodeApi == null) {
            api.getUserProfile(username, 256, 256)
                    .compose(SchedulerTransformer.create())
                    .subscribe(observer);
        } else {
            nodeApi.getUserProfile(username, 256, 256)
                    .compose(SchedulerTransformer.create())
                    .subscribe(observer);
        }
    }

    public void updateMeProfile(String username, String first_name, String last_name,
                                String email, String url, String location, String bio,
                                BaseObserver<Me> observer) {
        api.updateMeProfile(
                username, first_name, last_name,
                email, url, location, bio)
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void requestAuthUser(@Nullable Me me, @NonNull RequestAuthUserObserver callback) {
        Observable<Me> meObservable = me == null
                ? api.getMeProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(callback::onRequestMeCompleted)
                .doOnError(throwable -> callback.onError())
                : Observable.create((ObservableOnSubscribe<Me>) emitter -> {
                    emitter.onNext(me);
                    emitter.onComplete();
                });
        meObservable.observeOn(Schedulers.io())
                .flatMap((Function<Me, ObservableSource<User>>) me1 ->
                        nodeApi == null
                                ? api.getUserProfile(me1.username, 256, 256)
                                : nodeApi.getUserProfile(me1.username, 256, 256)
                ).observeOn(AndroidSchedulers.mainThread())
                .doOnNext(callback::onRequestUserCompleted)
                .doOnError(throwable -> callback.onError())
                .doOnComplete(callback::onComplete)
                .subscribe(new ObserverContainer<>(compositeDisposable, null));
    }

    public void requestFollowers(String username, int page, int perPage, BaseObserver<List<User>> observer) {
        if (nodeApi == null) {
            api.getFollowers(username, page, perPage)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        } else {
            nodeApi.getFollowers(username, page, perPage)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        }
    }

    public void requestFollowing(String username, int page, int perPage, BaseObserver<List<User>> observer) {
        if (nodeApi == null) {
            api.getFolloweing(username, page, perPage)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        } else {
            nodeApi.getFolloweing(username, page, perPage)
                    .compose(SchedulerTransformer.create())
                    .subscribe(new ObserverContainer<>(compositeDisposable, observer));
        }
    }

    public void cancel() {
        compositeDisposable.clear();
    }

    public interface RequestAuthUserObserver {
        void onRequestMeCompleted(@NonNull Me me);
        void onRequestUserCompleted(@NonNull User user);
        void onComplete();
        void onError();
    }
}
