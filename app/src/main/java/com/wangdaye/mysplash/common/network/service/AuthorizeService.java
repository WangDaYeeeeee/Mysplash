package com.wangdaye.mysplash.common.network.service;

import android.content.Context;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.api.AuthorizeApi;
import com.wangdaye.mysplash.common.network.json.AccessToken;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Authorize service.
 * */

public class AuthorizeService {

    private AuthorizeApi api;

    @Nullable private Call call;
    @Nullable private Callback callback;

    @Inject
    public AuthorizeService(OkHttpClient client, GsonConverterFactory factory) {
        api = new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_URL)
                .client(client)
                .addConverterFactory(factory)
                .build()
                .create(AuthorizeApi.class);
        call = null;
        callback = null;
    }

    public void requestAccessToken(Context c, String code, Callback<AccessToken> callback) {
        Call<AccessToken> getAccessToken = api.getAccessToken(
                Mysplash.getAppId(c, true),
                Mysplash.getSecret(c),
                "mysplash://" + Mysplash.UNSPLASH_LOGIN_CALLBACK,
                code,
                "authorization_code");
        getAccessToken.enqueue(callback);
        this.call = getAccessToken;
        this.callback = callback;
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
