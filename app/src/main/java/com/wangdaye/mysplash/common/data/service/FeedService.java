package com.wangdaye.mysplash.common.data.service;

import android.net.Uri;

import com.google.gson.GsonBuilder;
import com.wangdaye.mysplash.common.data.api.FeedApi;
import com.wangdaye.mysplash.common.data.entity.unsplash.FollowingFeed;
import com.wangdaye.mysplash.common.data.entity.unsplash.TrendingFeed;
import com.wangdaye.mysplash.common.utils.widget.interceptor.FeedInterceptor;

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

public class FeedService {

    private Call call;

    public static FeedService getService() {
        return new FeedService();
    }

    private OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new FeedInterceptor())
                .build();
    }

    private FeedApi buildApi(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl("https://unsplash.com/")
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder().setLenient().create()))
                .build()
                .create((FeedApi.class));
    }

    public void requestTrendingFeed(String url, final OnRequestTrendingFeedListener l) {
        String after = Uri.parse(url).getQueryParameter("after");
        Call<TrendingFeed> getFeed = buildApi(buildClient()).getTrendingFeed(after);
        getFeed.enqueue(new Callback<TrendingFeed>() {
            @Override
            public void onResponse(Call<TrendingFeed> call, retrofit2.Response<TrendingFeed> response) {
                if (l != null) {
                    l.onRequestTrendingFeedSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<TrendingFeed> call, Throwable t) {
                if (l != null) {
                    l.onRequestTrendingFeedFailed(call, t);
                }
            }
        });
        call = getFeed;
    }

    public void requestFollowingFeed(String url, final OnRequestFollowingFeedListener l) {
        String after = Uri.parse(url).getQueryParameter("after");
        Call<FollowingFeed> getFeed = buildApi(buildClient()).getFollowingFeed(after);
        getFeed.enqueue(new Callback<FollowingFeed>() {
            @Override
            public void onResponse(Call<FollowingFeed> call, retrofit2.Response<FollowingFeed> response) {
                if (l != null) {
                    l.onRequestFollowingFeedSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<FollowingFeed> call, Throwable t) {
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

    // interface.

    public interface OnRequestTrendingFeedListener {
        void onRequestTrendingFeedSuccess(Call<TrendingFeed> call, Response<TrendingFeed> response);
        void onRequestTrendingFeedFailed(Call<TrendingFeed> call, Throwable t);
    }

    public interface OnRequestFollowingFeedListener {
        void onRequestFollowingFeedSuccess(Call<FollowingFeed> call, Response<FollowingFeed> response);
        void onRequestFollowingFeedFailed(Call<FollowingFeed> call, Throwable t);
    }

    public interface OnFollowListener {
        void onFollowSuccess(Call<ResponseBody> call, Response<ResponseBody> response);
        void onCancelFollowSuccess(Call<ResponseBody> call, Response<ResponseBody> response);
        void onFollowFailed(Call<ResponseBody> call, Throwable t);
        void onCancelFollowFailed(Call<ResponseBody> call, Throwable t);
    }
}
