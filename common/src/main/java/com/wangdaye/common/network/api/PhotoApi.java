package com.wangdaye.common.network.api;

import com.wangdaye.base.unsplash.LikePhotoResult;
import com.wangdaye.base.unsplash.Photo;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Photo api.
 */

public interface PhotoApi {

    @GET("photos")
    Observable<List<Photo>> getPhotos(@Query("page") int page,
                                      @Query("per_page") int per_page,
                                      @Query("order_by") String order_by);

    @GET("photos/curated")
    Observable<List<Photo>> getCuratedPhotos(@Query("page") int page,
                                             @Query("per_page") int per_page,
                                             @Query("order_by") String order_by);

    @POST("photos/{id}/like")
    Observable<LikePhotoResult> likeAPhoto(@Path("id") String id);

    @DELETE("photos/{id}/like")
    Observable<LikePhotoResult> unlikeAPhoto(@Path("id") String id);

    @GET("photos/{id}")
    Observable<Photo> getAPhoto(@Path("id") String id);

    @GET("users/{username}/photos")
    Observable<List<Photo>> getUserPhotos(@Path("username") String username,
                                          @Query("page") int page,
                                          @Query("per_page") int per_page,
                                          @Query("order_by") String order_by);

    @GET("users/{username}/likes")
    Observable<List<Photo>> getUserLikes(@Path("username") String username,
                                         @Query("page") int page,
                                         @Query("per_page") int per_page,
                                         @Query("order_by") String order_by);

    @GET("collections/{id}/photos")
    Observable<List<Photo>> getCollectionPhotos(@Path("id") int id,
                                                @Query("page") int page,
                                                @Query("per_page") int per_page);

    @GET("collections/curated/{id}/photos")
    Observable<List<Photo>> getCuratedCollectionPhotos(@Path("id") int id,
                                                       @Query("page") int page,
                                                       @Query("per_page") int per_page);

    @GET("photos/random")
    Observable<List<Photo>> getRandomPhotos(@Query("collections") String collections,
                                            @Query("featured") Boolean featured,
                                            @Query("username") String username,
                                            @Query("query") String query,
                                            @Query("orientation") String orientation,
                                            @Query("count") int count);

    @GET("photos/random")
    Call<List<Photo>> callRandomPhotos(@Query("collections") String collections,
                                       @Query("featured") Boolean featured,
                                       @Query("username") String username,
                                       @Query("query") String query,
                                       @Query("orientation") String orientation,
                                       @Query("count") int count);

    @GET("photos/{id}/download")
    Observable<ResponseBody> downloadPhoto(@Path("id") String id);
}
