package com.wangdaye.mysplash.data.unslpash.api;

import com.wangdaye.mysplash.data.unslpash.model.Category;
import com.wangdaye.mysplash.data.unslpash.model.Photo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Categories api.
 * */

public interface CategoriesApi {

    /** <br> interface. */

    @GET("categories")
    Call<List<Category>> getCategories();

    @GET("categories/:id")
    Call<Category> getACategory(@Query("id") String id);
}