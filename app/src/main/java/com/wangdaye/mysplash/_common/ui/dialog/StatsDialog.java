package com.wangdaye.mysplash._common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.data.entity.unsplash.PhotoStats;
import com.wangdaye.mysplash._common.data.service.PhotoService;
import com.wangdaye.mysplash._common.ui._basic.MysplashDialogFragment;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Stats dialog.
 * */

public class StatsDialog extends MysplashDialogFragment
        implements PhotoService.OnRequestStatsListener {
    // widget
    private CoordinatorLayout container;
    private CircularProgressView progress;
    private LinearLayout dataContainer;
    private TextView likeNum;
    private TextView viewNum;
    private TextView downloadNum;

    // data
    private PhotoService service;
    private Photo photo;

    private int state = 0;
    private final int LOADING_STATE = 0;
    private final int SUCCESS_STATE = 1;

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
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

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    /** <br> UI. */

    private void initWidget(View v) {
        this.container = (CoordinatorLayout) v.findViewById(R.id.dialog_stats_container);

        state = LOADING_STATE;
        this.service = PhotoService.getService();

        this.progress = (CircularProgressView) v.findViewById(R.id.dialog_stats_progress);
        progress.setVisibility(View.VISIBLE);

        this.dataContainer = (LinearLayout) v.findViewById(R.id.dialog_stats_dataContainer);
        dataContainer.setVisibility(View.GONE);

        this.likeNum = (TextView) v.findViewById(R.id.dialog_stats_likeNum);
        DisplayUtils.setTypeface(getActivity(), likeNum);

        this.viewNum = (TextView) v.findViewById(R.id.dialog_stats_viewNum);
        DisplayUtils.setTypeface(getActivity(), viewNum);

        this.downloadNum = (TextView) v.findViewById(R.id.dialog_stats_downloadNum);
        DisplayUtils.setTypeface(getActivity(), downloadNum);

        if (Mysplash.getInstance().isLightTheme()) {
            ((ImageView) v.findViewById(R.id.dialog_stats_downloadIcon))
                    .setImageResource(R.drawable.ic_download_light);
            ((ImageView) v.findViewById(R.id.dialog_stats_likeIcon))
                    .setImageResource(R.drawable.ic_heart_outline_light);
            ((ImageView) v.findViewById(R.id.dialog_stats_viewIcon))
                    .setImageResource(R.drawable.ic_eye_light);
        } else {
            ((ImageView) v.findViewById(R.id.dialog_stats_downloadIcon))
                    .setImageResource(R.drawable.ic_download_dark);
            ((ImageView) v.findViewById(R.id.dialog_stats_likeIcon))
                    .setImageResource(R.drawable.ic_heart_outline_dark);
            ((ImageView) v.findViewById(R.id.dialog_stats_viewIcon))
                    .setImageResource(R.drawable.ic_eye_dark);
        }
    }

    private void setState(int stateTo) {
        switch (stateTo) {
            case SUCCESS_STATE:
                if (state == LOADING_STATE) {
                    AnimUtils.animHide(progress);
                    AnimUtils.animShow(dataContainer);
                }
                break;
        }
        this.state = stateTo;
    }

    /** <br> data. */

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    /** <br> interface. */

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
            service.requestStats(photo.id, this);
        }
    }

    @Override
    public void onRequestStatsFailed(Call<PhotoStats> call, Throwable t) {
        service.requestStats(photo.id, this);
    }
}
