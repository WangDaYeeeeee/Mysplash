package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.data.service.network.UserService;
import com.wangdaye.mysplash.common.basic.fragment.MysplashDialogFragment;
import com.wangdaye.mysplash.common.utils.AnimUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Profile dialog.
 *
 * This dialog is used to show user's profile.
 *
 * */

public class ProfileDialog extends MysplashDialogFragment
        implements UserService.OnRequestUserProfileListener {

    @BindView(R.id.dialog_profile_container)
    CoordinatorLayout container;

    @BindView(R.id.dialog_profile_progress)
    CircularProgressView progressView;

    @BindView(R.id.dialog_profile_scrollView)
    NestedScrollView scrollView;

    @BindView(R.id.dialog_profile_text)
    TextView contentTxt;

    private UserService service;
    private String username;

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_profile, null, false);
        ButterKnife.bind(this, view);
        initData();
        initWidget();
        service.requestUserProfile(username, this);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    private void initData() {
        this.service = UserService.getService();
    }

    private void initWidget() {
        scrollView.setVisibility(View.GONE);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // interface.

    // on click listener.

    @OnClick(R.id.dialog_profile_enterBtn) void enter() {
        dismiss();
    }

    // on request user profile listener.

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestUserProfileSuccess(Call<User> call, Response<User> response) {
        if (response.isSuccessful() && response.body() != null) {
            User user = response.body();
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
        } else {
            service.requestUserProfile(username, this);
        }
    }

    @Override
    public void onRequestUserProfileFailed(Call<User> call, Throwable t) {
        if (!TextUtils.isEmpty(username)) {
            service.requestUserProfile(username, this);
        }
    }
}