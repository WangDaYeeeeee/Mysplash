package com.wangdaye.mysplash._common.data.api;

import com.wangdaye.mysplash._common.data.data.ChangeCollectionPhotoResult;
import com.wangdaye.mysplash._common.data.data.Collection;
import com.wangdaye.mysplash._common.data.data.DeleteCollectionResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Collection api.
 * */

public interface CollectionApi {

    @GET("collections")
    Call<List<Collection>> getAllCollections(@Query("page") int page,
                                             @Query("per_page") int per_page);

    @GET("collections/curated")
    Call<List<Collection>> getCuratedCollections(@Query("page") int page,
                                                 @Query("per_page") int per_page);

    @GET("collections/featured")
    Call<List<Collection>> getFeaturedCollections(@Query("page") int page,
                                                  @Query("per_page") int per_page);

    @GET("users/{username}/collections")
    Call<List<Collection>> getUserCollections(@Path("username") String username,
                                              @Query("page") int page,
                                              @Query("per_page") int per_page);

    @POST("collections")
    Call<Collection> createCollection(@Query("title") String title,
                                      @Query("description") String description,
                                      @Query("private") boolean privateX);

    @POST("collections")
    Call<Collection> createCollection(@Query("title") String title,
                                      @Query("private") boolean privateX);

    @PUT("collections/{id}")
    Call<Collection> updateCollection(@Path("id") int id,
                                      @Query("title") String title,
                                      @Query("description") String description,
                                      @Query("private") boolean privateX);

    @DELETE("collections/{id}")
    Call<DeleteCollectionResult> deleteCollection(@Path("id") int id);

    @POST("collections/{collection_id}/add")
    Call<ChangeCollectionPhotoResult> addPhotoToCollection(@Path("collection_id") int collection_id,
                                                           @Query("photo_id") String photo_id);

    @DELETE("collections/{collection_id}/remove")
    Call<ChangeCollectionPhotoResult> deletePhotoFromCollection(@Path("collection_id") int collection_id,
                                                                @Query("photo_id") String photo_id);
}
