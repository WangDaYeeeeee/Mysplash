package com.wangdaye.common.presenter;

import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.ui.dialog.RequestBrowsableDataDialog;
import com.wangdaye.common.ui.dialog.RetryDialog;

import org.jetbrains.annotations.Nullable;

public abstract class BrowsableDialogMangePresenter {

    @Nullable private RequestBrowsableDataDialog progress;
    @Nullable private RetryDialog retry;

    private State state;
    public enum State {
        LOADING, ERROR, SUCCESS
    }

    public BrowsableDialogMangePresenter() {
        state = State.SUCCESS;
    }

    public State getState() {
        return state;
    }

    public void load(MysplashActivity activity) {
        state = State.LOADING;
        if (progress == null) {
            progress = new RequestBrowsableDataDialog();
            progress.setOnBackPressedListener(this::finishActivity);
            progress.show(activity.getSupportFragmentManager(), null);
        }
        if (retry != null) {
            retry.dismiss();
            retry = null;
        }
    }

    public void error(MysplashActivity activity,
                      @Nullable RetryDialog.OnRetryListener l) {
        state = State.ERROR;
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
        if (retry == null) {
            retry = new RetryDialog();
            retry.setOnRetryListener(l);
            retry.setOnBackPressedListener(this::finishActivity);
            retry.show(activity.getSupportFragmentManager(), null);
        }
    }

    public void success() {
        state = State.SUCCESS;
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
        if (retry != null) {
            retry.dismiss();
            retry = null;
        }
    }

    public abstract void finishActivity();
}
