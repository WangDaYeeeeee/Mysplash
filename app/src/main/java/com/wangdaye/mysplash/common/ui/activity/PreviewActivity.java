package com.wangdaye.mysplash.common.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.Previewable;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollPhotoView;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

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

    @BindView(R.id.activity_preview_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_preview_widgetContainer)
    LinearLayout widgetContainer;

    @BindView(R.id.activity_preview_iconContainer)
    LinearLayout iconContainer;

    private Previewable previewable; // this object will provide data for picture.
    private boolean showIcon = false; // If set true, the icon view will become visible when user tap picture.
    private boolean showingIcon = false; // If set true, it means the icon view is visible.

    public static final String KEY_PREVIEW_ACTIVITY_PREVIEW = "preview_activity_preview";
    public static final String KEY_PREVIEW_ACTIVITY_SHOW_ICON = "preview_activity_show_icon";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            ButterKnife.bind(this);
            initData();
            initWidget();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Mysplash.getInstance().removeActivity(this);
    }

    @Override
    protected void setTheme() {
        if (ThemeManager.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Preview);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Preview);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }

    @Override
    protected boolean operateStatusBarBySelf() {
        return true;
    }

    @Override
    public void finishActivity(int dir) {
        finish();
        switch (dir) {
            case SwipeBackCoordinatorLayout.UP_DIR:
                overridePendingTransition(0, R.anim.activity_slide_out_top);
                break;

            case SwipeBackCoordinatorLayout.DOWN_DIR:
                overridePendingTransition(0, R.anim.activity_slide_out_bottom);
                break;
        }
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    public void handleBackPressed() {
        finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
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
        SwipeBackCoordinatorLayout swipeBackView = ButterKnife.findById(
                this, R.id.activity_preview_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        final NestedScrollPhotoView photoView = ButterKnife.findById(this, R.id.activity_preview_photoView);
        photoView.enable();
        photoView.enableRotate();
        photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        photoView.setMaxScale(calcMaxiScale());
        ImageHelper.loadFullPhoto(
                this, photoView, previewable.getFullUrl(), previewable.getRegularUrl(),
                new ImageHelper.OnLoadImageListener() {
            @Override
            public void onLoadSucceed() {
                photoView.setMaxScale(2 * calcMaxiScale());
            }

            @Override
            public void onLoadFailed() {
                // do nothing.
            }
        });
    }

    // control.

    private void showIcons() {
        TranslateAnimation show = new TranslateAnimation(
                0, 0,
                0, -iconContainer.getMeasuredHeight());
        show.setFillEnabled(true);
        show.setFillAfter(true);
        show.setDuration(200);
        iconContainer.clearAnimation();
        iconContainer.startAnimation(show);
    }

    private void hideIcons() {
        TranslateAnimation hide = new TranslateAnimation(
                0, 0,
                -iconContainer.getMeasuredHeight(), 0);
        hide.setFillEnabled(true);
        hide.setFillAfter(true);
        hide.setDuration(200);
        iconContainer.clearAnimation();
        iconContainer.startAnimation(hide);
    }

    private void showWidget() {
        TranslateAnimation show = new TranslateAnimation(
                0, 0,
                0, widgetContainer.getMeasuredHeight());
        show.setFillEnabled(true);
        show.setFillAfter(true);
        show.setDuration(200);
        widgetContainer.clearAnimation();
        widgetContainer.startAnimation(show);
    }

    private void hideWidget() {
        TranslateAnimation hide = new TranslateAnimation(
                0, 0,
                widgetContainer.getMeasuredHeight(), 0);
        hide.setFillEnabled(true);
        hide.setFillAfter(true);
        hide.setDuration(200);
        widgetContainer.clearAnimation();
        widgetContainer.startAnimation(hide);
    }

    private float calcMaxiScale() {
        if (previewable.getWidth() == 128) {
            return 0.5F;
        } else {
            return 5;
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
    public boolean canSwipeBack(int dir) {
        return true;
    }

    @Override
    public void onSwipeProcess(float percent) {
        container.setBackgroundColor(
                Color.argb(
                        (int) (255 * 0.5 * (2 - percent)),
                        0, 0, 0));
    }

    @Override
    public void onSwipeFinish(int dir) {
        finishActivity(dir);
    }
}