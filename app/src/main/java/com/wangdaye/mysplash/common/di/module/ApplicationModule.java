package com.wangdaye.mysplash.common.di.module;

import com.wangdaye.mysplash.common.network.TLSCompactHelper;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApplicationModule {

    @Provides
    public OkHttpClient getOkHttpClient() {
        return TLSCompactHelper.getOKHttpClient();
    }

    @Provides
    public GsonConverterFactory getGsonConverterFactory() {
        return GsonConverterFactory.create();
    }

    @Provides
    public RxJava2CallAdapterFactory getRxJava2CallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }

    @Provides
    public CompositeDisposable getCompositeDisposable() {
        return new CompositeDisposable();
    }
}
