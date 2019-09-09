package com.wangdaye.common.network.api;

import com.wangdaye.base.unsplash.SearchCollectionsResult;
import com.wangdaye.base.unsplash.SearchPhotosResult;
import com.wangdaye.base.unsplash.SearchUsersResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Search api.
 * */

public interface SearchApi {

    @GET("search/photos")
    Observable<SearchPhotosResult> searchPhotos(@Query("query") String query,
                                                @Query("page") int page,
                                                @Query("per_page") int per_page);

    @GET("search/users")
    Observable<SearchUsersResult> searchUsers(@Query("query") String query,
                                              @Query("page") int page,
                                              @Query("per_page") int per_page);

    @GET("search/collections")
    Observable<SearchCollectionsResult> searchCollections(@Query("query") String query,
                                                          @Query("page") int page,
                                                          @Query("per_page") int per_page);
}
