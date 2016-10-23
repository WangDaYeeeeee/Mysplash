package com.wangdaye.mysplash._common.data.service;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.api.AuthorizeApi;
import com.wangdaye.mysplash._common.data.entity.AccessToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Authorize service.
 * */

public class AuthorizeService {
    // widget
    private Call call;

    /** <br> data. */

    public void requestAccessToken(String code, final OnRequestAccessTokenListener l) {
        Call<AccessToken> getAccessToken = buildApi()
                .getAccessToken(
                        Mysplash.APPLICATION_ID,
                        Mysplash.SECRET,
                        "mysplash://" + Mysplash.UNSPLASH_LOGIN_CALLBACK,
                        code,
                        "authorization_code");
        getAccessToken.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (l != null) {
                    l.onRequestAccessTokenSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                if (l != null) {
                    l.onRequestAccessTokenFailed(call, t);
                }
            }
        });
        call = getAccessToken;
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

    /** <br> build. */

    public static AuthorizeService getService() {
        return new AuthorizeService();
    }

    private AuthorizeApi buildApi() {
        return new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create((AuthorizeApi.class));
    }

    /** <br> interface. */

    public interface OnRequestAccessTokenListener {
        void onRequestAccessTokenSuccess(Call<AccessToken> call, retrofit2.Response<AccessToken> response);
        void onRequestAccessTokenFailed(Call<AccessToken> call, Throwable t);
    }
}
