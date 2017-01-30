package com.wangdaye.mysplash._common.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;

/**
 * Photo download view.
 * */

public class PhotoDownloadView extends FrameLayout {
    // widget
    private ImageButton[] optionButtons;
    private RelativeLayout progressView;
    private CircularProgressView progress;
    private TextView progressTxt;

    private Animator animator;

    // data
    private boolean showProgress;
    private int process;

    /** <br> life cycle. */

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PhotoDownloadView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    @SuppressLint("InflateParams")
    private void initialize() {
        View buttonView = LayoutInflater.from(getContext()).inflate(R.layout.container_download_button, null);
        addView(buttonView);

        View progressView = LayoutInflater.from(getContext()).inflate(R.layout.container_download_progress, null);
        addView(progressView);

        initData();
        initWidget();
    }

    /** <br> UI. */

    // init.

    private void initWidget() {
        // button view.
        this.optionButtons = new ImageButton[] {
                (ImageButton) findViewById(R.id.container_download_downloadBtn),
                (ImageButton) findViewById(R.id.container_download_shareBtn),
                (ImageButton) findViewById(R.id.container_download_wallBtn)};
        if (Mysplash.getInstance().isLightTheme()) {
            optionButtons[0].setImageResource(R.drawable.ic_download_light);
            optionButtons[1].setImageResource(R.drawable.ic_send_light);
            optionButtons[2].setImageResource(R.drawable.ic_mountain_light);
        } else {
            optionButtons[0].setImageResource(R.drawable.ic_download_dark);
            optionButtons[1].setImageResource(R.drawable.ic_send_dark);
            optionButtons[2].setImageResource(R.drawable.ic_mountain_dark);
        }

        TextView[] optionTexts = new TextView[]{
                (TextView) findViewById(R.id.container_download_downloadTxt),
                (TextView) findViewById(R.id.container_download_shareTxt),
                (TextView) findViewById(R.id.container_download_wallTxt)};
        String[] downloadOptions = getResources().getStringArray(R.array.download_options);
        for (int i = 0; i < optionTexts.length; i ++) {
            optionTexts[i].setText(downloadOptions[i]);
        }

        // progress view.
        this.progressView = (RelativeLayout) findViewById(R.id.container_download_progress);
        progressView.setVisibility(GONE);

        this.progress = (CircularProgressView) findViewById(R.id.container_download_progress_progressView);
        this.progressTxt = (TextView) findViewById(R.id.container_download_progress_text);
    }

    // interface.

    public void setButtonState() {
        if (showProgress) {
            setShowProgress(false);
            if (animator != null) {
                animator.cancel();
            }

            animator = ObjectAnimator.ofFloat(progressView, "alpha", progressView.getAlpha(), 0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    progressView.setVisibility(GONE);
                    animator.removeAllListeners();
                }
            });
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(200);
            animator.start();
        }
    }

    public void setProgressState() {
        if (!showProgress) {
            setShowProgress(true);
            if (animator != null) {
                animator.cancel();
            }

            initProcess();

            progressView.setVisibility(VISIBLE);

            animator = ObjectAnimator.ofFloat(progressView, "alpha", 0, 1);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(200);
            animator.start();
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

    /** <br> data. */

    // init.

    private void initData() {
        setShowProgress(false);
    }

    // interface.

    public void setShowProgress(boolean show) {
        this.showProgress = show;
    }

    /** <br> interface. */

    @Override
    public void setOnClickListener(OnClickListener l) {
        progressView.setOnClickListener(l);
        for (ImageButton optionButton : optionButtons) {
            optionButton.setOnClickListener(l);
        }
    }
}
