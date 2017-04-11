package com.wangdaye.mysplash.common.data.service;

import com.wangdaye.mysplash.BuildConfig;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.api.GetStreamApi;
import com.wangdaye.mysplash.common.utils.widget.interceptor.GetStreamInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Get stream service.
 * */

public class GetStreamService {
    // widget
    private Call call;

    /** <br> data. */

    public void requestFollowingFeed(String user_code, final OnRequestEnrichListener l) {
        Call<ResponseBody> getEnrich = buildApi(buildClient())
                .getEnrich(user_code, Mysplash.DEFAULT_PER_PAGE, BuildConfig.GET_STREAM_KEY, "unspecified");
        getEnrich.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (l != null) {
                    l.onRequestEnrichSucceed(call, response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (l != null) {
                    l.onRequestEnrichFailed(call, t);
                }
            }
        });
        call = getEnrich;
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

    /** <br> build. */

    public static GetStreamService getService() {
        return new GetStreamService();
    }

    private OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new GetStreamInterceptor())
                .build();
    }

    private GetStreamApi buildApi(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Mysplash.STREAM_API_BASE_URL)
                .client(client)
                .build()
                .create((GetStreamApi.class));
    }

    /** <br> interface. */

    public interface OnRequestEnrichListener {
        void onRequestEnrichSucceed(Call<ResponseBody> call, Response<ResponseBody> response);
        void onRequestEnrichFailed(Call<ResponseBody> call, Throwable t);
    }
}
