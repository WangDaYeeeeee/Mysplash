package com.wangdaye.common.network.service;

import android.content.Context;

import com.wangdaye.base.unsplash.AccessToken;
import com.wangdaye.common.network.SchedulerTransformer;
import com.wangdaye.common.network.UrlCollection;
import com.wangdaye.common.network.api.AuthorizeApi;
import com.wangdaye.common.network.observer.BaseObserver;
import com.wangdaye.common.network.observer.ObserverContainer;
import com.wangdaye.common.utils.manager.CustomApiManager;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Authorize service.
 * */

public class AuthorizeService {

    private AuthorizeApi api;
    private CompositeDisposable compositeDisposable;

    public AuthorizeService(OkHttpClient client,
                            GsonConverterFactory gsonConverterFactory,
                            RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                            CompositeDisposable disposable) {
        api = new Retrofit.Builder()
                .baseUrl(UrlCollection.UNSPLASH_URL)
                .client(client.newBuilder().build())
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .build()
                .create(AuthorizeApi.class);
        compositeDisposable = disposable;
    }

    public void requestAccessToken(Context c, String code, BaseObserver<AccessToken> observer) {
        api.getAccessToken(
                CustomApiManager.getInstance(c).getAppId(c, true),
                CustomApiManager.getInstance(c).getSecret(c),
                "mysplash://" + UrlCollection.UNSPLASH_LOGIN_CALLBACK,
                code,
                "authorization_code"
        ).compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void cancel() {
        compositeDisposable.clear();
    }
}
