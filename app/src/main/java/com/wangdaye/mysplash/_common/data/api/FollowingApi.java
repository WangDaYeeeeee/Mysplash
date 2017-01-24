package com.wangdaye.mysplash._common.data.api;

import com.wangdaye.mysplash._common.data.entity.unsplash.FollowingFeedResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Feed api.
 * */

public interface FollowingApi {

    @GET("napi/feeds/following")
    Call<FollowingFeedResult> getFeedFollowingResult(@Query("after") String after);
}
