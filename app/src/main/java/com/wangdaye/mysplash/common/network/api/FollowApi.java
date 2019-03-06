package com.wangdaye.mysplash.common.network.api;

import com.wangdaye.mysplash.Mysplash;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Follow api.
 * */
public interface FollowApi {

    @POST(Mysplash.UNSPLASH_NODE_API_URL + "users/{username}/follow")
    Call<ResponseBody> follow(@Path("username") String username);

    @DELETE(Mysplash.UNSPLASH_NODE_API_URL + "users/{username}/follow")
    Call<ResponseBody> cancelFollow(@Path("username") String username);
}
