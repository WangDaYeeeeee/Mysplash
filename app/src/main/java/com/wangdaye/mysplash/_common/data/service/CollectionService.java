package com.wangdaye.mysplash._common.data.service;

import com.google.gson.GsonBuilder;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.api.CollectionApi;
import com.wangdaye.mysplash._common.data.data.AddPhotoToCollectionResult;
import com.wangdaye.mysplash._common.data.data.Collection;
import com.wangdaye.mysplash._common.data.data.DeleteCollectionResult;
import com.wangdaye.mysplash._common.data.data.Me;
import com.wangdaye.mysplash._common.data.data.User;
import com.wangdaye.mysplash._common.data.tools.AuthInterceptor;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Collection service.
 * */

public class CollectionService {
    // widget
    private OkHttpClient client;
    private Call call;

    /** <br> data. */
    public void requestAllCollections(int page, int per_page, final OnRequestCollectionsListener l) {
        Call<List<Collection>> getAllCollections = buildApi(client).getAllCollections(page, per_page);
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

    public void requestCuratedCollections(int page, int per_page, final OnRequestCollectionsListener l) {
        Call<List<Collection>> getCuratedCollections = buildApi(client).getCuratedCollections(page, per_page);
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

    public void requestFeaturedCollections(int page, int per_page, final OnRequestCollectionsListener l) {
        Call<List<Collection>> getFeaturedCollections = buildApi(client).getFeaturedCollections(page, per_page);
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

    public void requestUserCollections(User u, int page, int per_page, final OnRequestCollectionsListener l) {
        Call<List<Collection>> getUserCollections = buildApi(client).getUserCollections(u.username, page, per_page);
        getUserCollections.enqueue(new Callback<List<Collection>>() {
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
        call = getUserCollections;
    }

    public void requestUserCollections(Me me, int page, int per_page, final OnRequestCollectionsListener l) {
        Call<List<Collection>> getUserCollections = buildApi(client).getUserCollections(me.username, page, per_page);
        getUserCollections.enqueue(new Callback<List<Collection>>() {
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
        call = getUserCollections;
    }

    public void createCollection(String title, String description, boolean privateX,
                                 final OnRequestACollectionListener l) {
        Call<Collection> createCollection;
        if (description == null) {
            createCollection = buildApi(client).createCollection(title, privateX);
        } else {
            createCollection = buildApi(client).createCollection(title, description, privateX);
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

    public void addPhotoToCollection(int collection_id, String photo_id,
                                     final OnAddPhotoToCollectionListener l) {
        Call<AddPhotoToCollectionResult> addPhotoToCollection = buildApi(client)
                .addPhotoToCollection(collection_id, photo_id);
        addPhotoToCollection.enqueue(new Callback<AddPhotoToCollectionResult>() {
            @Override
            public void onResponse(Call<AddPhotoToCollectionResult> call,
                                   Response<AddPhotoToCollectionResult> response) {
                if (l != null) {
                    l.onAddPhotoSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<AddPhotoToCollectionResult> call, Throwable t) {
                if (l != null) {
                    l.onAddPhotoFailed(call, t);
                }
            }
        });
        call = addPhotoToCollection;
    }

    public void updateCollection(int id, String title, String description, boolean privateX,
                                 final OnRequestACollectionListener l) {
        Call<Collection> updateCollection = buildApi(client).updateCollection(id, title, description, privateX);
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

    public void deleteCollection(int id, final OnDeleteCollectionListener l) {
        Call<DeleteCollectionResult> deleteCollection = buildApi(client).deleteCollection(id);
        deleteCollection.enqueue(new Callback<DeleteCollectionResult>() {
            @Override
            public void onResponse(Call<DeleteCollectionResult> call,
                                   Response<DeleteCollectionResult> response) {
                if (l != null) {
                    l.onDeleteCollectionSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<DeleteCollectionResult> call, Throwable t) {
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

    /** <br> build. */

    public static CollectionService getService() {
        return new CollectionService();
    }

    public CollectionService buildClient() {
        this.client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .build();
        return this;
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

    /** <br> interface. */

    public interface OnRequestCollectionsListener {
        void onRequestCollectionsSuccess(Call<List<Collection>> call, retrofit2.Response<List<Collection>> response);
        void onRequestCollectionsFailed(Call<List<Collection>> call, Throwable t);
    }

    public interface OnRequestACollectionListener {
        void onRequestACollectionSuccess(Call<Collection> call, retrofit2.Response<Collection> response);
        void onRequestACollectionFailed(Call<Collection> call, Throwable t);
    }

    public interface OnAddPhotoToCollectionListener {
        void onAddPhotoSuccess(Call<AddPhotoToCollectionResult> call, retrofit2.Response<AddPhotoToCollectionResult> response);
        void onAddPhotoFailed(Call<AddPhotoToCollectionResult> call, Throwable t);
    }

    public interface OnDeleteCollectionListener {
        void onDeleteCollectionSuccess(Call<DeleteCollectionResult> call, Response<DeleteCollectionResult> response);
        void onDeleteCollectionFailed(Call<DeleteCollectionResult> call, Throwable t);
    }
}
