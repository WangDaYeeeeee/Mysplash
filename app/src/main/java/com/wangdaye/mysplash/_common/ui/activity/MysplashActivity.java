package com.wangdaye.mysplash._common.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Mysplash Activity
 * */

public abstract class MysplashActivity extends AppCompatActivity {
    // data.
    private boolean started = false;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        Mysplash.getInstance().addActivity(this);
        DisplayUtils.setStatusBarTextDark(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Mysplash.getInstance().removeActivity();
    }

    /** <br> UI. */

    public void animShowView(final View v, int delay) {
        v.setVisibility(View.INVISIBLE);
        DisplayUtils utils = new DisplayUtils(this);
        ObjectAnimator anim = ObjectAnimator
                .ofFloat(v, "translationY", utils.dpToPx(72), 0)
                .setDuration(300);

        anim.setInterpolator(new DecelerateInterpolator());
        anim.setStartDelay(delay);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                v.setVisibility(View.VISIBLE);
            }
        });
        anim.start();
    }

    /** <br> data. */

    public void setStarted() {
        started = true;
    }

    public boolean isStarted() {
        return started;
    }

    protected abstract void setTheme();
}
