package com.wangdaye.mysplash.common.data.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Stream api.
 * */

public interface GetStreamApi {

    @GET("{user_code}")
    Call<ResponseBody> getEnrich(@Path("user_code") String user_code,
                                 @Query("limit") int limit,
                                 @Query("api_key") String api_key,
                                 @Query("location") String location);
}
