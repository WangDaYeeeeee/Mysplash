package com.wangdaye.common.di.module;

import com.wangdaye.common.di.annotation.ApplicationInstance;
import com.wangdaye.common.network.ComponentCollection;
import com.wangdaye.common.network.TLSCompactHelper;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module()
public class NetworkModule {

    @Provides
    public OkHttpClient getOkHttpClient() {
        return TLSCompactHelper.getOKHttpClient();
    }

    @ApplicationInstance
    @Provides
    public OkHttpClient getApplicationOkHttpClient() {
        return ComponentCollection.getInstance().getHttpClient();
    }

    @Provides
    public GsonConverterFactory getGsonConverterFactory() {
        return GsonConverterFactory.create();
    }

    @ApplicationInstance
    @Provides
    public GsonConverterFactory getApplicationGsonConverterFactory() {
        return ComponentCollection.getInstance().getGsonConverterFactory();
    }

    @Provides
    public RxJava2CallAdapterFactory getRxJava2CallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }

    @ApplicationInstance
    @Provides
    public RxJava2CallAdapterFactory getApplicationRxJava2CallAdapterFactory() {
        return ComponentCollection.getInstance().getRxJava2CallAdapterFactory();
    }
}
