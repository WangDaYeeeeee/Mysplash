package com.wangdaye.common.network.api;

import com.wangdaye.base.unsplash.Total;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Status api.
 * */

public interface StatusApi {

    @GET("stats/total")
    Observable<Total> getTotal();
}
