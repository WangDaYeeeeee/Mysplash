package com.wangdaye.mysplash.common.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.model.Previewable;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.singleOrientationScrollView.NestedScrollPhotoView;
import com.wangdaye.mysplash.common.image.ImageHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Preview activity.
 *
 * This activity is used to preview a picture.
 *
 * */

public class PreviewActivity extends MysplashActivity
        implements SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R.id.activity_preview_container) CoordinatorLayout container;
    @BindView(R.id.activity_preview_widgetContainer) LinearLayout widgetContainer;
    @BindView(R.id.activity_preview_iconContainer) LinearLayout iconContainer;

    private Previewable previewable; // this object will provide data for picture.
    private boolean showIcon = false; // If set true, the icon view will become visible when user tap picture.
    private boolean showingIcon = false; // If set true, it means the icon view is visible.

    public static final String KEY_PREVIEW_ACTIVITY_PREVIEW = "preview_activity_preview";
    public static final String KEY_PREVIEW_ACTIVITY_SHOW_ICON = "preview_activity_show_icon";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        initData();
        initWidget();
    }

    @Override
    protected void setTheme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }

    @Override
    protected boolean operateStatusBarBySelf() {
        return true;
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    public void finishSelf(boolean backPressed) {
        finish();
        if (backPressed) {
            overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
        } else {
            overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
        }
    }

    @Override
    public void handleBackPressed() {
        finishSelf(true);
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initData() {
        this.previewable = getIntent().getParcelableExtra(KEY_PREVIEW_ACTIVITY_PREVIEW);
        this.showIcon = getIntent().getBooleanExtra(KEY_PREVIEW_ACTIVITY_SHOW_ICON, false);
    }

    private void initWidget() {
        SwipeBackCoordinatorLayout swipeBackView = findViewById(R.id.activity_preview_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        final NestedScrollPhotoView photoView = findViewById(R.id.activity_preview_photoView);
        photoView.setMaxScale(getMaxiScale(false));
        if (previewable instanceof Photo) {
            photoView.setScaleType(AppCompatImageView.ScaleType.FIT_CENTER);
            ImageHelper.loadRegularPhoto(this, photoView, (Photo) previewable, null);
        } else {
            ImageHelper.loadImageFromUrl(
                    this, photoView, previewable.getFullUrl(), false, null);
        }
    }

    // control.

    private void showIcons() {
        TranslateAnimation show = new TranslateAnimation(
                0, 0,
                0, -iconContainer.getMeasuredHeight()
        );
        show.setFillEnabled(true);
        show.setFillAfter(true);
        show.setDuration(200);
        iconContainer.clearAnimation();
        iconContainer.startAnimation(show);
    }

    private void hideIcons() {
        TranslateAnimation hide = new TranslateAnimation(
                0, 0,
                -iconContainer.getMeasuredHeight(), 0
        );
        hide.setFillEnabled(true);
        hide.setFillAfter(true);
        hide.setDuration(200);
        iconContainer.clearAnimation();
        iconContainer.startAnimation(hide);
    }

    private void showWidget() {
        TranslateAnimation show = new TranslateAnimation(
                0, 0,
                0, widgetContainer.getMeasuredHeight()
        );
        show.setFillEnabled(true);
        show.setFillAfter(true);
        show.setDuration(200);
        widgetContainer.clearAnimation();
        widgetContainer.startAnimation(show);
    }

    private void hideWidget() {
        TranslateAnimation hide = new TranslateAnimation(
                0, 0,
                widgetContainer.getMeasuredHeight(), 0
        );
        hide.setFillEnabled(true);
        hide.setFillAfter(true);
        hide.setDuration(200);
        widgetContainer.clearAnimation();
        widgetContainer.startAnimation(hide);
    }

    private float getMaxiScale(boolean fullSize) {
        if (previewable instanceof Photo) {
            if (fullSize) {
                double scaleRatio = 0.7 * Math.max(
                        getResources().getDisplayMetrics().widthPixels,
                        getResources().getDisplayMetrics().heightPixels
                ) / Math.min(previewable.getWidth(), previewable.getHeight());
                return getMaxiScale(
                        (int) (scaleRatio * previewable.getWidth()),
                        (int) (scaleRatio * previewable.getHeight())
                );
            } else {
                return getMaxiScale(
                        1080,
                        (int) (1080.0 * previewable.getHeight() / previewable.getWidth())
                );
            }
        } else {
            return getMaxiScale(previewable.getWidth(), previewable.getHeight());
        }
    }

    private float getMaxiScale(int w, int h) {
        int maxWidth;
        int normalWidth;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        if (previewable instanceof Photo) {
            if (1.0 * w / h >= 1.0 * screenWidth / screenHeight) {
                maxWidth = (int) (2.0 * screenHeight * w / h);
            } else {
                maxWidth = (int) (2.0 * screenWidth);
            }
            normalWidth = Math.min(getResources().getDisplayMetrics().widthPixels, w);
        } else {
            maxWidth = (int) (0.5 * Math.min(screenWidth, screenHeight));
            normalWidth = w;
        }
        if (maxWidth > normalWidth) {
            return (float) (1.0 * maxWidth / normalWidth);
        } else {
            return 1;
        }
    }

    // interface.

    // on click listener.

    @OnClick(R.id.activity_preview_photoView) void tapPicture() {
        if (showIcon) {
            if (showingIcon) {
                showingIcon = false;
                hideWidget();
                hideIcons();
            } else {
                showingIcon = true;
                showWidget();
                showIcons();
            }
        }
    }

    // on long click listener.

    @OnLongClick(R.id.activity_preview_photoView) boolean longClickPicture() {
        // TODO: 2017/3/31 download.
        return true;
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        return true;
    }

    @Override
    public void onSwipeProcess(float percent) {
        container.setBackgroundColor(
                Color.argb((int) (255 * 0.5 * (2 - percent)), 0, 0, 0)
        );
    }

    @Override
    public void onSwipeFinish(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        finishSelf(false);
    }
}