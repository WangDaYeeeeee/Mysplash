package com.wangdaye.mysplash.common.di.module;

import com.wangdaye.mysplash.common.network.TLSCompactHelper;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
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
}
