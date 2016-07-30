package com.wangdaye.mysplash.common.data.api;

import com.wangdaye.mysplash.common.data.model.PhotoStats;
import com.wangdaye.mysplash.common.data.model.LikePhotoResult;
import com.wangdaye.mysplash.common.data.model.Photo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Photo api.
 * */

public interface PhotoApi {
    // data.
    int DEFAULT_PER_PAGE = 30;

    String ORDER_BY_LATEST = "latest";
    String ORDER_BY_OLDEST = "oldest";
    String ORDER_BY_POPULAR = "popular";

    String LANDSCAPE_ORIENTATION = "landscape";
    String PORTRAIT_ORIENTATION = "portrait";
    String SQUARE_ORIENTATION = "square";

    /** <br> interface. */

    @GET("photos")
    Call<List<Photo>> getPhotos(@Query("page") int page,
                                @Query("per_page") int per_page,
                                @Query("order_by") String order_by);

    @GET("photos/curated")
    Call<List<Photo>> getCuratedPhotos(@Query("page") int page,
                                       @Query("per_page") int per_page,
                                       @Query("order_by") String order_by);

    @GET("photos/search")
    Call<List<Photo>> searchPhotos(@Query("query") String query,
                                   @Query("orientation") String orientation,
                                   @Query("page") int page,
                                   @Query("per_page") int per_page);

    @GET("photos/{id}/stats")
    Call<PhotoStats> getPhotoStats(@Path("id") String id);

    @GET("categories/{id}/photos")
    Call<List<Photo>> getPhotosInAGivenCategory(@Path("id") int id,
                                                @Query("page") int page,
                                                @Query("per_page") int per_page);

    @POST("photos/{id}/like")
    Call<LikePhotoResult> likeAPhoto(@Path("id") String id);

    @DELETE("photos/{id}/like")
    Call<LikePhotoResult> unlikeAPhoto(@Path("id") String id);
}
