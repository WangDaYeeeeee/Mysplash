package com.wangdaye.mysplash.common.network.api;

import com.wangdaye.mysplash.common.network.json.Me;
import com.wangdaye.mysplash.common.network.json.User;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * User api.
 * */

public interface UserApi {

    @GET("users/{username}")
    Call<User> getUserProfile(@Path("username") String username,
                              @Query("w") int w,
                              @Query("h") int h);

    @GET("me")
    Call<Me> getMeProfile();

    @PUT("me")
    Call<Me> updateMeProfile(@Query("username") String username,
                             @Query("first_name") String first_name,
                             @Query("last_name") String last_name,
                             @Query("email") String email,
                             @Query("url") String url,
                             @Query("location") String location,
                             @Query("bio") String bio);

    @GET("users/{username}/following")
    Call<List<User>> getFolloweing(@Path("username") String username,
                                  @Query("page") int page,
                                  @Query("per_page") int per_page);

    @GET("users/{username}/followers")
    Call<List<User>> getFollowers(@Path("username") String username,
                                  @Query("page") int page,
                                  @Query("per_page") int per_page);
}
