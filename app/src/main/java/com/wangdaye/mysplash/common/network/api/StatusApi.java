package com.wangdaye.mysplash.common.network.api;

import com.wangdaye.mysplash.common.network.json.Total;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Status api.
 * */

public interface StatusApi {

    @GET("stats/total")
    Observable<Total> getTotal();
}
