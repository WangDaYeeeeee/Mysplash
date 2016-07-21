package com.wangdaye.mysplash.data.unslpash.service;

import com.google.gson.GsonBuilder;
import com.wangdaye.mysplash.data.constant.Mysplash;
import com.wangdaye.mysplash.data.unslpash.api.UserApi;
import com.wangdaye.mysplash.data.unslpash.model.User;
import com.wangdaye.mysplash.data.unslpash.tools.AuthInterceptor;
import com.wangdaye.mysplash.data.unslpash.tools.ClientInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * User service.
 * */

public class UserService {
    // data
    private OkHttpClient client;
    private Call call;

    /** <br> data. */

    public void requestUser(String username, int w, int h, final OnRequestUserListener l) {
        UserApi api = new Retrofit.Builder()
                .baseUrl(Mysplash.BASE_URL)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder()
                                        .setDateFormat(Mysplash.DATE_FORMAT)
                                        .create()))
                .build()
                .create((UserApi.class));

        Call<User> getUser = api.getUser(username, w, h);
        getUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                if (l != null) {
                    l.onRequestUserSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (l != null) {
                    l.onRequestUserFailed(call, t);
                }
            }
        });
        call = getUser;
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

    /** <br> build. */

    public static UserService getService() {
        return new UserService();
    }

    public UserService buildClient(String token) {
        this.client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(token))
                .build();
        return this;
    }

    public UserService buildClient() {
        this.client = new OkHttpClient.Builder()
                .addInterceptor(new ClientInterceptor())
                .build();
        return this;
    }

    /** <br> interface. */

    public interface OnRequestUserListener {
        void onRequestUserSuccess(Call<User> call, retrofit2.Response<User> response);
        void onRequestUserFailed(Call<User> call, Throwable t);
    }
}
