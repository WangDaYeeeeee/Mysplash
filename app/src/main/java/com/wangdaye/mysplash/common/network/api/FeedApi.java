package com.wangdaye.mysplash.common.network.api;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.json.Photo;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Feed api.
 * */

public interface FeedApi {

    @GET(Mysplash.UNSPLASH_NODE_API_URL + Mysplash.UNSPLASH_FOLLOWING_FEED_URL)
    Observable<List<Photo>> getFollowingFeed(@Query("page") int page,
                                             @Query("per_page") int per_page);
}
