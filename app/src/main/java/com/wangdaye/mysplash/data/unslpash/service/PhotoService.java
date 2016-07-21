package com.wangdaye.mysplash.data.unslpash.service;

import com.google.gson.GsonBuilder;
import com.wangdaye.mysplash.data.constant.Mysplash;
import com.wangdaye.mysplash.data.unslpash.api.PhotoApi;
import com.wangdaye.mysplash.data.unslpash.model.Photo;
import com.wangdaye.mysplash.data.unslpash.model.PhotoStats;
import com.wangdaye.mysplash.data.unslpash.tools.AuthInterceptor;
import com.wangdaye.mysplash.data.unslpash.tools.ClientInterceptor;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Photo service.
 * */

public class PhotoService {
    // widget
    private OkHttpClient client;
    private Call call;

    /** <br> data. */

    public void requestPhotos(final int page, int per_page, String order_by,
                                     final OnRequestPhotosListener l) {
        PhotoApi api = new Retrofit.Builder()
                .baseUrl(Mysplash.BASE_URL)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder()
                                        .setDateFormat(Mysplash.DATE_FORMAT)
                                        .create()))
                .build()
                .create((PhotoApi.class));

        Call<List<Photo>> getPhotos = api.getPhotos(page, per_page, order_by);
        getPhotos.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, retrofit2.Response<List<Photo>> response) {
                if (l != null) {
                    l.onRequestPhotosSuccess(call, response, page);
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                if (l != null) {
                    l.onRequestPhotosFailed(call, t, page);
                }
            }
        });
        call = getPhotos;
    }

    public void requestCuratePhotos(final int page, int per_page, String order_by,
                                           final OnRequestPhotosListener l) {
        PhotoApi api = new Retrofit.Builder()
                .baseUrl(Mysplash.BASE_URL)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder()
                                        .setDateFormat(Mysplash.DATE_FORMAT)
                                        .create()))
                .build()
                .create((PhotoApi.class));

        Call<List<Photo>> getCuratePhotos = api.getCuratedPhotos(page, per_page, order_by);
        getCuratePhotos.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, retrofit2.Response<List<Photo>> response) {
                if (l != null) {
                    l.onRequestPhotosSuccess(call, response, page);
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                if (l != null) {
                    l.onRequestPhotosFailed(call, t, page);
                }
            }
        });
        call = getCuratePhotos;
    }

    public void searchPhotos(String query, String orientation, final int page, int per_page,
                                   final OnRequestPhotosListener l) {
        PhotoApi api = new Retrofit.Builder()
                .baseUrl(Mysplash.BASE_URL)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder()
                                        .setDateFormat(Mysplash.DATE_FORMAT)
                                        .create()))
                .build()
                .create((PhotoApi.class));

        Call<List<Photo>> searchPhotos = api.searchPhotos(query, orientation, page, per_page);
        searchPhotos.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, retrofit2.Response<List<Photo>> response) {
                if (l != null) {
                    l.onRequestPhotosSuccess(call, response, page);
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                if (l != null) {
                    l.onRequestPhotosFailed(call, t, page);
                }
            }
        });
        call = searchPhotos;
    }

    public void requestStats(String id, final OnRequestStatsListener l) {
        PhotoApi api = new Retrofit.Builder()
                .baseUrl(Mysplash.BASE_URL)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder()
                                        .setDateFormat(Mysplash.DATE_FORMAT)
                                        .create()))
                .build()
                .create((PhotoApi.class));

        Call<PhotoStats> getStats = api.getPhotoStats(id);
        getStats.enqueue(new Callback<PhotoStats>() {
            @Override
            public void onResponse(Call<PhotoStats> call, retrofit2.Response<PhotoStats> response) {
                if (l != null) {
                    l.onRequestStatsSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<PhotoStats> call, Throwable t) {
                if (l != null) {
                    l.onRequestStatsFailed(call, t);
                }
            }
        });
        call = getStats;
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

    /** <br> build. */

    public static PhotoService getService() {
        return new PhotoService();
    }

    public PhotoService buildClient(String token) {
        this.client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(token))
                .build();
        return this;
    }

    public PhotoService buildClient() {
        this.client = new OkHttpClient.Builder()
                .addInterceptor(new ClientInterceptor())
                .build();
        return this;
    }

    /** <br> interface. */

    public interface OnRequestPhotosListener {
        void onRequestPhotosSuccess(Call<List<Photo>> call, retrofit2.Response<List<Photo>> response, int page);
        void onRequestPhotosFailed(Call<List<Photo>> call, Throwable t, int page);
    }

    public interface OnRequestStatsListener {
        void onRequestStatsSuccess(Call<PhotoStats> call, retrofit2.Response<PhotoStats> response);
        void onRequestStatsFailed(Call<PhotoStats> call, Throwable t);
    }
}
