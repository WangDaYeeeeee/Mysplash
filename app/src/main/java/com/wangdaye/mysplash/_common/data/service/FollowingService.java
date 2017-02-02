package com.wangdaye.mysplash._common.data.service;

import android.net.Uri;

import com.google.gson.GsonBuilder;
import com.wangdaye.mysplash._common.data.api.FollowingApi;
import com.wangdaye.mysplash._common.data.entity.unsplash.FollowingFeedResult;
import com.wangdaye.mysplash._common.utils.widget.FollowingInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Feed service.
 * */

public class FollowingService {
    // widget
    private Call call;

    /** <br> data. */

    public void requestFollowingFeed(String url, final OnRequestFollowingFeedListener l) {
        String after = Uri.parse(url).getQueryParameter("after");
        Call<FollowingFeedResult> getFeed = buildApi(buildClient()).getFollowingFeed(after);
        getFeed.enqueue(new Callback<FollowingFeedResult>() {
            @Override
            public void onResponse(Call<FollowingFeedResult> call, retrofit2.Response<FollowingFeedResult> response) {
                if (l != null) {
                    l.onRequestFollowingFeedSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<FollowingFeedResult> call, Throwable t) {
                if (l != null) {
                    l.onRequestFollowingFeedFailed(call, t);
                }
            }
        });
        call = getFeed;
    }

    public void setFollowUser(String username, final boolean follow, final OnFollowListener l) {
        Call<ResponseBody> followRequest;
        if (follow) {
            followRequest = buildApi(buildClient()).follow(username);
        } else {
            followRequest = buildApi(buildClient()).cancelFollow(username);
        }
        followRequest.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (l != null) {
                    if (follow) {
                        l.onFollowSuccess(call, response);
                    } else {
                        l.onCancelFollowSuccess(call, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (l != null) {
                    if (follow) {
                        l.onFollowFailed(call, t);
                    } else {
                        l.onCancelFollowFailed(call, t);
                    }
                }
            }
        });
        call = followRequest;
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

    /** <br> build. */

    public static FollowingService getService() {
        return new FollowingService();
    }

    private OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new FollowingInterceptor())
                .build();
    }

    private FollowingApi buildApi(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl("https://unsplash.com/")
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder().setLenient().create()))
                .build()
                .create((FollowingApi.class));
    }

    /** <br> interface. */

    public interface OnRequestFollowingFeedListener {
        void onRequestFollowingFeedSuccess(Call<FollowingFeedResult> call, retrofit2.Response<FollowingFeedResult> response);
        void onRequestFollowingFeedFailed(Call<FollowingFeedResult> call, Throwable t);
    }

    public interface OnFollowListener {
        void onFollowSuccess(Call<ResponseBody> call, Response<ResponseBody> response);
        void onCancelFollowSuccess(Call<ResponseBody> call, Response<ResponseBody> response);
        void onFollowFailed(Call<ResponseBody> call, Throwable t);
        void onCancelFollowFailed(Call<ResponseBody> call, Throwable t);
    }
}
