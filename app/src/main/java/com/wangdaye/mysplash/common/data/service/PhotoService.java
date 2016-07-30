package com.wangdaye.mysplash.common.data.service;

import com.google.gson.GsonBuilder;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.api.PhotoApi;
import com.wangdaye.mysplash.common.data.model.LikePhotoResult;
import com.wangdaye.mysplash.common.data.model.Photo;
import com.wangdaye.mysplash.common.data.model.PhotoStats;
import com.wangdaye.mysplash.common.data.tools.AuthInterceptor;
import com.wangdaye.mysplash.common.data.tools.ClientInterceptor;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
                              final boolean refresh, final OnRequestPhotosListener l) {
        Call<List<Photo>> getPhotos = buildApi(client).getPhotos(page, per_page, order_by);
        getPhotos.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, retrofit2.Response<List<Photo>> response) {
                if (l != null) {
                    l.onRequestPhotosSuccess(call, response, page, refresh);
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                if (l != null) {
                    l.onRequestPhotosFailed(call, t, refresh);
                }
            }
        });
        call = getPhotos;
    }

    public void requestCuratePhotos(final int page, int per_page, String order_by,
                                    final boolean refresh, final OnRequestPhotosListener l) {
        Call<List<Photo>> getCuratePhotos = buildApi(client).getCuratedPhotos(page, per_page, order_by);
        getCuratePhotos.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, retrofit2.Response<List<Photo>> response) {
                if (l != null) {
                    l.onRequestPhotosSuccess(call, response, page, refresh);
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                if (l != null) {
                    l.onRequestPhotosFailed(call, t, refresh);
                }
            }
        });
        call = getCuratePhotos;
    }

    public void searchPhotos(String query, String orientation, final int page, int per_page,
                             final boolean refresh, final OnRequestPhotosListener l) {
        Call<List<Photo>> searchPhotos = buildApi(client).searchPhotos(query, orientation, page, per_page);
        searchPhotos.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, retrofit2.Response<List<Photo>> response) {
                if (l != null) {
                    l.onRequestPhotosSuccess(call, response, page, refresh);
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                if (l != null) {
                    l.onRequestPhotosFailed(call, t, refresh);
                }
            }
        });
        call = searchPhotos;
    }

    public void requestStats(String id, final OnRequestStatsListener l) {
        Call<PhotoStats> getStats = buildApi(client).getPhotoStats(id);
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

    public void requestPhotosInAGivenCategory(int id, final int page, int per_page,
                                              final boolean refresh, final OnRequestPhotosListener l) {
        Call<List<Photo>> getPhotosInAGivenCategory = buildApi(client).getPhotosInAGivenCategory(id, page, per_page);
        getPhotosInAGivenCategory.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, retrofit2.Response<List<Photo>> response) {
                if (l != null) {
                    l.onRequestPhotosSuccess(call, response, page, refresh);
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                if (l != null) {
                    l.onRequestPhotosFailed(call, t, refresh);
                }
            }
        });
        call = getPhotosInAGivenCategory;
    }

    public void setLikeForAPhoto(String id, boolean like, final int position, final OnSetLikeListener l) {
        Call<LikePhotoResult> setLikeForAPhoto = like ?
                buildApi(client).likeAPhoto(id) : buildApi(client).unlikeAPhoto(id);
        setLikeForAPhoto.enqueue(new Callback<LikePhotoResult>() {
            @Override
            public void onResponse(Call<LikePhotoResult> call, Response<LikePhotoResult> response) {
                if (l != null) {
                    l.onSetLikeSuccess(call, response, position);
                }
            }

            @Override
            public void onFailure(Call<LikePhotoResult> call, Throwable t) {
                if (l != null) {
                    l.onSetLikeFailed(call, t, position);
                }
            }
        });
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

    private PhotoApi buildApi(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Mysplash.BASE_URL)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder()
                                        .setDateFormat(Mysplash.DATE_FORMAT)
                                        .create()))
                .build()
                .create((PhotoApi.class));
    }

    /** <br> interface. */

    public interface OnRequestPhotosListener {
        void onRequestPhotosSuccess(Call<List<Photo>> call, retrofit2.Response<List<Photo>> response,
                                    int page, boolean refresh);
        void onRequestPhotosFailed(Call<List<Photo>> call, Throwable t, boolean refresh);
    }

    public interface OnRequestStatsListener {
        void onRequestStatsSuccess(Call<PhotoStats> call, retrofit2.Response<PhotoStats> response);
        void onRequestStatsFailed(Call<PhotoStats> call, Throwable t);
    }

    public interface OnSetLikeListener {
        void onSetLikeSuccess(Call<LikePhotoResult> call, retrofit2.Response<LikePhotoResult> response, int position);
        void onSetLikeFailed(Call<LikePhotoResult> call, Throwable t, int position);
    }
}
