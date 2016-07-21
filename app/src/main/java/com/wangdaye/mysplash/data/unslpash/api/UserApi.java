package com.wangdaye.mysplash.data.unslpash.api;

import com.wangdaye.mysplash.data.unslpash.model.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * User api.
 * */

public interface UserApi {
    // data
    int DEFAULT_PROFILE_IMAGE_SIZE = 200;

    /** <br> interface. */

    @GET("users/:username?username={username}&w={w}&h={h}")
    Call<User> getUser(@Path("username") String username,
                       @Path("w") int w,
                       @Path("h") int h);
}
