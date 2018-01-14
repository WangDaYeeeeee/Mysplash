package com.wangdaye.mysplash.common.data.service;

import com.google.gson.GsonBuilder;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.api.SearchApi;
import com.wangdaye.mysplash.common.data.entity.unsplash.SearchCollectionsResult;
import com.wangdaye.mysplash.common.data.entity.unsplash.SearchPhotosResult;
import com.wangdaye.mysplash.common.data.entity.unsplash.SearchUsersResult;
import com.wangdaye.mysplash.common.utils.widget.interceptor.AuthInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Search service.
 * */

public class SearchService {

    private Call call;
    private SearchNodeService nodeService;

    private SearchService() {
        call = null;
        nodeService = SearchNodeService.getService();
    }

    public static SearchService getService() {
        return new SearchService();
    }

    private OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .build();
    }

    private SearchApi buildApi(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_API_BASE_URL)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder()
                                        .setDateFormat(Mysplash.DATE_FORMAT)
                                        .create()))
                .build()
                .create((SearchApi.class));
    }

    public void searchPhotos(String query, int page, final OnRequestPhotosListener l) {
        Call<SearchPhotosResult> searchPhotos = buildApi(buildClient()).searchPhotos(query, page, Mysplash.DEFAULT_PER_PAGE);
        searchPhotos.enqueue(new Callback<SearchPhotosResult>() {
            @Override
            public void onResponse(Call<SearchPhotosResult> call, retrofit2.Response<SearchPhotosResult> response) {
                if (l != null) {
                    l.onRequestPhotosSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<SearchPhotosResult> call, Throwable t) {
                if (l != null) {
                    l.onRequestPhotosFailed(call, t);
                }
            }
        });
        call = searchPhotos;
    }

    public void searchUsers(String query, int page, final OnRequestUsersListener l) {
        if (nodeService == null) {
            Call<SearchUsersResult> searchUsers = buildApi(buildClient()).searchUsers(query, page, Mysplash.DEFAULT_PER_PAGE);
            searchUsers.enqueue(new Callback<SearchUsersResult>() {
                @Override
                public void onResponse(Call<SearchUsersResult> call, retrofit2.Response<SearchUsersResult> response) {
                    if (l != null) {
                        l.onRequestUsersSuccess(call, response);
                    }
                }

                @Override
                public void onFailure(Call<SearchUsersResult> call, Throwable t) {
                    if (l != null) {
                        l.onRequestUsersFailed(call, t);
                    }
                }
            });
            call = searchUsers;
        } else {
            nodeService.searchUsers(query, page, l);
        }
    }

    public void searchCollections(String query, int page, final OnRequestCollectionsListener l) {
        if (nodeService == null) {
            Call<SearchCollectionsResult> searchCollections = buildApi(buildClient()).searchCollections(query, page, Mysplash.DEFAULT_PER_PAGE);
            searchCollections.enqueue(new Callback<SearchCollectionsResult>() {
                @Override
                public void onResponse(Call<SearchCollectionsResult> call, retrofit2.Response<SearchCollectionsResult> response) {
                    if (l != null) {
                        l.onRequestCollectionsSuccess(call, response);
                    }
                }

                @Override
                public void onFailure(Call<SearchCollectionsResult> call, Throwable t) {
                    if (l != null) {
                        l.onRequestCollectionsFailed(call, t);
                    }
                }
            });
            call = searchCollections;
        } else {
            nodeService.searchCollections(query, page, l);
        }
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

    // interface.

    public interface OnRequestPhotosListener {
        void onRequestPhotosSuccess(Call<SearchPhotosResult> call, retrofit2.Response<SearchPhotosResult> response);
        void onRequestPhotosFailed(Call<SearchPhotosResult> call, Throwable t);
    }

    public interface OnRequestCollectionsListener {
        void onRequestCollectionsSuccess(Call<SearchCollectionsResult> call, retrofit2.Response<SearchCollectionsResult> response);
        void onRequestCollectionsFailed(Call<SearchCollectionsResult> call, Throwable t);
    }

    public interface OnRequestUsersListener {
        void onRequestUsersSuccess(Call<SearchUsersResult> call, retrofit2.Response<SearchUsersResult> response);
        void onRequestUsersFailed(Call<SearchUsersResult> call, Throwable t);
    }
}
