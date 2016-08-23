package com.wangdaye.mysplash._common.data.api;

import com.wangdaye.mysplash._common.data.data.PhotoDetails;
import com.wangdaye.mysplash._common.data.data.PhotoStats;
import com.wangdaye.mysplash._common.data.data.LikePhotoResult;
import com.wangdaye.mysplash._common.data.data.Photo;

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

    @GET("photos/{id}")
    Call<PhotoDetails> getAPhoto(@Path("id") String id,
                                 @Query("w") int w,
                                 @Query("h") int h,
                                 @Query("rect") String rect);

    @GET("users/{username}/photos")
    Call<List<Photo>> getUserPhotos(@Path("username") String username,
                                    @Query("page") int page,
                                    @Query("per_page") int per_page,
                                    @Query("order_by") String order_by);

    @GET("users/{username}/likes")
    Call<List<Photo>> getUserLikes(@Path("username") String username,
                                    @Query("page") int page,
                                    @Query("per_page") int per_page,
                                    @Query("order_by") String order_by);

    @GET("collections/{id}/photos")
    Call<List<Photo>> getCollectionPhotos(@Path("id") int id,
                                          @Query("page") int page,
                                          @Query("per_page") int per_page);

    @GET("collections/curated/{id}/photos")
    Call<List<Photo>> getCuratedCollectionPhotos(@Path("id") int id,
                                                 @Query("page") int page,
                                                 @Query("per_page") int per_page);
}
