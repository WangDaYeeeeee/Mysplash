package com.wangdaye.mysplash.common.data.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Stream api.
 * */

public interface GetStreamApi {

    @GET("api/v1.0/feed/notification/{numeric_id}/")
    Call<ResponseBody> getFirstPageStream(@Path("numeric_id") int numeric_id,
                                          @Query("limit") int limit,
                                          @Query("api_key") String api_key,
                                          @Query("location") String location);

    @GET
    Call<ResponseBody> getNextPageStream(@Url String next_page);
}
