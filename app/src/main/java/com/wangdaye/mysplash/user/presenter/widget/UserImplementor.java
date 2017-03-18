package com.wangdaye.mysplash.user.presenter.widget;

import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common.data.service.FollowingService;
import com.wangdaye.mysplash._common.data.service.UserService;
import com.wangdaye.mysplash._common.i.model.UserModel;
import com.wangdaye.mysplash._common.i.presenter.UserPresenter;
import com.wangdaye.mysplash._common.i.view.UserView;

import okhttp3.ResponseBody;
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
    private OnRequestUserProfileListener requestUserProfileListener;
    private OnFollowListener followListener;

    /** <br> life cycle. */

    public UserImplementor(UserModel model, UserView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void requestUser() {
        view.initRefreshStart();
        requestUserProfileListener = new OnRequestUserProfileListener();
        model.getUserService().requestUserProfile(model.getUser().username, requestUserProfileListener);
    }

    @Override
    public void followUser() {
        followListener = new OnFollowListener();
        model.getFollowingService().setFollowUser(model.getUser().username, true, followListener);
    }

    @Override
    public void cancelFollowUser() {
        followListener = new OnFollowListener();
        model.getFollowingService().setFollowUser(model.getUser().username, true, followListener);
    }

    @Override
    public void cancelRequest() {
        if (requestUserProfileListener != null) {
            requestUserProfileListener.cancel();
        }
        if (followListener != null) {
            followListener.cancel();
        }
        model.getUserService().cancel();
        model.getFollowingService().cancel();
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

    // on request user profile swipeListener.

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
            if (response.isSuccessful()) {
                model.setUser(response.body());
                view.drawUserInfo(response.body());
                view.requestDetailsSuccess();
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

    // on follow swipeListener.

    private class OnFollowListener implements FollowingService.OnFollowListener {
        // data
        private boolean canceled;

        OnFollowListener() {
            canceled = false;
        }

        public void cancel() {
            canceled = true;
        }

        @Override
        public void onFollowSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (canceled) {
                return;
            }
            if (response.isSuccessful()) {
                view.followRequestSuccess(true);
            } else {
                view.followRequestFailed(true);
            }
        }

        @Override
        public void onCancelFollowSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (canceled) {
                return;
            }
            if (response.isSuccessful()) {
                view.followRequestSuccess(false);
            } else {
                view.followRequestFailed(false);
            }
        }

        @Override
        public void onFollowFailed(Call<ResponseBody> call, Throwable t) {
            if (canceled) {
                return;
            }
            view.followRequestFailed(true);
        }

        @Override
        public void onCancelFollowFailed(Call<ResponseBody> call, Throwable t) {
            view.followRequestFailed(false);
        }
    }

}
