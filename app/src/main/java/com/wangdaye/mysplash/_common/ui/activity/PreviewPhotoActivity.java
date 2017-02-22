package com.wangdaye.mysplash._common.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.ui.widget.nestedScrollView.NestedScrollPhotoView;

/**
 * Photo preview activity.
 * */

public class PreviewPhotoActivity extends MysplashActivity
        implements View.OnClickListener, SwipeBackCoordinatorLayout.OnSwipeListener {
    // widget
    private CoordinatorLayout container;
    private LinearLayout widgetContainer;
    private LinearLayout iconContainer;

    // data
    private Photo photo;
    private boolean showPreview = false;

    public static final String KEY_PREVIEW_PHOTO_ACTIVITY_PHOTO = "preview_photo_activity_photo";

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_photo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initData();
            initView();
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
            setTheme(R.style.MysplashTheme_light_Translucent_PhotoPreview);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_PhotoPreview);
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
    protected boolean needSetStatusBarTextDark() {
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

    private void initView() {
        this.container = (CoordinatorLayout) findViewById(R.id.activity_preview_photo_container);

        SwipeBackCoordinatorLayout swipeBackView = (SwipeBackCoordinatorLayout) findViewById(R.id.activity_preview_photo_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        final NestedScrollPhotoView photoView = (NestedScrollPhotoView) findViewById(R.id.activity_preview_photo_photoView);
        photoView.enable();
        photoView.enableRotate();
        photoView.setMaxScale(calcMaxiScale());
        photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        photoView.setOnClickListener(this);
        DrawableRequestBuilder<String> thumbnailRequest = Glide
                .with(this)
                .load(photo.urls.regular)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);
        Glide.with(this)
                .load(photo.urls.full)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .thumbnail(thumbnailRequest)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e,
                                               String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        photoView.setMaxScale(2 * calcMaxiScale());
                        return false;
                    }
                })
                .into(photoView);

        this.widgetContainer = (LinearLayout) findViewById(R.id.activity_preview_photo_widgetContainer);
        this.iconContainer = (LinearLayout) findViewById(R.id.activity_preview_photo_iconContainer);
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
        this.photo = getIntent().getParcelableExtra(KEY_PREVIEW_PHOTO_ACTIVITY_PHOTO);
    }

    private float calcMaxiScale() {
        float screenWidth = getResources().getDisplayMetrics().widthPixels;
        float screenHeight = getResources().getDisplayMetrics().heightPixels;
        if (photo.width >= photo.height) {
            return (float) (1.0 * screenHeight * photo.width / screenWidth / photo.height);
        } else {
            return (float) (1.0 * screenWidth * photo.height / screenHeight / photo.width);
        }
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_preview_photo_photoView:
                if (showPreview) {
                    showPreview = false;
                    hideWidget();
                    hideIcons();
                } else {
                    showPreview = true;
                    showWidget();
                    showIcons();
                }
                break;
        }
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