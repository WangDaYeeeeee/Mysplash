package com.wangdaye.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.common.R;
import com.wangdaye.common.R2;
import com.wangdaye.common.base.dialog.MysplashDialogFragment;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.di.component.DaggerNetworkServiceComponent;
import com.wangdaye.common.network.observer.BaseObserver;
import com.wangdaye.common.network.service.UserService;
import com.wangdaye.common.utils.AnimUtils;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.nekocode.rxlifecycle.LifecycleEvent;
import cn.nekocode.rxlifecycle.compact.RxLifecycleCompact;
import io.reactivex.Emitter;
import io.reactivex.Observable;

/**
 * Profile dialog.
 *
 * This dialog is used to show user's profile.
 *
 * */

public class ProfileDialog extends MysplashDialogFragment {

    @BindView(R2.id.dialog_profile_container) CoordinatorLayout container;
    @BindView(R2.id.dialog_profile_progress) CircularProgressView progressView;
    @BindView(R2.id.dialog_profile_scrollView) NestedScrollView scrollView;
    @BindView(R2.id.dialog_profile_text) TextView contentTxt;

    @OnClick(R2.id.dialog_profile_enterBtn) void enter() {
        dismiss();
    }

    @Inject UserService service;
    private String username;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        DaggerNetworkServiceComponent.create().inject(this);
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_profile, null, false);
        ButterKnife.bind(this, view);
        initWidget();
        service.requestUserProfile(username, new ProfileCallback());
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

    private class ProfileCallback extends BaseObserver<User> {

        @SuppressLint("SetTextI18n")
        @Override
        public void onSucceed(User user) {
            if (getActivity() == null) {
                return;
            }

            contentTxt.setText(
                    user.name + "\n\n"
                            + user.bio + "\n\n"
                            + user.total_photos + " " + getResources().getStringArray(R.array.user_tabs)[0] + "\n"
                            + user.total_collections + " " + getResources().getStringArray(R.array.user_tabs)[1] + "\n"
                            + user.total_likes + " " + getResources().getStringArray(R.array.user_tabs)[2] + "\n"
                            + user.followers_count + " " + getResources().getStringArray(R.array.my_follow_tabs)[0] + "\n"
                            + user.following_count + " " + getResources().getStringArray(R.array.my_follow_tabs)[1]
            );

            AnimUtils.animShow(scrollView);
            AnimUtils.animHide(progressView);
        }

        @Override
        public void onFailed() {
            if (getActivity() == null) {
                return;
            }

            if (!TextUtils.isEmpty(username)) {
                Observable.create(Emitter::onComplete)
                        .compose(
                                RxLifecycleCompact.bind(ProfileDialog.this)
                                        .disposeObservableWhen(LifecycleEvent.DESTROY)
                        ).delay(2, TimeUnit.SECONDS)
                        .doOnComplete(() -> service.requestUserProfile(username, new ProfileCallback()))
                        .subscribe();
            }
        }
    }
}