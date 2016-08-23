package com.wangdaye.mysplash.login.presenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.data.AccessToken;
import com.wangdaye.mysplash._common.data.service.AuthorizeService;
import com.wangdaye.mysplash._common.data.tools.AuthManager;
import com.wangdaye.mysplash._common.i.model.LoginModel;
import com.wangdaye.mysplash._common.i.presenter.LoginPresenter;
import com.wangdaye.mysplash._common.i.view.LoginView;
import com.wangdaye.mysplash._common.ui.toast.MaterialToast;
import com.wangdaye.mysplash._common.utils.LinkUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Login implementor.
 * */

public class LoginImplementor
        implements LoginPresenter,
        AuthorizeService.OnRequestAccessTokenListener {
    // model & view.
    private LoginModel model;
    private LoginView view;

    /** <br> life cycle. */

    public LoginImplementor(LoginModel model, LoginView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void showAuthorizeView(Context c) {
        LinkUtils.accessLink(c, Mysplash.UNSPLASH_LOGIN_URL);
    }

    @Override
    public void checkAuthCallback(Intent intent) {
        if (intent != null
                && intent.getData() != null
                && !TextUtils.isEmpty(intent.getData().getAuthority())
                && Mysplash.UNSPLASH_LOGIN_CALLBACK.equals(intent.getData().getAuthority())) {
            requestAccessToken(intent.getData().getQueryParameter("code"));
            view.onAuthCallback();
        }
    }

    @Override
    public void requestAccessToken(String code) {
        model.getAuthService().requestAccessToken(code, this);
    }

    @Override
    public void cancelRequest() {
        model.getAuthService().cancel();
    }

    /** <br> interface. */

    @Override
    public void onRequestAccessTokenSuccess(Call<AccessToken> call, Response<AccessToken> response) {
        if (response.isSuccessful() && response.body() != null) {
            AuthManager.getInstance().writeAccessToken(response.body());
            view.requestAccessTokenSuccess();
        } else {
            Log.d("LOGIN IMP", response.message());
            view.requestAccessTokenFailed();
        }
    }

    @Override
    public void onRequestAccessTokenFailed(Call<AccessToken> call, Throwable t) {
        Context c = Mysplash.getInstance().getActivityList().get(0);
        MaterialToast.makeText(
                c,
                c.getString(R.string.feedback_request_token_failed),
                null,
                MaterialToast.LENGTH_SHORT).show();
        view.requestAccessTokenFailed();
    }
}