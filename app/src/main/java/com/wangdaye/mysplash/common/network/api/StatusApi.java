package com.wangdaye.mysplash.common.network.api;

import com.wangdaye.mysplash.common.network.json.Total;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Status api.
 * */

public interface StatusApi {

    @GET("stats/total")
    Call<Total> getTotal();
}
