package com.wangdaye.common.network.api;

import com.wangdaye.base.unsplash.Me;
import com.wangdaye.base.unsplash.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * User api.
 * */

public interface UserApi {

    @GET("users/{username}")
    Observable<User> getUserProfile(@Path("username") String username,
                                    @Query("w") int w,
                                    @Query("h") int h);

    @GET("me")
    Observable<Me> getMeProfile();

    @PUT("me")
    Observable<Me> updateMeProfile(@Query("username") String username,
                                   @Query("first_name") String first_name,
                                   @Query("last_name") String last_name,
                                   @Query("email") String email,
                                   @Query("url") String url,
                                   @Query("location") String location,
                                   @Query("bio") String bio);

    @GET("users/{username}/following")
    Observable<List<User>> getFolloweing(@Path("username") String username,
                                         @Query("page") int page,
                                         @Query("per_page") int per_page);

    @GET("users/{username}/followers")
    Observable<List<User>> getFollowers(@Path("username") String username,
                                        @Query("page") int page,
                                        @Query("per_page") int per_page);
}
