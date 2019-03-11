package com.wangdaye.mysplash.common.network.api;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.json.SearchCollectionsResult;
import com.wangdaye.mysplash.common.network.json.SearchUsersResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Search node api.
 * */

public interface SearchNodeApi {

    @GET(Mysplash.UNSPLASH_NODE_API_URL + "search/users")
    Observable<SearchUsersResult> searchUsers(@Query("query") String query,
                                              @Query("page") int page,
                                              @Query("per_page") int per_page);

    @GET(Mysplash.UNSPLASH_NODE_API_URL + "search/collections")
    Observable<SearchCollectionsResult> searchCollections(@Query("query") String query,
                                                          @Query("page") int page,
                                                          @Query("per_page") int per_page);
}
