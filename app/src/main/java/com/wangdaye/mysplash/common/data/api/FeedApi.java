package com.wangdaye.mysplash.common.data.api;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.entity.unsplash.FollowingFeed;
import com.wangdaye.mysplash.common.data.entity.unsplash.TrendingFeed;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Feed api.
 * */

public interface FeedApi {

    @GET(Mysplash.UNSPLASH_NODE_API_URL + Mysplash.UNSPLASH_TREND_FEEDING_URL)
    Call<TrendingFeed> getTrendingFeed(@Query("after") String after);

    @GET(Mysplash.UNSPLASH_NODE_API_URL + Mysplash.UNSPLASH_FOLLOWING_FEED_URL)
    Call<FollowingFeed> getFollowingFeed(@Query("after") String after);

    @POST(Mysplash.UNSPLASH_NODE_API_URL + "users/{username}/follow")
    Call<ResponseBody> follow(@Path("username") String username);

    @DELETE(Mysplash.UNSPLASH_NODE_API_URL + "users/{username}/follow")
    Call<ResponseBody> cancelFollow(@Path("username") String username);
}
