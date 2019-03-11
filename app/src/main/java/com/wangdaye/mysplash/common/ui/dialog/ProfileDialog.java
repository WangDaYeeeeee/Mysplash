package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.network.observer.BaseObserver;
import com.wangdaye.mysplash.common.network.service.UserService;
import com.wangdaye.mysplash.common.basic.fragment.MysplashDialogFragment;
import com.wangdaye.mysplash.common.utils.AnimUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Profile dialog.
 *
 * This dialog is used to show user's profile.
 *
 * */

public class ProfileDialog extends MysplashDialogFragment {

    @BindView(R.id.dialog_profile_container) CoordinatorLayout container;
    @BindView(R.id.dialog_profile_progress) CircularProgressView progressView;
    @BindView(R.id.dialog_profile_scrollView) NestedScrollView scrollView;
    @BindView(R.id.dialog_profile_text) TextView contentTxt;

    @OnClick(R.id.dialog_profile_enterBtn) void enter() {
        dismiss();
    }

    @Inject UserService service;
    private String username;

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_profile, null, false);
        ButterKnife.bind(this, view);
        initWidget();
        service.requestUserProfile(username, onRequestUserCallback);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    private void initWidget() {
        scrollView.setVisibility(View.GONE);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // interface.

    // on request user profile listener.

    private BaseObserver<User> onRequestUserCallback = new BaseObserver<User>() {

        @SuppressLint("SetTextI18n")
        @Override
        public void onSucceed(User user) {
            contentTxt.setText(
                    user.name + "\n\n"
                            + user.bio + "\n\n"
                            + user.total_photos + " " + getResources().getStringArray(R.array.user_tabs)[0] + "\n"
                            + user.total_collections + " " + getResources().getStringArray(R.array.user_tabs)[1] + "\n"
                            + user.total_likes + " " + getResources().getStringArray(R.array.user_tabs)[2] + "\n"
                            + user.followers_count + " " + getResources().getStringArray(R.array.my_follow_tabs)[0] + "\n"
                            + user.following_count + " " + getResources().getStringArray(R.array.my_follow_tabs)[1]);

            AnimUtils.animShow(scrollView);
            AnimUtils.animHide(progressView);
        }

        @Override
        public void onFailed() {
            if (!TextUtils.isEmpty(username)) {
                service.requestUserProfile(username, this);
            }
        }
    };
}