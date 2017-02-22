package com.wangdaye.mysplash._common.data.api;

import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Photo info api.
 * */

public interface PhotoInfoApi {

    @GET("XXX")
    Call<Photo> getPhotoInfo(@Path("id") String id);
}
