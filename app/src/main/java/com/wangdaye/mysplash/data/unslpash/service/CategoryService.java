package com.wangdaye.mysplash.data.unslpash.service;

import com.google.gson.GsonBuilder;
import com.wangdaye.mysplash.data.constant.Mysplash;
import com.wangdaye.mysplash.data.unslpash.api.CategoriesApi;
import com.wangdaye.mysplash.data.unslpash.model.Category;
import com.wangdaye.mysplash.data.unslpash.tools.AuthInterceptor;
import com.wangdaye.mysplash.data.unslpash.tools.ClientInterceptor;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Category service.
 * */

public class CategoryService {
    // widget
    private OkHttpClient client;
    private Call call;

    /** <br> data. */

    public void getCategories(final OnRequestCategoriesListener l) {
        Call<List<Category>> getPhotos = buildApi(client).getCategories();
        getPhotos.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, retrofit2.Response<List<Category>> response) {
                if (l != null) {
                    l.onRequestCategoriesSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                if (l != null) {
                    l.onRequestCategoriesFailed(call, t);
                }
            }
        });
        call = getPhotos;
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

    /** <br> build. */

    public static CategoryService getService() {
        return new CategoryService();
    }

    public CategoryService buildClient(String token) {
        this.client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(token))
                .build();
        return this;
    }

    public CategoryService buildClient() {
        this.client = new OkHttpClient.Builder()
                .addInterceptor(new ClientInterceptor())
                .build();
        return this;
    }

    private CategoriesApi buildApi(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Mysplash.BASE_URL)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder()
                                        .setDateFormat(Mysplash.DATE_FORMAT)
                                        .create()))
                .build()
                .create((CategoriesApi.class));
    }

    /** <br> interface. */

    public interface OnRequestCategoriesListener {
        void onRequestCategoriesSuccess(Call<List<Category>> call, retrofit2.Response<List<Category>> response);
        void onRequestCategoriesFailed(Call<List<Category>> call, Throwable t);
    }
}
