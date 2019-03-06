package com.wangdaye.mysplash.common.network.api;

import com.wangdaye.mysplash.BuildConfig;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.OPTIONS;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Stream api.
 * */

public interface GetStreamApi {

    @Headers({
            "Connection: keep-alive",
            "Access-Control-Request-Method: GET",
            "Origin: https://unsplash.com",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.83 Safari/537.36",
            "Access-Control-Request-Headers: authorization,stream-auth-type,x-stream-client",
            "Accept: */*",
            "Referer: https://unsplash.com/",
            "Accept-Encoding: gzip, deflate, sdch, br",
            "Accept-Language: zh-CN,zh;q=0.8,en;q=0.6"
    })
    @OPTIONS("api/v1.0/feed/notification/{numeric_id}/")
    Call<ResponseBody> optionFirstPageStream(@Path("numeric_id") int numeric_id,
                                             @Query("limit") int limit,
                                             @Query("api_key") String api_key,
                                             @Query("location") String location);

    @Headers({
            "Connection: keep-alive",
            "accept: application/json",
            "stream-auth-type: jwt",
            "Origin: https://unsplash.com",
            "X-Stream-Client: stream-javascript-client-browser-unknown",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.83 Safari/537.36",
            "Authorization: " + BuildConfig.GET_STREAM_AUTH_CODE,
            "Referer: https://unsplash.com/",
            "Accept-Encoding: gzip, deflate, sdch, br",
            "Accept-Language: zh-CN,zh;q=0.8,en;q=0.6"

    })
    @GET("api/v1.0/feed/notification/{numeric_id}/")
    Call<ResponseBody> getFirstPageStream(@Path("numeric_id") int numeric_id,
                                          @Query("limit") int limit,
                                          @Query("api_key") String api_key,
                                          @Query("location") String location);

    @Headers({
            "Connection: keep-alive",
            "Access-Control-Request-Method: GET",
            "Origin: https://unsplash.com",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.83 Safari/537.36",
            "Access-Control-Request-Headers: authorization,stream-auth-type,x-stream-client",
            "Accept: */*",
            "Referer: https://unsplash.com/",
            "Accept-Encoding: gzip, deflate, sdch, br",
            "Accept-Language: zh-CN,zh;q=0.8,en;q=0.6"
    })
    @OPTIONS
    Call<ResponseBody> optionNextPageStream(@Url String next_page);

    @Headers({
            "Connection: keep-alive",
            "accept: application/json",
            "stream-auth-type: jwt",
            "Origin: https://unsplash.com",
            "X-Stream-Client: stream-javascript-client-browser-unknown",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.83 Safari/537.36",
            "Authorization: " + BuildConfig.GET_STREAM_AUTH_CODE,
            "Referer: https://unsplash.com/",
            "Accept-Encoding: gzip, deflate, sdch, br",
            "Accept-Language: zh-CN,zh;q=0.8,en;q=0.6"
    })
    @GET
    Call<ResponseBody> getNextPageStream(@Url String next_page);
}
