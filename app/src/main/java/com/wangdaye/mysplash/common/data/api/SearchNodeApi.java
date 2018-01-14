package com.wangdaye.mysplash.common.data.api;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.entity.unsplash.SearchCollectionsResult;
import com.wangdaye.mysplash.common.data.entity.unsplash.SearchUsersResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Search node api.
 * */

public interface SearchNodeApi {

    @GET(Mysplash.UNSPLASH_NODE_API_URL + "search/users")
    Call<SearchUsersResult> searchUsers(@Query("query") String query,
                                        @Query("page") int page,
                                        @Query("per_page") int per_page);

    @GET(Mysplash.UNSPLASH_NODE_API_URL + "search/collections")
    Call<SearchCollectionsResult> searchCollections(@Query("query") String query,
                                                    @Query("page") int page,
                                                    @Query("per_page") int per_page);
}
