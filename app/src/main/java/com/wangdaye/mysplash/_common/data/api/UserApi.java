package com.wangdaye.mysplash._common.data.api;

import com.wangdaye.mysplash._common.data.data.Me;
import com.wangdaye.mysplash._common.data.data.User;


import retrofit2.Call;
import retrofit2.http.GET;
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
}
