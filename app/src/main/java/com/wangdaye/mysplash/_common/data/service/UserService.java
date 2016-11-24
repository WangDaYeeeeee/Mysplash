package com.wangdaye.mysplash._common.data.service;

import com.google.gson.GsonBuilder;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.api.UserApi;
import com.wangdaye.mysplash._common.data.entity.unsplash.Me;
import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common.utils.widget.AuthInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * User service.
 * */

public class UserService {
    // widget
    private Call call;

    /** <br> data. */

    public void requestUserProfile(String username, final OnRequestUserProfileListener l) {
        Call<User> getUserProfile = buildApi(buildClient()).getUserProfile(username, 128, 128);
        getUserProfile.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (l != null) {
                    l.onRequestUserProfileSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (l != null) {
                    l.onRequestUserProfileFailed(call, t);
                }
            }
        });
        call = getUserProfile;
    }

    public void requestMeProfile(final OnRequestMeProfileListener l) {
        Call<Me> getMeProfile = buildApi(buildClient()).getMeProfile();
        getMeProfile.enqueue(new Callback<Me>() {
            @Override
            public void onResponse(Call<Me> call, Response<Me> response) {
                if (l != null) {
                    l.onRequestMeProfileSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<Me> call, Throwable t) {
                if (l != null) {
                    l.onRequestMeProfileFailed(call, t);
                }
            }
        });
        call = getMeProfile;
    }

    public void updateMeProfile(String username, String first_name, String last_name,
                                String email, String url, String location, String bio,
                                final OnRequestMeProfileListener l) {
        Call<Me> updateMeProfile = buildApi(buildClient()).updateMeProfile(
                username, first_name, last_name,
                email, url, location, bio);
        updateMeProfile.enqueue(new Callback<Me>() {
            @Override
            public void onResponse(Call<Me> call, Response<Me> response) {
                if (l != null) {
                    l.onRequestMeProfileSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<Me> call, Throwable t) {
                if (l != null) {
                    l.onRequestMeProfileFailed(call, t);
                }
            }
        });
        call = updateMeProfile;
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

    private OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .build();
    }

    private UserApi buildApi(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Mysplash.UNSPLASH_API_BASE_URL)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder()
                                        .setDateFormat(Mysplash.DATE_FORMAT)
                                        .create()))
                .build()
                .create((UserApi.class));
    }

    /** <br> interface. */

    public interface OnRequestUserProfileListener {
        void onRequestUserProfileSuccess(Call<User> call, Response<User> response);
        void onRequestUserProfileFailed(Call<User> call, Throwable t);
    }

    public interface OnRequestMeProfileListener {
        void onRequestMeProfileSuccess(Call<Me> call, Response<Me> response);
        void onRequestMeProfileFailed(Call<Me> call, Throwable t);
    }
}
