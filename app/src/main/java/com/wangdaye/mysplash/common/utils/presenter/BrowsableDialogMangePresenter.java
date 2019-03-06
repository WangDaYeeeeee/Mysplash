package com.wangdaye.mysplash.common.utils.presenter;

import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.dialog.RequestBrowsableDataDialog;
import com.wangdaye.mysplash.common.ui.dialog.RetryDialog;

import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public class BrowsableDialogMangePresenter {

    @Nullable private RequestBrowsableDataDialog progress;
    @Nullable private RetryDialog retry;

    private State state;
    public enum State {
        LOADING, ERROR, SUCCESS
    }

    @Inject
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
            retry.show(activity.getSupportFragmentManager(), null);
        }
    }

    public void success() {
        state = State.ERROR;
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
        if (retry != null) {
            retry.dismiss();
            retry = null;
        }
    }
}
