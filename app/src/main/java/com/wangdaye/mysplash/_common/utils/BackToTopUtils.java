package com.wangdaye.mysplash._common.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.ui.activity.SettingsActivity;

/**
 * Back to top utils.
 * */

public class BackToTopUtils {

    /** <br> data. */

    public static boolean isSetBackToTop(boolean home) {
        if (home) {
            return !Mysplash.getInstance().getBackToTopType().equals("none");
        } else {
            return Mysplash.getInstance().getBackToTopType().equals("all");
        }
    }

    /** <br> UI. */

    private static void showSetBackToTopSnackbar() {
        if (!Mysplash.getInstance().isNotifiedSetBackToTop()) {
            final Context c = Mysplash.getInstance().getTopActivity();
            SharedPreferences.Editor editor = Mysplash.getInstance()
                    .getSharedPreferences(
                            Mysplash.PREFERENCE_BACK_TO_TOP,
                            Mysplash.MODE_PRIVATE)
                    .edit();
            editor.putBoolean(
                    c.getString(R.string.key_notified_set_back_to_top),
                    true);
            editor.apply();

            Mysplash.getInstance().setNotifiedSetBackToTop();

            NotificationUtils.showActionSnackbar(
                    c.getString(R.string.feedback_notify_set_back_to_top),
                    c.getString(R.string.set),
                    Snackbar.LENGTH_LONG,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent s = new Intent(c, SettingsActivity.class);
                            c.startActivity(s);
                        }
                    });
        }
    }

    public static void scrollToTop(RecyclerView recyclerView) {
        int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();
        if (firstVisibleItem > 5) {
            recyclerView.scrollToPosition(5);
        }
        recyclerView.smoothScrollToPosition(0);

        if (!Mysplash.getInstance().isNotifiedSetBackToTop()) {
            BackToTopUtils.showSetBackToTopSnackbar();
        }
    }

    public static void showTopBar(AppBarLayout topBar, View contentView) {
        if (topBar.getY() < 0) {
            ShowTopBarAnim topBarAnim = new ShowTopBarAnim(topBar);
            topBarAnim.setDuration(300);
            topBarAnim.setAnimationListener(new ShowTopBarListener(topBar));

            ShowContentAnim contentAnim = new ShowContentAnim(topBar, contentView);
            contentAnim.setDuration(300);

            topBar.clearAnimation();
            contentView.clearAnimation();

            topBar.startAnimation(topBarAnim);
            contentView.startAnimation(contentAnim);
        }
    }

    /** <br> inner class. */

    private static class ShowTopBarAnim extends Animation {
        // widget
        private View topBar;

        // data
        private float topBarStartY;

        // life cycle.

        ShowTopBarAnim(View topBar) {
            this.topBar = topBar;
            this.topBarStartY = topBar.getY();
        }

        // parent methods.

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            topBar.setY(topBarStartY * (1 - interpolatedTime));
        }
    }

    private static class ShowContentAnim extends Animation {
        // widget
        private View content;

        // data
        private float contentStartY;
        private float contentEndY;

        // life cycle.

        ShowContentAnim(View topBar, View content) {
            this.content = content;
            this.contentStartY = content.getY();
            this.contentEndY = topBar.getMeasuredHeight();
        }

        // parent methods.

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            content.setY(contentStartY + (contentEndY - contentStartY) * interpolatedTime);
        }
    }

    private static class ShowTopBarListener implements Animation.AnimationListener {
        // widget
        private AppBarLayout topBar;

        // life cycle.

        ShowTopBarListener(AppBarLayout bar) {
            topBar = bar;
        }

        // interface.

        @Override
        public void onAnimationStart(Animation animation) {
            // do nothing.
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) topBar.getLayoutParams();
            params.setBehavior(new AppBarLayout.Behavior());
            topBar.setLayoutParams(params);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // do nothing.
        }
    }
}
