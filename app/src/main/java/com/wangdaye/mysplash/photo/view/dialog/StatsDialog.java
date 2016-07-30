package com.wangdaye.mysplash.photo.view.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.model.PhotoStats;
import com.wangdaye.mysplash.common.data.model.SimplifiedPhoto;
import com.wangdaye.mysplash.common.data.service.PhotoService;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Stats dialog.
 * */

public class StatsDialog extends DialogFragment
        implements PhotoService.OnRequestStatsListener, View.OnClickListener {
    // widget
    private CircularProgressView progress;
    private Button retryButton;
    private LinearLayout dataContainer;
    private TextView likeNum;
    private TextView viewNum;
    private TextView downloadNum;

    // data
    private PhotoService service;
    private SimplifiedPhoto photo;

    private int state = 0;
    private final int LOADING_STATE = 0;
    private final int FAILED_STATE = -1;
    private final int SUCCESS_STATE = 1;

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_stats, null, false);
        initWidget(view);
        service.requestStats(photo.id, this);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (service != null) {
            service.cancel();
        }
    }

    /** <br> UI. */

    private void initWidget(View v) {
        state = LOADING_STATE;
        this.service = PhotoService.getService()
                .buildClient();

        this.progress = (CircularProgressView) v.findViewById(R.id.dialog_stats_progress);
        this.retryButton = (Button) v.findViewById(R.id.dialog_stats_retryButton);
        retryButton.setOnClickListener(this);

        this.dataContainer = (LinearLayout) v.findViewById(R.id.dialog_stats_dataContainer);
        this.likeNum = (TextView) v.findViewById(R.id.dialog_stats_likeNum);
        this.viewNum = (TextView) v.findViewById(R.id.dialog_stats_viewNum);
        this.downloadNum = (TextView) v.findViewById(R.id.dialog_stats_downloadNum);
    }

    private void setState(int stateTo) {
        switch (stateTo) {
            case LOADING_STATE:
                if (state == FAILED_STATE) {
                    animShow(progress);
                    animHide(retryButton);
                }
                break;

            case FAILED_STATE:
                if (state == LOADING_STATE) {
                    animShow(retryButton);
                    animHide(progress);
                }
                break;

            case SUCCESS_STATE:
                if (state == LOADING_STATE) {
                    animHide(progress);
                    animShow(dataContainer);
                }
                break;
        }
        this.state = stateTo;
    }

    private void animShow(final View v) {
        if (v.getVisibility() != View.VISIBLE) {
            v.setVisibility(View.VISIBLE);
        }
        ObjectAnimator
                .ofFloat(v, "alpha", 0, 1)
                .setDuration(300)
                .start();
    }

    private void animHide(final View v) {
        ObjectAnimator anim = ObjectAnimator
                .ofFloat(v, "alpha", 1, 0)
                .setDuration(300);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setVisibility(View.GONE);
            }
        });
        anim.start();
    }

    /** <br> data. */

    public void setPhoto(SimplifiedPhoto photo) {
        this.photo = photo;
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_stats_retryButton:
                setState(LOADING_STATE);
                service.requestStats(photo.id, this);
                break;
        }
    }

    // on request stats listener.

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestStatsSuccess(Call<PhotoStats> call, Response<PhotoStats> response) {
        if (response.isSuccessful() && response.body() != null) {
            likeNum.setText(response.body().likes + " LIKES");
            viewNum.setText(response.body().views + " VIEWS");
            downloadNum.setText(response.body().downloads + " DOWNLOADS");
            setState(SUCCESS_STATE);
        } else {
            setState(FAILED_STATE);
        }
    }

    @Override
    public void onRequestStatsFailed(Call<PhotoStats> call, Throwable t) {
        setState(FAILED_STATE);
    }
}
