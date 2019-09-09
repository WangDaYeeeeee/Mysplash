package com.wangdaye.common.network;

import com.wangdaye.common.di.component.DaggerNetworkComponent;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ComponentCollection {

    private static ComponentCollection instance;

    public static ComponentCollection getInstance() {
        if (instance == null) {
            synchronized (ComponentCollection.class) {
                if (instance == null) {
                    instance = new ComponentCollection();
                }
            }
        }
        return instance;
    }

    @Inject OkHttpClient httpClient;
    @Inject GsonConverterFactory gsonConverterFactory;
    @Inject RxJava2CallAdapterFactory rxJava2CallAdapterFactory;

    private ComponentCollection() {
        DaggerNetworkComponent.create().inject(this);
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public GsonConverterFactory getGsonConverterFactory() {
        return gsonConverterFactory;
    }

    public RxJava2CallAdapterFactory getRxJava2CallAdapterFactory() {
        return rxJava2CallAdapterFactory;
    }
}
