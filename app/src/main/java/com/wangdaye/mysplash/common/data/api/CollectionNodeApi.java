package com.wangdaye.mysplash.common.data.api;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Collection node api.
 * */

public interface CollectionNodeApi {

    @GET(Mysplash.UNSPLASH_NODE_API_URL + "collections")
    Call<List<Collection>> getAllCollections(@Query("page") int page,
                                             @Query("per_page") int per_page);

    @GET(Mysplash.UNSPLASH_NODE_API_URL + "collections/curated")
    Call<List<Collection>> getCuratedCollections(@Query("page") int page,
                                                 @Query("per_page") int per_page);

    @GET(Mysplash.UNSPLASH_NODE_API_URL + "collections/featured")
    Call<List<Collection>> getFeaturedCollections(@Query("page") int page,
                                                  @Query("per_page") int per_page);

    @GET(Mysplash.UNSPLASH_NODE_API_URL + "collections/{id}")
    Call<Collection> getACollection(@Path("id") String id);

    @GET(Mysplash.UNSPLASH_NODE_API_URL + "collections/curated/{id}")
    Call<Collection> getACuratedCollection(@Path("id") String id);

    @GET(Mysplash.UNSPLASH_NODE_API_URL + "users/{username}/collections")
    Call<List<Collection>> getUserCollections(@Path("username") String username,
                                              @Query("page") int page,
                                              @Query("per_page") int per_page);
}
