package com.wangdaye.mysplash.common.network.api;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.json.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * User node api.
 * */

public interface UserNodeApi {

    @GET(Mysplash.UNSPLASH_NODE_API_URL + "users/{username}")
    Observable<User> getUserProfile(@Path("username") String username,
                                    @Query("w") int w,
                                    @Query("h") int h);

    @GET(Mysplash.UNSPLASH_NODE_API_URL + "users/{username}/following")
    Observable<List<User>> getFolloweing(@Path("username") String username,
                                         @Query("page") int page,
                                         @Query("per_page") int per_page);

    @GET(Mysplash.UNSPLASH_NODE_API_URL + "users/{username}/followers")
    Observable<List<User>> getFollowers(@Path("username") String username,
                                        @Query("page") int page,
                                        @Query("per_page") int per_page);
}
