package com.wangdaye.mysplash.common.data.api;

import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Photo info api.
 * */

public interface PhotoInfoApi {

    @GET("napi/photos/{id}/info")
    Call<Photo> getPhotoInfo(@Path("id") String id);
}
