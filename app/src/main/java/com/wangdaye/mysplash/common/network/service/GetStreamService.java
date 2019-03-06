package com.wangdaye.mysplash.common.network.service;

import com.wangdaye.mysplash.BuildConfig;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.api.GetStreamApi;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Get stream service.
 * */

public class GetStreamService {

    private GetStreamApi api;

    @Nullable private Call call;
    @Nullable private Callback callback;

    @Inject
    public GetStreamService(OkHttpClient client) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.STREAM_API_BASE_URL)
                .client(client)
                .build()
                .create((GetStreamApi.class));
        call = null;
        callback = null;
    }

    public void requestFirstPageStream(Callback<ResponseBody> callback) {
        if (AuthManager.getInstance().isAuthorized()
                && AuthManager.getInstance().getUser() != null
                && AuthManager.getInstance().getUser().numeric_id >= 0) {
            optionFirstPageStream(AuthManager.getInstance().getUser().numeric_id, callback);
        }
    }

    private void optionFirstPageStream(final int numeric_id, Callback<ResponseBody> callback) {
        Call<ResponseBody> optionFirstPageStream = api.optionFirstPageStream(
                numeric_id,
                Mysplash.DEFAULT_PER_PAGE,
                BuildConfig.GET_STREAM_KEY,
                "unspecified");
        optionFirstPageStream.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onSucceed(ResponseBody responseBody) {
                requestFirstPageStream(numeric_id, callback);
            }

            @Override
            public void onFailed() {
                callback.onFailed();
            }
        });
        this.call = optionFirstPageStream;
        this.callback = callback;
    }

    private void requestFirstPageStream(int numeric_id, Callback<ResponseBody> callback) {
        Call<ResponseBody> getFirstPageStream = api.getFirstPageStream(
                numeric_id, Mysplash.DEFAULT_PER_PAGE, BuildConfig.GET_STREAM_KEY, "unspecified");
        getFirstPageStream.enqueue(callback);
        this.call = getFirstPageStream;
        this.callback = callback;
    }

    public void requestNextPageStream(final String nextPage, Callback<ResponseBody> callback) {
        Call<ResponseBody> optionNextPageStream = api.optionNextPageStream(nextPage);
        optionNextPageStream.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onSucceed(ResponseBody responseBody) {
                requestNextPage(nextPage, callback);
            }

            @Override
            public void onFailed() {
                callback.onFailed();
            }
        });
        this.call = optionNextPageStream;
        this.callback = callback;
    }

    private void requestNextPage(String nextPage, Callback<ResponseBody> callback) {
        Call<ResponseBody> getNextPageStream = api.getNextPageStream(nextPage);
        getNextPageStream.enqueue(callback);
        this.call = getNextPageStream;
        this.callback = callback;
    }

    public String getStreamUsablePart(String stream) {
        String result = "\"results\": ";
        return "stream_feed="
                + stream.substring(
                stream.indexOf(result) + result.length(),
                stream.lastIndexOf("]") + 1);
    }

    public void cancel() {
        if (callback != null) {
            callback.cancel();
        }
        if (call != null) {
            call.cancel();
        }
    }
}
