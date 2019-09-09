package com.wangdaye.common.network.api;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.network.UrlCollection;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Feed api.
 * */

public interface FeedApi {

    @GET(UrlCollection.UNSPLASH_NODE_API_URL + UrlCollection.UNSPLASH_FOLLOWING_FEED_URL)
    Observable<List<Photo>> getFollowingFeed(@Query("page") int page,
                                             @Query("per_page") int per_page);
}
