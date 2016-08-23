package com.wangdaye.mysplash._common.i.view;

/**
 * Login view.
 * */

public interface LoginView {

    void onAuthCallback();
    void requestAccessTokenSuccess();
    void requestAccessTokenFailed();
}
