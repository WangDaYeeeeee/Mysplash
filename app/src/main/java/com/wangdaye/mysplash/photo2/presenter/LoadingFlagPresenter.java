package com.wangdaye.mysplash.photo2.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.animation.DecelerateInterpolator;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.ui.widget.CircularProgressIcon;

/**
 * Loading flag presenter.
 *
 * This class is used to manage the loading flag icon.
 *
 * */

public class LoadingFlagPresenter {

    private CircularProgressIcon loadingFLag;
    private AnimatorSet flagAnim;

    public LoadingFlagPresenter(@NonNull CircularProgressIcon loadingFLag) {
        this.loadingFLag = loadingFLag;

        this.flagAnim = null;
    }

    public void setLoadingState(boolean init) {
        if (flagAnim != null && flagAnim.isStarted()) {
            flagAnim.cancel();
            flagAnim = null;
        }
        loadingFLag.setAlpha(1);
        loadingFLag.setEnabled(true);
        if (init) {
            loadingFLag.forceSetProgressState();
        } else {
            loadingFLag.setProgressState();
        }
    }

    public void setSucceedState() {
        loadingFLag.setResultState(R.drawable.ic_item_state_succeed);

        flagAnim = new AnimatorSet();
        flagAnim.setDuration(300);
        flagAnim.setInterpolator(new DecelerateInterpolator());
        flagAnim.setStartDelay(1000);
        flagAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                loadingFLag.setEnabled(false);
            }
        });
        flagAnim.play(ObjectAnimator.ofFloat(loadingFLag, "alpha", loadingFLag.getAlpha(), 0));
        flagAnim.start();
    }

    public void setFailedState() {
        loadingFLag.setResultState(R.drawable.ic_item_state_error);
        loadingFLag.setAlpha(1);
        loadingFLag.setEnabled(true);
    }
}
