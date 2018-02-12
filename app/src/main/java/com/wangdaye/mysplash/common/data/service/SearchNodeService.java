package com.wangdaye.mysplash.common.data.service;

import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common._basic.TLSCompactService;
import com.wangdaye.mysplash.common.data.api.SearchNodeApi;
import com.wangdaye.mysplash.common.data.entity.unsplash.SearchCollectionsResult;
import com.wangdaye.mysplash.common.data.entity.unsplash.SearchUsersResult;
import com.wangdaye.mysplash.common.utils.widget.interceptor.AuthInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Search node service.
 * */

public class SearchNodeService extends TLSCompactService {

    private Call call;

    static SearchNodeService getService() {
        return TextUtils.isEmpty(Mysplash.UNSPLASH_NODE_API_URL) ? null : new SearchNodeService();
    }

    private OkHttpClient buildClient() {
        return getClientBuilder()
                .addInterceptor(new AuthInterceptor())
                .build();
    }

    private SearchNodeApi buildApi(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder()
                                        .setDateFormat(Mysplash.DATE_FORMAT)
                                        .create()))
                .build()
                .create((SearchNodeApi.class));
    }

    void searchUsers(String query, int page, final SearchService.OnRequestUsersListener l) {
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
    }

    void searchCollections(String query, int page, final SearchService.OnRequestCollectionsListener l) {
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
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }
}
