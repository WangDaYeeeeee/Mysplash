package com.wangdaye.mysplash.common.data.api;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Photo node api.
 * */

public interface PhotoNodeApi {

    @GET(Mysplash.UNSPLASH_NODE_API_URL + "photos/{id}/info")
    Call<Photo> getAPhoto(@Path("id") String id);
}
