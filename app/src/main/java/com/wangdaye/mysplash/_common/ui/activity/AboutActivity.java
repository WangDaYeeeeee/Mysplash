package com.wangdaye.mysplash._common.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackLayout;
import com.wangdaye.mysplash._common.utils.LinkUtils;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash._common.ui.widget.StatusBarView;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;

/**
 * About activity.
 * */

public class AboutActivity extends MysplashActivity
        implements View.OnClickListener, SwipeBackLayout.OnSwipeListener {
    // widget
    private CoordinatorLayout container;
    private NestedScrollView scrollView;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initWidget();
        }
    }

    @Override
    protected void setTheme() {
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Common);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.activity_slide_out_bottom);
    }

    /** <br> UI.. */

    private void initWidget() {
        SwipeBackLayout swipeBackLayout = (SwipeBackLayout) findViewById(R.id.activity_about_swipeBackLayout);
        swipeBackLayout.setOnSwipeListener(this);

        StatusBarView statusBar = (StatusBarView) findViewById(R.id.activity_about_statusBar);
        if (ThemeUtils.getInstance(this).isNeedSetStatusBarMask()) {
            statusBar.setMask(true);
        }

        this.container = (CoordinatorLayout) findViewById(R.id.activity_about_container);
        this.scrollView = (NestedScrollView) findViewById(R.id.activity_about_scrollView);

        ImageView iconView = (ImageView) findViewById(R.id.container_about_appIcon);
        Glide.with(this)
                .load(R.drawable.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .crossFade(300)
                .into(iconView);

        ImageButton backBtn = (ImageButton) findViewById(R.id.container_about_backButton);
        backBtn.setOnClickListener(this);

        RelativeLayout[] containers = new RelativeLayout[] {
                (RelativeLayout) findViewById(R.id.container_about_gitHubContainer),
                (RelativeLayout) findViewById(R.id.container_about_emailContainer),
                (RelativeLayout) findViewById(R.id.container_about_sourceCodeContainer),
                (RelativeLayout) findViewById(R.id.container_about_unsplashContainer),
                (RelativeLayout) findViewById(R.id.container_about_retrofitContainer),
                (RelativeLayout) findViewById(R.id.container_about_glideContainer),
                (RelativeLayout) findViewById(R.id.container_about_cpvContainer),
                (RelativeLayout) findViewById(R.id.container_about_civContainer),
                (RelativeLayout) findViewById(R.id.container_about_downloaderContainer),
                (RelativeLayout) findViewById(R.id.container_about_tagContainer),
                (RelativeLayout) findViewById(R.id.container_about_photoViewContainer)};
        for (RelativeLayout r : containers) {
            r.setOnClickListener(this);
        }

        TextView[] textViews = new TextView[] {
                (TextView) findViewById(R.id.container_about_versionCode),
                (TextView) findViewById(R.id.container_about_unsplashTitle),
                (TextView) findViewById(R.id.container_about_retrofitTitle),
                (TextView) findViewById(R.id.container_about_glideTitle),
                (TextView) findViewById(R.id.container_about_cpvTitle),
                (TextView) findViewById(R.id.container_about_civTitle),
                (TextView) findViewById(R.id.container_about_downloaderTitle),
                (TextView) findViewById(R.id.container_about_tagTitle),
                (TextView) findViewById(R.id.container_about_photoViewTitle),
                (TextView) findViewById(R.id.container_about_unsplashContent),
                (TextView) findViewById(R.id.container_about_retrofitContent),
                (TextView) findViewById(R.id.container_about_glideContent),
                (TextView) findViewById(R.id.container_about_cpvContent),
                (TextView) findViewById(R.id.container_about_civContent),
                (TextView) findViewById(R.id.container_about_downloaderContent),
                (TextView) findViewById(R.id.container_about_tagContent),
                (TextView) findViewById(R.id.container_about_photoViewContent)};
        for (TextView t : textViews) {
            TypefaceUtils.setTypeface(this, t);
        }

        if (ThemeUtils.getInstance(this).isLightTheme()) {
            ((ImageView) findViewById(R.id.container_about_backButton)).setImageResource(R.drawable.ic_toolbar_back_light);
            ((ImageView) findViewById(R.id.container_about_gitHubIcon)).setImageResource(R.drawable.ic_github_light);
            ((ImageView) findViewById(R.id.container_about_emailIcon)).setImageResource(R.drawable.ic_email_light);
            ((ImageView) findViewById(R.id.container_about_sourceCodeIcon)).setImageResource(R.drawable.ic_android_studio_light);
        } else {
            ((ImageView) findViewById(R.id.container_about_backButton)).setImageResource(R.drawable.ic_toolbar_back_dark);
            ((ImageView) findViewById(R.id.container_about_gitHubIcon)).setImageResource(R.drawable.ic_github_dark);
            ((ImageView) findViewById(R.id.container_about_emailIcon)).setImageResource(R.drawable.ic_email_dark);
            ((ImageView) findViewById(R.id.container_about_sourceCodeIcon)).setImageResource(R.drawable.ic_android_studio_dark);
        }
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
            case R.id.container_about_backButton:
                finish();
                break;

            case R.id.container_about_gitHubContainer:
                LinkUtils.accessLink(this, Mysplash.AUTHOR_GITHUB);
                break;

            case R.id.container_about_emailContainer:
                Uri email = Uri.parse("mailto:wangdayeeeeee@gmail.com");
                startActivity(new Intent(Intent.ACTION_SENDTO, email));
                break;

            case R.id.container_about_sourceCodeContainer:
                LinkUtils.accessLink(this, Mysplash.MYSPLASH_GITHUB);
                break;

            case R.id.container_about_unsplashContainer:
                LinkUtils.accessLink(this, Mysplash.UNSPLASH_URL);
                break;

            case R.id.container_about_retrofitContainer:
                LinkUtils.accessLink(this, "https://github.com/square/retrofit");
                break;

            case R.id.container_about_glideContainer:
                LinkUtils.accessLink(this, "https://github.com/bumptech/glide");
                break;

            case R.id.container_about_cpvContainer:
                LinkUtils.accessLink(this, "https://github.com/rahatarmanahmed/CircularProgressView");
                break;

            case R.id.container_about_civContainer:
                LinkUtils.accessLink(this, "https://github.com/hdodenhof/CircleImageView");
                break;

            case R.id.container_about_downloaderContainer:
                LinkUtils.accessLink(this, "https://github.com/smanikandan14/ThinDownloadManager");
                break;

            case R.id.container_about_tagContainer:
                LinkUtils.accessLink(this, "https://github.com/hongyangAndroid/FlowLayout");
                break;

            case R.id.container_about_photoViewContainer:
                LinkUtils.accessLink(this, "https://github.com/bm-x/PhotoView");
                break;
        }
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return SwipeBackLayout.canSwipeBack(scrollView, dir);
    }

    @Override
    public void onSwipeFinish(int dir) {
        finish();
        switch (dir) {
            case SwipeBackLayout.UP_DIR:
                overridePendingTransition(0, R.anim.activity_slide_out_top);
                break;

            case SwipeBackLayout.DOWN_DIR:
                overridePendingTransition(0, R.anim.activity_slide_out_bottom);
                break;
        }
    }

    // snackbar container.

    @Override
    public View getSnackbarContainer() {
        return container;
    }
}
