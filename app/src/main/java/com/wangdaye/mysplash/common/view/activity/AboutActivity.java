package com.wangdaye.mysplash.common.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.LinkUtils;
import com.wangdaye.mysplash.common.widget.StatusBarView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;

/**
 * About activity.
 * */

public class AboutActivity extends AppCompatActivity
        implements View.OnClickListener {
    // model.
    private boolean started = false;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtils.setStatusBarTransparent(this);
        DisplayUtils.setStatusBarTextDark(this);
        setContentView(R.layout.activity_about);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!started) {
            started = true;
            initView();
        }
    }

    /** <br> view. */

    private void initView() {
        StatusBarView statusBar = (StatusBarView) findViewById(R.id.activity_about_statusBar);
        if (Build.VERSION.SDK_INT <Build.VERSION_CODES.M) {
            statusBar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            statusBar.setMask(true);
        }

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
                (RelativeLayout) findViewById(R.id.container_about_downloaderContainer)};
        for (RelativeLayout r : containers) {
            r.setOnClickListener(this);
        }
    }

    /** <br> interface. */

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
        }
    }
}
