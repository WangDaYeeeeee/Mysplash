package com.wangdaye.common.network.api;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.network.UrlCollection;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Photo node api.
 * */

public interface PhotoNodeApi {

    @GET(UrlCollection.UNSPLASH_NODE_API_URL + "photos/{id}/info")
    Observable<Photo> getAPhoto(@Path("id") String id);
}
