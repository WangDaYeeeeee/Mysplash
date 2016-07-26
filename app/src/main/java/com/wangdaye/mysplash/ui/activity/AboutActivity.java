package com.wangdaye.mysplash.ui.activity;

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
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.data.constant.Mysplash;
import com.wangdaye.mysplash.ui.widget.StatusBarView;
import com.wangdaye.mysplash.utils.DisplayUtils;

/**
 * About activity.
 * */

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {
    // data
    private boolean started = false;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtils.setStatusBarTransparent(this);
        DisplayUtils.setStatusBarTextDark(this);
        DisplayUtils.setWindowTop(this,
                getString(R.string.action_about),
                ContextCompat.getColor(this, R.color.colorPrimary));
        setContentView(R.layout.activity_about);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!started) {
            started = true;
            initWidget();
        }
    }

    /** <br> UI. */

    private void initWidget() {
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
                Uri gitHub = Uri.parse(Mysplash.AUTHOR_GITHUB);
                startActivity(new Intent(Intent.ACTION_VIEW, gitHub));
                break;

            case R.id.container_about_emailContainer:
                Uri email = Uri.parse("mailto:wangdayeeeeee@gmail.com");
                startActivity(new Intent(Intent.ACTION_SENDTO, email));
                break;

            case R.id.container_about_sourceCodeContainer:
                Uri source = Uri.parse(Mysplash.MYSPLASH_GITHUB);
                startActivity(new Intent(Intent.ACTION_VIEW, source));
                break;

            case R.id.container_about_unsplashContainer:
                Uri unslpash = Uri.parse(Mysplash.UNSPLASH_URL);
                startActivity(new Intent(Intent.ACTION_VIEW, unslpash));
                break;

            case R.id.container_about_retrofitContainer:
                Uri retrofit = Uri.parse("https://github.com/square/retrofit");
                startActivity(new Intent(Intent.ACTION_VIEW, retrofit));
                break;

            case R.id.container_about_glideContainer:
                Uri glide = Uri.parse("https://github.com/bumptech/glide");
                startActivity(new Intent(Intent.ACTION_VIEW, glide));
                break;

            case R.id.container_about_cpvContainer:
                Uri cpv = Uri.parse("https://github.com/rahatarmanahmed/CircularProgressView");
                startActivity(new Intent(Intent.ACTION_VIEW, cpv));
                break;

            case R.id.container_about_civContainer:
                Uri civ = Uri.parse("https://github.com/hdodenhof/CircleImageView");
                startActivity(new Intent(Intent.ACTION_VIEW, civ));
                break;

            case R.id.container_about_downloaderContainer:
                Uri downloader = Uri.parse("https://github.com/Aspsine/MultiThreadDownload");
                startActivity(new Intent(Intent.ACTION_VIEW, downloader));
                break;
        }
    }
}
