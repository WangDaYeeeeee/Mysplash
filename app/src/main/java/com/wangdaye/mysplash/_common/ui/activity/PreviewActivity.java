package com.wangdaye.mysplash._common.ui.activity;

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
import com.wangdaye.mysplash._common._basic.Previewable;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.ui.widget.nestedScrollView.NestedScrollPhotoView;
import com.wangdaye.mysplash._common.utils.helper.ImageHelper;

/**
 * Photo preview activity.
 * */

public class PreviewActivity extends MysplashActivity
        implements View.OnClickListener, View.OnLongClickListener, SwipeBackCoordinatorLayout.OnSwipeListener {
    // widget
    private CoordinatorLayout container;
    private LinearLayout widgetContainer;
    private LinearLayout iconContainer;

    // data
    private Previewable previewable;
    private boolean showIcon = false;
    private boolean showingIcon = false;

    public static final String KEY_PREVIEW_ACTIVITY_PREVIEW = "preview_activity_preview";
    public static final String KEY_PREVIEW_ACTIVITY_SHOW_ICON = "preview_activity_show_icon";

    /** <br> life cycle. */

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
            initData();
            initWidget();
        }
    }

    @Override
    public void handleBackPressed() {
        finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Mysplash.getInstance().removeActivity(this);
    }

    @Override
    protected void setTheme() {
        if (Mysplash.getInstance().isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Preview);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Preview);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    protected boolean isFullScreen() {
        return false;
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
    public View getSnackbarContainer() {
        return container;
    }

    /** <br> view. */

    // init.

    private void initWidget() {
        this.container = (CoordinatorLayout) findViewById(R.id.activity_preview_container);

        SwipeBackCoordinatorLayout swipeBackView = (SwipeBackCoordinatorLayout) findViewById(R.id.activity_preview_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        final NestedScrollPhotoView photoView = (NestedScrollPhotoView) findViewById(R.id.activity_preview_photoView);
        photoView.enable();
        photoView.enableRotate();
        photoView.setMaxScale(calcMaxiScale());
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (showIcon) {
            photoView.setOnClickListener(this);
        }
        photoView.setOnLongClickListener(this);
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

        this.widgetContainer = (LinearLayout) findViewById(R.id.activity_preview_widgetContainer);
        this.iconContainer = (LinearLayout) findViewById(R.id.activity_preview_iconContainer);
    }

    // anim.

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

    /** <br> data. */

    private void initData() {
        this.previewable = getIntent().getParcelableExtra(KEY_PREVIEW_ACTIVITY_PREVIEW);
        this.showIcon = getIntent().getBooleanExtra(KEY_PREVIEW_ACTIVITY_SHOW_ICON, false);
    }

    private float calcMaxiScale() {
        float screenWidth = getResources().getDisplayMetrics().widthPixels;
        float screenHeight = getResources().getDisplayMetrics().heightPixels;
        if (previewable.getWidth() >= previewable.getHeight()) {
            return (float) (1.0 * screenHeight * previewable.getWidth() / screenWidth / previewable.getHeight());
        } else {
            return (float) (1.0 * screenWidth * previewable.getHeight() / screenHeight / previewable.getWidth());
        }
    }

    /** <br> interface. */

    // on click swipeListener.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_preview_photoView:
                if (showingIcon) {
                    showingIcon = false;
                    hideWidget();
                    hideIcons();
                } else {
                    showingIcon = true;
                    showWidget();
                    showIcons();
                }
                break;
        }
    }

    // on long click swipeListener.

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.activity_preview_photoView:

                break;
        }
        return true;
    }

    // on swipe swipeListener.

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