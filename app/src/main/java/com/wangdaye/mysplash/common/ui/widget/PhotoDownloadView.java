package com.wangdaye.mysplash.common.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Photo download view.
 * */

public class PhotoDownloadView extends RelativeLayout {

    @BindView(R.id.container_download_button)
    LinearLayout buttonView;

    @BindViews({
            R.id.container_download_downloadBtn,
            R.id.container_download_shareBtn,
            R.id.container_download_wallBtn})
    ImageButton[] optionButtons;

    @BindView(R.id.container_download_progress)
    RelativeLayout progressView;

    @BindView(R.id.container_download_progress_progressView)
    CircularProgressView progress;

    @BindView(R.id.container_download_progress_text)
    TextView progressTxt;

    private Animator show;
    private Animator hide;

    private boolean showProgress;
    private int process;

    public PhotoDownloadView(Context context) {
        super(context);
        this.initialize();
    }

    public PhotoDownloadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public PhotoDownloadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @SuppressLint("InflateParams")
    private void initialize() {
        View buttonView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_download_button, null);
        addView(buttonView);

        View progressView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_download_progress, null);
        addView(progressView);

        ButterKnife.bind(this, this);
        initData();
        initWidget();
    }

    private void initData() {
        setShowProgress(false);
    }

    private void initWidget() {
        ThemeManager.setImageResource(
                optionButtons[0], R.drawable.ic_download_light, R.drawable.ic_download_dark);
        ThemeManager.setImageResource(
                optionButtons[1], R.drawable.ic_send_light, R.drawable.ic_send_dark);
        ThemeManager.setImageResource(
                optionButtons[2], R.drawable.ic_mountain_light, R.drawable.ic_mountain_dark);

        TextView[] optionTexts = new TextView[]{
                (TextView) findViewById(R.id.container_download_downloadTxt),
                (TextView) findViewById(R.id.container_download_shareTxt),
                (TextView) findViewById(R.id.container_download_wallTxt)};
        String[] downloadOptions = getResources().getStringArray(R.array.download_options);
        for (int i = 0; i < optionTexts.length; i ++) {
            optionTexts[i].setText(downloadOptions[i]);
        }

        progressView.setVisibility(GONE);
    }

    // draw.

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        LayoutParams params = (LayoutParams) buttonView.getLayoutParams();
        if (DisplayUtils.isTabletDevice(getContext())
                || getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.width = getResources()
                    .getDimensionPixelSize(R.dimen.tablet_download_button_bar_width);
        } else {
            params.width = getMeasuredWidth();
        }
        params.addRule(CENTER_IN_PARENT);
        buttonView.setLayoutParams(params);

        params = (LayoutParams) progressView.getLayoutParams();
        params.width = getMeasuredWidth();
        params.height = getMeasuredHeight();
        progressView.setLayoutParams(params);
    }

    // control.

    public void setButtonState() {
        if (showProgress) {
            setShowProgress(false);
            if (show != null) {
                show.cancel();
            }
            if (hide != null) {
                hide.cancel();
            }

            show = ObjectAnimator.ofFloat(buttonView, "alpha", buttonView.getAlpha(), 1);
            show.setInterpolator(new AccelerateDecelerateInterpolator());
            show.setDuration(200);

            hide = ObjectAnimator.ofFloat(progressView, "alpha", progressView.getAlpha(), 0);
            hide.removeAllListeners();
            hide.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    progressView.setVisibility(GONE);
                }
            });
            hide.setInterpolator(new AccelerateDecelerateInterpolator());
            hide.setDuration(200);

            buttonView.setVisibility(VISIBLE);
            show.start();
            hide.start();
        }
    }

    public void setProgressState() {
        if (!showProgress) {
            setShowProgress(true);
            if (show != null) {
                show.cancel();
            }
            if (hide != null) {
                hide.cancel();
            }

            initProcess();

            show = ObjectAnimator.ofFloat(progressView, "alpha", progressView.getAlpha(), 1);
            show.setInterpolator(new AccelerateDecelerateInterpolator());
            show.setDuration(200);

            hide = ObjectAnimator.ofFloat(buttonView, "alpha", buttonView.getAlpha(), 0);
            hide.removeAllListeners();
            hide.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    buttonView.setVisibility(GONE);
                }
            });
            hide.setInterpolator(new AccelerateDecelerateInterpolator());
            hide.setDuration(200);

            progressView.setVisibility(VISIBLE);
            show.start();
            hide.start();
        }
    }

    @SuppressLint("SetTextI18n")
    public void setProcess(int p) {
        if (p != process) {
            process = p;
            progress.setProgress(p);
            progressTxt.setText(p + " %");
        }
    }

    @SuppressLint("SetTextI18n")
    private void initProcess() {
        process = 0;
        progress.setProgress(process);
        progressTxt.setText(process + " %");
    }

    public void setShowProgress(boolean show) {
        this.showProgress = show;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        progressView.setOnClickListener(l);
        for (ImageButton optionButton : optionButtons) {
            optionButton.setOnClickListener(l);
        }
    }
}
