package com.wangdaye.mysplash.common.network.api;

import com.wangdaye.mysplash.common.network.json.NotificationFeed;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Notification api.
 * */

public interface NotificationApi {

    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("napi/feeds/enrich")
    Call<NotificationFeed> getNotification(@Body RequestBody body);
}
