package com.wangdaye.mysplash._common.data.api;

import com.wangdaye.mysplash._common.data.entity.unsplash.Total;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Status api.
 * */

public interface StatusApi {

    @GET("stats/total")
    Call<Total> getTotal();
}
