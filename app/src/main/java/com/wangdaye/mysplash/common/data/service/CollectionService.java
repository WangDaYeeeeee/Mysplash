package com.wangdaye.mysplash.common.data.service;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.api.CollectionApi;
import com.wangdaye.mysplash.common.data.entity.unsplash.ChangeCollectionPhotoResult;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.utils.widget.interceptor.AuthInterceptor;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Collection service.
 * */

public class CollectionService {

    private Call call;

    public static CollectionService getService() {
        return new CollectionService();
    }

    private OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .build();
    }

    private CollectionApi buildApi(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_API_BASE_URL)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder()
                                        .setDateFormat(Mysplash.DATE_FORMAT)
                                        .create()))
                .build()
                .create((CollectionApi.class));
    }

    public void requestAllCollections(@Mysplash.PageRule int page,
                                      @Mysplash.PerPageRule int per_page,
                                      final OnRequestCollectionsListener l) {
        Call<List<Collection>> getAllCollections = buildApi(buildClient()).getAllCollections(page, per_page);
        getAllCollections.enqueue(new Callback<List<Collection>>() {
            @Override
            public void onResponse(Call<List<Collection>> call, retrofit2.Response<List<Collection>> response) {
                if (l != null) {
                    l.onRequestCollectionsSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<List<Collection>> call, Throwable t) {
                if (l != null) {
                    l.onRequestCollectionsFailed(call, t);
                }
            }
        });
        call = getAllCollections;
    }

    public void requestCuratedCollections(@Mysplash.PageRule int page,
                                          @Mysplash.PerPageRule int per_page,
                                          final OnRequestCollectionsListener l) {
        Call<List<Collection>> getCuratedCollections = buildApi(buildClient()).getCuratedCollections(page, per_page);
        getCuratedCollections.enqueue(new Callback<List<Collection>>() {
            @Override
            public void onResponse(Call<List<Collection>> call, retrofit2.Response<List<Collection>> response) {
                if (l != null) {
                    l.onRequestCollectionsSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<List<Collection>> call, Throwable t) {
                if (l != null) {
                    l.onRequestCollectionsFailed(call, t);
                }
            }
        });
        call = getCuratedCollections;
    }

    public void requestFeaturedCollections(@Mysplash.PageRule int page,
                                           @Mysplash.PerPageRule int per_page,
                                           final OnRequestCollectionsListener l) {
        Call<List<Collection>> getFeaturedCollections = buildApi(buildClient()).getFeaturedCollections(page, per_page);
        getFeaturedCollections.enqueue(new Callback<List<Collection>>() {
            @Override
            public void onResponse(Call<List<Collection>> call, retrofit2.Response<List<Collection>> response) {
                if (l != null) {
                    l.onRequestCollectionsSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<List<Collection>> call, Throwable t) {
                if (l != null) {
                    l.onRequestCollectionsFailed(call, t);
                }
            }
        });
        call = getFeaturedCollections;
    }

    public void requestACollections(String id,
                                    final OnRequestSingleCollectionListener l) {
        Call<Collection> getACollection = buildApi(buildClient()).getACollection(id);
        getACollection.enqueue(new Callback<Collection>() {
            @Override
            public void onResponse(Call<Collection> call, retrofit2.Response<Collection> response) {
                if (l != null) {
                    l.onRequestSingleCollectionSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<Collection> call, Throwable t) {
                if (l != null) {
                    l.onRequestSingleCollectionFailed(call, t);
                }
            }
        });
        call = getACollection;
    }

    @Nullable
    public Collection requestACollections(String id) {
        Call<Collection> getACollection = buildApi(buildClient()).getACollection(id);
        try {
            Response<Collection> response = getACollection.execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void requestACuratedCollections(String id,
                                           final OnRequestSingleCollectionListener l) {
        Call<Collection> getACuratedCollection = buildApi(buildClient()).getACuratedCollection(id);
        getACuratedCollection.enqueue(new Callback<Collection>() {
            @Override
            public void onResponse(Call<Collection> call, retrofit2.Response<Collection> response) {
                if (l != null) {
                    l.onRequestSingleCollectionSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<Collection> call, Throwable t) {
                if (l != null) {
                    l.onRequestSingleCollectionFailed(call, t);
                }
            }
        });
        call = getACuratedCollection;
    }

    @Nullable
    public Collection requestACuratedCollections(String id) {
        Call<Collection> getACuratedCollection = buildApi(buildClient()).getACuratedCollection(id);
        try {
            Response<Collection> response = getACuratedCollection.execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void requestUserCollections(String username,
                                       @Mysplash.PageRule int page,
                                       @Mysplash.PerPageRule int per_page,
                                       final OnRequestCollectionsListener l) {
        Call<List<Collection>> getUserCollections = buildApi(buildClient())
                .getUserCollections(username, page, per_page);
        getUserCollections.enqueue(new Callback<List<Collection>>() {
            @Override
            public void onResponse(
                    Call<List<Collection>> call,
                    retrofit2.Response<List<Collection>> response) {
                if (l != null) {
                    l.onRequestCollectionsSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<List<Collection>> call, Throwable t) {
                if (l != null) {
                    l.onRequestCollectionsFailed(call, t);
                }
            }
        });
        call = getUserCollections;
    }

    public void createCollection(String title, @Nullable String description, boolean privateX,
                                 final OnRequestACollectionListener l) {
        Call<Collection> createCollection;
        if (description == null) {
            createCollection = buildApi(buildClient()).createCollection(title, privateX);
        } else {
            createCollection = buildApi(buildClient()).createCollection(title, description, privateX);
        }
        createCollection.enqueue(new Callback<Collection>() {
            @Override
            public void onResponse(Call<Collection> call, Response<Collection> response) {
                if (l != null) {
                    l.onRequestACollectionSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<Collection> call, Throwable t) {
                if (l != null) {
                    l.onRequestACollectionFailed(call, t);
                }
            }
        });
        call = createCollection;
    }

    public void addPhotoToCollection(@IntRange(from = 0) int collectionId,
                                     String photoId,
                                     final OnChangeCollectionPhotoListener l) {
        Call<ChangeCollectionPhotoResult> addPhotoToCollection = buildApi(buildClient())
                .addPhotoToCollection(collectionId, photoId);
        addPhotoToCollection.enqueue(new Callback<ChangeCollectionPhotoResult>() {
            @Override
            public void onResponse(Call<ChangeCollectionPhotoResult> call,
                                   Response<ChangeCollectionPhotoResult> response) {
                if (l != null) {
                    l.onChangePhotoSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<ChangeCollectionPhotoResult> call, Throwable t) {
                if (l != null) {
                    l.onChangePhotoFailed(call, t);
                }
            }
        });
        call = addPhotoToCollection;
    }

    public void deletePhotoFromCollection(@IntRange(from = 0) int collectionId, String photoId,
                                          final OnChangeCollectionPhotoListener l) {
        Call<ChangeCollectionPhotoResult> deletePhotoFromCollection = buildApi(buildClient())
                .deletePhotoFromCollection(collectionId, photoId);
        deletePhotoFromCollection.enqueue(new Callback<ChangeCollectionPhotoResult>() {
            @Override
            public void onResponse(Call<ChangeCollectionPhotoResult> call,
                                   Response<ChangeCollectionPhotoResult> response) {
                if (l != null) {
                    l.onChangePhotoSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<ChangeCollectionPhotoResult> call, Throwable t) {
                if (l != null) {
                    l.onChangePhotoFailed(call, t);
                }
            }
        });
        call = deletePhotoFromCollection;
    }

    public void updateCollection(@IntRange(from = 0) int collectionId,
                                 String title, String description, boolean privateX,
                                 final OnRequestACollectionListener l) {
        Call<Collection> updateCollection = buildApi(buildClient())
                .updateCollection(collectionId, title, description, privateX);
        updateCollection.enqueue(new Callback<Collection>() {
            @Override
            public void onResponse(Call<Collection> call, Response<Collection> response) {
                if (l != null) {
                    l.onRequestACollectionSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<Collection> call, Throwable t) {
                if (l != null) {
                    l.onRequestACollectionFailed(call, t);
                }
            }
        });
        call = updateCollection;
    }

    public void deleteCollection(@IntRange(from = 0) int id, final OnDeleteCollectionListener l) {
        Call<ResponseBody> deleteCollection = buildApi(buildClient()).deleteCollection(id);
        deleteCollection.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if (l != null) {
                    l.onDeleteCollectionSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (l != null) {
                    l.onDeleteCollectionFailed(call, t);
                }
            }
        });
        call = deleteCollection;
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

    // interface.

    public interface OnRequestCollectionsListener {
        void onRequestCollectionsSuccess(Call<List<Collection>> call, retrofit2.Response<List<Collection>> response);
        void onRequestCollectionsFailed(Call<List<Collection>> call, Throwable t);
    }

    public interface OnRequestSingleCollectionListener {
        void onRequestSingleCollectionSuccess(Call<Collection> call, retrofit2.Response<Collection> response);
        void onRequestSingleCollectionFailed(Call<Collection> call, Throwable t);
    }

    public interface OnRequestACollectionListener {
        void onRequestACollectionSuccess(Call<Collection> call, retrofit2.Response<Collection> response);
        void onRequestACollectionFailed(Call<Collection> call, Throwable t);
    }

    public interface OnChangeCollectionPhotoListener {
        void onChangePhotoSuccess(Call<ChangeCollectionPhotoResult> call, retrofit2.Response<ChangeCollectionPhotoResult> response);
        void onChangePhotoFailed(Call<ChangeCollectionPhotoResult> call, Throwable t);
    }

    public interface OnDeleteCollectionListener {
        void onDeleteCollectionSuccess(Call<ResponseBody> call, Response<ResponseBody> response);
        void onDeleteCollectionFailed(Call<ResponseBody> call, Throwable t);
    }
}
