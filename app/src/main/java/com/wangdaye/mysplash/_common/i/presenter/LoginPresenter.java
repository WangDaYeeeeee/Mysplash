package com.wangdaye.mysplash._common.i.presenter;

import android.content.Context;
import android.content.Intent;

/**
 * Login presenter.
 * */

public interface LoginPresenter {

    void showAuthorizeView(Context c);
    void checkAuthCallback(Intent intent);

    void requestAccessToken(String code);
    void cancelRequest();
}
