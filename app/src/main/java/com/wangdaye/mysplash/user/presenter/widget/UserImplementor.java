package com.wangdaye.mysplash.user.presenter.widget;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.entity.User;
import com.wangdaye.mysplash._common.data.service.UserService;
import com.wangdaye.mysplash._common.i.model.UserModel;
import com.wangdaye.mysplash._common.i.presenter.UserPresenter;
import com.wangdaye.mysplash._common.i.view.UserView;
import com.wangdaye.mysplash._common.ui.dialog.RateLimitDialog;

import retrofit2.Call;
import retrofit2.Response;

/**
 * User implementor.
 * */

public class UserImplementor
        implements UserPresenter {
    // model & view.
    private UserModel model;
    private UserView view;

    // data
    private OnRequestUserProfileListener listener;

    /** <br> life cycle. */

    public UserImplementor(UserModel model, UserView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void requestUser() {
        view.initRefreshStart();
        listener = new OnRequestUserProfileListener();
        model.getService().requestUserProfile(model.getUser().username, listener);
    }

    @Override
    public void cancelRequest() {
        if (listener != null) {
            listener.cancel();
        }
        model.getService().cancel();
    }

    @Override
    public void setUser(User u) {
        model.setUser(u);
    }

    @Override
    public User getUser() {
        return model.getUser();
    }

    /** <br> interface. */

    private class OnRequestUserProfileListener implements UserService.OnRequestUserProfileListener {
        // data
        private boolean canceled;

        OnRequestUserProfileListener() {
            canceled = false;
        }

        public void cancel() {
            canceled = true;
        }

        @Override
        public void onRequestUserProfileSuccess(Call<User> call, Response<User> response) {
            if (canceled) {
                return;
            }
            if (response.isSuccessful() && response.body() != null) {
                model.setUser(response.body());
                view.drawUserInfo(response.body());
                view.requestDetailsSuccess();
            } else if (Integer.parseInt(response.headers().get("X-Ratelimit-Remaining")) < 0) {
                RateLimitDialog dialog = new RateLimitDialog();
                dialog.show(
                        Mysplash.getInstance().getTopActivity().getFragmentManager(),
                        null);
            } else {
                requestUser();
            }
        }

        @Override
        public void onRequestUserProfileFailed(Call<User> call, Throwable t) {
            if (canceled) {
                return;
            }
            requestUser();
        }
    }
}
