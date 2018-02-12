package com.wangdaye.mysplash.common.data.service;

import com.wangdaye.mysplash.BuildConfig;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common._basic.TLSCompactService;
import com.wangdaye.mysplash.common.data.api.GetStreamApi;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Get stream service.
 * */

public class GetStreamService extends TLSCompactService {
    // widget
    private Call call;

    public static GetStreamService getService() {
        return new GetStreamService();
    }

    private OkHttpClient buildClient() {
        return getClientBuilder()
                .build();
    }

    private GetStreamApi buildApi(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Mysplash.STREAM_API_BASE_URL)
                .client(client)
                .build()
                .create((GetStreamApi.class));
    }

    public void requestFirstPageStream(final OnRequestStreamListener l) {
        if (AuthManager.getInstance().isAuthorized()
                && AuthManager.getInstance().getNumericId() >= 0) {
            optionFirstPageStream(AuthManager.getInstance().getNumericId(), l);
        }
    }

    private void optionFirstPageStream(final int numeric_id, final OnRequestStreamListener l) {
        Call<ResponseBody> optionFirstPageStream = buildApi(buildClient())
                .optionFirstPageStream(
                        numeric_id,
                        Mysplash.DEFAULT_PER_PAGE,
                        BuildConfig.GET_STREAM_KEY,
                        "unspecified");
        optionFirstPageStream.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code() / 100 == 2) {
                    requestFirstPageStream(numeric_id, l);
                } else {
                    if (l != null) {
                        l.onRequestEnrichSucceed(call, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (l != null) {
                    l.onRequestEnrichFailed(call, t);
                }
            }
        });
        call = optionFirstPageStream;
    }

    private void requestFirstPageStream(int numeric_id, final OnRequestStreamListener l) {
        Call<ResponseBody> getFirstPageStream = buildApi(buildClient())
                .getFirstPageStream(
                        numeric_id, Mysplash.DEFAULT_PER_PAGE, BuildConfig.GET_STREAM_KEY, "unspecified");
        getFirstPageStream.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
        call = getFirstPageStream;
    }

    public void requestNextPageStream(final String nextPage, final OnRequestStreamListener l) {
        Call<ResponseBody> optionNextPageStream = buildApi(buildClient()).optionNextPageStream(nextPage);
        optionNextPageStream.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code() / 100 == 2) {
                    requestNextPage(nextPage, l);
                } else {
                    if (l != null) {
                        l.onRequestEnrichSucceed(call, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (l != null) {
                    l.onRequestEnrichFailed(call, t);
                }
            }
        });
        call = optionNextPageStream;
    }

    private void requestNextPage(String nextPage, final OnRequestStreamListener l) {
        Call<ResponseBody> getNextPageStream = buildApi(buildClient()).getNextPageStream(nextPage);
        getNextPageStream.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
        call = getNextPageStream;
    }

    public String getStreamUsablePart(String stream) {
        String result = "\"results\": ";
        return "stream_feed="
                + stream.substring(
                stream.indexOf(result) + result.length(),
                stream.lastIndexOf("]") + 1);
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

    // interface.

    public interface OnRequestStreamListener {
        void onRequestEnrichSucceed(Call<ResponseBody> call, Response<ResponseBody> response);
        void onRequestEnrichFailed(Call<ResponseBody> call, Throwable t);
    }
}
