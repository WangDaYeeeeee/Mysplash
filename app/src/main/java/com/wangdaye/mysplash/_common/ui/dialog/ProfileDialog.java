package com.wangdaye.mysplash._common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common.data.service.UserService;
import com.wangdaye.mysplash._common.ui._basic.MysplashDialogFragment;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Profile dialog.
 * */

public class ProfileDialog extends MysplashDialogFragment
        implements View.OnClickListener, UserService.OnRequestUserProfileListener {
    // widget
    private CoordinatorLayout container;
    private CircularProgressView progressView;
    private TextView contentTxt;

    // data
    private UserService service;
    private String username;

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_profile, null, false);
        initData();
        initWidget(view);
        service.requestUserProfile(username, this);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    /** <br> UI. */

    private void initWidget(View v) {
        this.container = (CoordinatorLayout) v.findViewById(R.id.dialog_profile_container);

        this.progressView = (CircularProgressView) v.findViewById(R.id.dialog_profile_progress);

        this.contentTxt = (TextView) v.findViewById(R.id.dialog_profile_text);
        contentTxt.setAlpha(0);
        DisplayUtils.setTypeface(getActivity(), contentTxt);

        v.findViewById(R.id.dialog_profile_enterBtn).setOnClickListener(this);
    }

    /** <br> data. */

    private void initData() {
        this.service = UserService.getService();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_profile_enterBtn:
                dismiss();
                break;
        }
    }

    // on request user profile listener.

    @Override
    public void onRequestUserProfileSuccess(Call<User> call, Response<User> response) {
        if (response.isSuccessful() && response.body() != null) {
            User user = response.body();
            contentTxt.setText(
                    user.total_photos + " " + getResources().getStringArray(R.array.user_tabs)[0] + "\n"
                            + user.total_collections + " " + getResources().getStringArray(R.array.user_tabs)[1] + "\n"
                            + user.total_likes + " " + getResources().getStringArray(R.array.user_tabs)[2] + "\n"
                            + user.followers_count + " " + getResources().getStringArray(R.array.my_follow_tabs)[0] + "\n"
                            + user.following_count + " " + getResources().getStringArray(R.array.my_follow_tabs)[1]);

            AlphaAnimation textAnim = new AlphaAnimation(contentTxt, 0, 1);
            textAnim.setDuration(200);
            contentTxt.startAnimation(textAnim);

            AlphaAnimation progressAnim = new AlphaAnimation(progressView, 1, 0);
            progressAnim.setDuration(200);
            progressView.startAnimation(progressAnim);
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

    /** <br> inner class. */

    private class AlphaAnimation extends Animation {
        // widget
        private View target;

        // data
        private float from;
        private float to;

        AlphaAnimation(View target, float from, float to) {
            this.target = target;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            target.setAlpha(from + (to - from) * interpolatedTime);
        }
    }
}