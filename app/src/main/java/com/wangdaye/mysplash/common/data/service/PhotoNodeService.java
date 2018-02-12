package com.wangdaye.mysplash.common.data.service;

import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common._basic.TLSCompactService;
import com.wangdaye.mysplash.common.data.api.PhotoNodeApi;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.utils.widget.interceptor.AuthInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Photo node service.
 * */

public class PhotoNodeService extends TLSCompactService {

    private Call call;

    static PhotoNodeService getService() {
        return TextUtils.isEmpty(Mysplash.UNSPLASH_NODE_API_URL) ? null : new PhotoNodeService();
    }

    private OkHttpClient buildClient() {
        return getClientBuilder()
                .addInterceptor(new AuthInterceptor())
                .build();
    }

    private PhotoNodeApi buildApi(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder()
                                        .setDateFormat(Mysplash.DATE_FORMAT)
                                        .create()))
                .build()
                .create((PhotoNodeApi.class));
    }

    void requestAPhoto(String id, final PhotoService.OnRequestSinglePhotoListener l) {
        Call<Photo> getPhotoInfo = buildApi(buildClient()).getAPhoto(id);
        getPhotoInfo.enqueue(new Callback<Photo>() {
            @Override
            public void onResponse(Call<Photo> call, Response<Photo> response) {
                if (l != null) {
                    l.onRequestSinglePhotoSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<Photo> call, Throwable t) {
                if (l != null) {
                    l.onRequestSinglePhotoFailed(call, t);
                }
            }
        });
        call = getPhotoInfo;
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }
}
