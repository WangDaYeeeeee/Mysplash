package com.wangdaye.mysplash.common.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.ui.activity.SettingsActivity;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.SettingsOptionManager;

/**
 * Back to top utils.
 *
 * An utils class that can control list view to scroll to the top.
 *
 * */

public class BackToTopUtils {

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
        private NestedScrollAppBarLayout topBar;

        // life cycle.

        ShowTopBarListener(NestedScrollAppBarLayout bar) {
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
            params.setBehavior(new NestedScrollAppBarLayout.Behavior());
            topBar.setLayoutParams(params);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // do nothing.
        }
    }

    public static boolean isSetBackToTop(boolean home) {
        if (home) {
            return !SettingsOptionManager.getInstance(Mysplash.getInstance())
                    .getBackToTopType().equals("none");
        } else {
            return SettingsOptionManager.getInstance(Mysplash.getInstance())
                    .getBackToTopType().equals("all");
        }
    }

    private static void showSetBackToTopSnackbar() {
        if (!SettingsOptionManager.getInstance(Mysplash.getInstance())
                .isNotifiedSetBackToTop()) {
            final Context c = Mysplash.getInstance().getTopActivity();
            if (c != null) {
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(Mysplash.getInstance())
                        .edit();
                editor.putBoolean(
                        c.getString(R.string.key_notified_set_back_to_top),
                        true);
                editor.apply();

                SettingsOptionManager.getInstance(Mysplash.getInstance())
                        .setNotifiedSetBackToTop(Mysplash.getInstance(), true);

                NotificationHelper.showActionSnackbar(
                        c.getString(R.string.feedback_notify_set_back_to_top),
                        c.getString(R.string.set),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent s = new Intent(c, SettingsActivity.class);
                                c.startActivity(s);
                            }
                        });
            }
        }
    }

    public static void scrollToTop(RecyclerView recyclerView) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        int firstVisibleItemPosition = 0;
        if (manager instanceof LinearLayoutManager) {
            firstVisibleItemPosition = getFirstVisibleItemPosition((LinearLayoutManager) manager);
        } else if (manager instanceof StaggeredGridLayoutManager) {
            firstVisibleItemPosition = getFirstVisibleItemPosition((StaggeredGridLayoutManager) manager);
        }
        if (firstVisibleItemPosition > 5) {
            recyclerView.scrollToPosition(5);
        }
        recyclerView.smoothScrollToPosition(0);

        if (!SettingsOptionManager.getInstance(Mysplash.getInstance())
                .isNotifiedSetBackToTop()) {
            BackToTopUtils.showSetBackToTopSnackbar();
        }
    }

    private static int getFirstVisibleItemPosition(LinearLayoutManager manager) {
        return manager.findFirstVisibleItemPosition();
    }

    private static int getFirstVisibleItemPosition(StaggeredGridLayoutManager manager) {
        return manager.findFirstVisibleItemPositions(null)[0];
    }

    /**
     * Expand top bar, like {@link NestedScrollAppBarLayout}.
     *
     * @param topBar      The top bar that needs to be expand.
     * @param contentView Content view with the top bar.
     * */
    public static void showTopBar(NestedScrollAppBarLayout topBar, View contentView) {
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
}
