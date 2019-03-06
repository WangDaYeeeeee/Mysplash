package com.wangdaye.mysplash.common.network.api;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.json.TrendingFeed;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Feed api.
 * */

public interface FeedApi {

    @GET(Mysplash.UNSPLASH_NODE_API_URL + Mysplash.UNSPLASH_FOLLOWING_FEED_URL)
    Call<List<Photo>> getFollowingFeed(@Query("page") int page,
                                       @Query("per_page") int per_page);

    @GET(Mysplash.UNSPLASH_NODE_API_URL + Mysplash.UNSPLASH_TREND_FEEDING_URL)
    Call<TrendingFeed> getTrendingFeed(@Query("after") String after,
                                       @Query("Insurance") String Insurance);
}
