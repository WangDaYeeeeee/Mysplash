package com.wangdaye.mysplash.main.presenter.fragment;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.i.presenter.NotificationBarPresenter;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

/**
 * Home fragment notification bar implementor.
 * */

public class HomeFragmentNotificationBarImplementor
        implements NotificationBarPresenter {

    private boolean unread;

    private class HideAnimation extends Animation {

        private ImageButton bellBtn;
        private ImageView dot;

        private float fromScale;
        private float toScale;
        private float fromAlpha;

        private boolean ended;

        HideAnimation(final ImageButton bellBtn, final ImageView dot) {
            this.bellBtn = bellBtn;
            this.dot = dot;
            this.fromScale = bellBtn.getScaleX();
            this.toScale = (float) (fromScale * 0.5);
            this.fromAlpha = bellBtn.getAlpha();

            setDuration(150);
            setInterpolator(new AccelerateDecelerateInterpolator());
            setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    ended = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (!ended) {
                        ended = true;
                        bellBtn.clearAnimation();
                        bellBtn.startAnimation(new ShowAnimation(bellBtn, dot));
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // do nothing.
                }
            });
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float scale = fromScale + (toScale - fromScale) * interpolatedTime;
            float alpha = fromAlpha + (0 - fromAlpha) * interpolatedTime;
            bellBtn.setScaleX(scale);
            bellBtn.setScaleY(scale);
            bellBtn.setAlpha(alpha);
            dot.setAlpha(alpha);
        }
    }

    private class ShowAnimation extends Animation {

        private ImageButton bellBtn;
        private ImageView dot;

        ShowAnimation(ImageButton bellBtn, ImageView dot) {
            this.bellBtn = bellBtn;
            this.dot = dot;

            setDuration(150);
            setInterpolator(new AccelerateDecelerateInterpolator());
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float scale = (float) (0.5 + 0.5 * interpolatedTime);
            float alpha = 0 + 1 * interpolatedTime;
            bellBtn.setScaleX(scale);
            bellBtn.setScaleY(scale);
            bellBtn.setAlpha(alpha);
            dot.setAlpha(alpha);
        }
    }

    public HomeFragmentNotificationBarImplementor() {
        unread = false;
    }

    @Override
    public void setImage(ImageButton bellBtn, ImageView dot) {
        if (AuthManager.getInstance().getNotificationManager().hasUnseenNotification()) {
            if (!unread) {
                bellBtn.clearAnimation();
                bellBtn.startAnimation(new HideAnimation(bellBtn, dot));
            }
        } else {
            if (unread) {
                bellBtn.clearAnimation();
                bellBtn.startAnimation(new HideAnimation(bellBtn, dot));
            }
        }
    }

    @Override
    public void setVisible(ImageButton bellBtn, ImageView dot) {
        if (AuthManager.getInstance().isAuthorized()) {
            if (AuthManager.getInstance().getNotificationManager().hasUnseenNotification()) {
                unread = true;
                ThemeManager.setImageResource(
                        bellBtn, R.drawable.ic_bell_light, R.drawable.ic_bell_dark);
                dot.setImageResource(R.drawable.ic_unread);
            } else {
                unread = false;
                ThemeManager.setImageResource(
                        bellBtn, R.drawable.ic_bell_outline_light, R.drawable.ic_bell_outline_dark);
                dot.setImageResource(R.drawable.ic_read);
            }
            bellBtn.setAlpha(1F);
            bellBtn.setEnabled(true);
            dot.setAlpha(1F);
            dot.setEnabled(true);
        } else {
            bellBtn.setAlpha(0F);
            bellBtn.setEnabled(false);
            dot.setAlpha(0F);
            dot.setEnabled(false);
        }
    }
}
