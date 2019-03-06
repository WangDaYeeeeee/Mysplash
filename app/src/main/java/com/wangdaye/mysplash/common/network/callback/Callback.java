package com.wangdaye.mysplash.common.network.callback;

import retrofit2.Call;
import retrofit2.Response;

public abstract class Callback<T> implements retrofit2.Callback<T> {

    private boolean canceled;

    public Callback() {
        canceled = false;
    }

    public void cancel() {
        canceled = true;
    }

    public abstract void onSucceed(T t);

    public abstract void onFailed();

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (canceled) {
            return;
        }
        if (response.isSuccessful() && response.body() != null) {
            onSucceed(response.body());
        } else {
            onFailed();
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (canceled) {
            return;
        }
        onFailed();
    }
}
