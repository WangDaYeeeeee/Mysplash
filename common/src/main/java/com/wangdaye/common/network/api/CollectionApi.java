package com.wangdaye.common.network.api;

import com.wangdaye.base.unsplash.ChangeCollectionPhotoResult;
import com.wangdaye.base.unsplash.Collection;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
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
    Observable<List<Collection>> getAllCollections(@Query("page") int page,
                                                   @Query("per_page") int per_page);

    @GET("collections/curated")
    Observable<List<Collection>> getCuratedCollections(@Query("page") int page,
                                                       @Query("per_page") int per_page);

    @GET("collections/featured")
    Observable<List<Collection>> getFeaturedCollections(@Query("page") int page,
                                                        @Query("per_page") int per_page);

    @GET("collections/{id}")
    Observable<Collection> getACollection(@Path("id") String id);

    @GET("collections/curated/{id}")
    Observable<Collection> getACuratedCollection(@Path("id") String id);

    @GET("users/{username}/collections")
    Observable<List<Collection>> getUserCollections(@Path("username") String username,
                                                    @Query("page") int page,
                                                    @Query("per_page") int per_page);

    @POST("collections")
    Observable<Collection> createCollection(@Query("title") String title,
                                            @Query("description") String description,
                                            @Query("private") boolean privateX);

    @POST("collections")
    Observable<Collection> createCollection(@Query("title") String title,
                                            @Query("private") boolean privateX);

    @PUT("collections/{id}")
    Observable<Collection> updateCollection(@Path("id") int id,
                                            @Query("title") String title,
                                            @Query("description") String description,
                                            @Query("private") boolean privateX);

    @DELETE("collections/{id}")
    Observable<ResponseBody> deleteCollection(@Path("id") int id);

    @POST("collections/{collection_id}/add")
    Observable<ChangeCollectionPhotoResult> addPhotoToCollection(@Path("collection_id") int collection_id,
                                                                 @Query("photo_id") String photo_id);

    @DELETE("collections/{collection_id}/remove")
    Observable<ChangeCollectionPhotoResult> deletePhotoFromCollection(@Path("collection_id") int collection_id,
                                                                      @Query("photo_id") String photo_id);
}
