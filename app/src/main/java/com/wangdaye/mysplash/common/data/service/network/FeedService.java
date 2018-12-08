package com.wangdaye.mysplash.common.data.service.network;

import android.net.Uri;

import com.google.gson.GsonBuilder;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.basic.TLSCompactService;
import com.wangdaye.mysplash.common.data.api.FeedApi;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.TrendingFeed;
import com.wangdaye.mysplash.common.utils.widget.interceptor.FeedInterceptor;
import com.wangdaye.mysplash.common.utils.widget.interceptor.NapiInterceptor;

import java.util.List;

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

public class FeedService extends TLSCompactService {

    private Call call;

    public static FeedService getService() {
        return new FeedService();
    }

    private OkHttpClient buildClient() {
        return getClientBuilder()
                .addInterceptor(new FeedInterceptor())
                //.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new NapiInterceptor())
                .build();
    }

    private FeedApi buildApi(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder().setLenient().create()))
                .build()
                .create((FeedApi.class));
    }

    public void requestFollowingFeed(@Mysplash.PageRule int page,
                                     @Mysplash.PerPageRule int per_page,
                                     final OnRequestFeedPhotoListener l) {
        Call<List<Photo>> getPhotos = buildApi(buildClient()).getFollowingFeed(page, per_page);
        getPhotos.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, retrofit2.Response<List<Photo>> response) {
                if (l != null) {
                    l.onRequestFeedPhotoSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                if (l != null) {
                    l.onRequestFeedPhotoFailed(call, t);
                }
            }
        });
        call = getPhotos;
    }

    public void requestTrendFeed(String url, final OnRequestTrendingFeedListener l) {
        String after = Uri.parse(url).getQueryParameter("after");
        Call<TrendingFeed> getFeed = buildApi(buildClient()).getTrendingFeed(after, "1");
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

    public void setFollowUser(String username, final boolean follow, final OnFollowListener l) {
        Call<ResponseBody> followRequest;
        if (follow) {
            followRequest = buildApi(buildClient()).follow(username, "1");
        } else {
            followRequest = buildApi(buildClient()).cancelFollow(username, "1");
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

    public interface OnRequestFeedPhotoListener {
        void onRequestFeedPhotoSuccess(Call<List<Photo>> call, Response<List<Photo>> response);
        void onRequestFeedPhotoFailed(Call<List<Photo>> call, Throwable t);
    }

    public interface OnRequestTrendingFeedListener {
        void onRequestTrendingFeedSuccess(Call<TrendingFeed> call, Response<TrendingFeed> response);
        void onRequestTrendingFeedFailed(Call<TrendingFeed> call, Throwable t);
    }

    public interface OnFollowListener {
        void onFollowSuccess(Call<ResponseBody> call, Response<ResponseBody> response);
        void onCancelFollowSuccess(Call<ResponseBody> call, Response<ResponseBody> response);
        void onFollowFailed(Call<ResponseBody> call, Throwable t);
        void onCancelFollowFailed(Call<ResponseBody> call, Throwable t);
    }
}
