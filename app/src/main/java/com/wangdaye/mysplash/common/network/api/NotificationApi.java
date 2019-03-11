package com.wangdaye.mysplash.common.network.api;

import com.wangdaye.mysplash.common.network.json.NotificationFeed;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Notification api.
 * */

public interface NotificationApi {

    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("napi/feeds/enrich")
    Observable<NotificationFeed> getNotification(@Body RequestBody body);
}
