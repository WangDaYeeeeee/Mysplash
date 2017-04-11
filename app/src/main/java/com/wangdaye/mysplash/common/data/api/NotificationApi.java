package com.wangdaye.mysplash.common.data.api;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.entity.unsplash.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Notification api.
 * */

public interface NotificationApi {

    @POST(Mysplash.UNSPLASH_NOTIFICATION_URL)
    Call<Notification> getNotification(@Body String enrich);
}
