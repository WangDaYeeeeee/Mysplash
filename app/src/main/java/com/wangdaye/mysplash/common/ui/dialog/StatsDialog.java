package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.PhotoStats;
import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.common._basic.fragment.MysplashDialogFragment;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Stats dialog.
 *
 * This dialog is used to show the stats for a photo.
 *
 * */

public class StatsDialog extends MysplashDialogFragment
        implements PhotoService.OnRequestStatsListener {

    @BindView(R.id.dialog_stats_container)
    CoordinatorLayout container;

    @BindView(R.id.dialog_stats_progress)
    CircularProgressView progress;

    @BindView(R.id.dialog_stats_dataContainer)
    LinearLayout dataContainer;

    @BindView(R.id.dialog_stats_likeNum)
    TextView likeNum;

    @BindView(R.id.dialog_stats_viewNum)
    TextView viewNum;

    @BindView(R.id.dialog_stats_downloadNum)
    TextView downloadNum;

    private PhotoService service;
    private Photo photo;

    @StateRule
    private int state = 0;

    private static final int LOADING_STATE = 0;
    private static final int SUCCESS_STATE = 1;
    @IntDef({LOADING_STATE, SUCCESS_STATE})
    private @interface StateRule {}

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_stats, null, false);
        ButterKnife.bind(this, view);
        state = LOADING_STATE;
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
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    private void initWidget(View v) {
        this.service = PhotoService.getService();

        progress.setVisibility(View.VISIBLE);
        dataContainer.setVisibility(View.GONE);

        DisplayUtils.setTypeface(getActivity(), likeNum);
        DisplayUtils.setTypeface(getActivity(), viewNum);
        DisplayUtils.setTypeface(getActivity(), downloadNum);

        ImageView download = ButterKnife.findById(v, R.id.dialog_stats_downloadIcon);
        ThemeManager.setImageResource(
                download, R.drawable.ic_download_light, R.drawable.ic_download_dark);

        ImageView like = ButterKnife.findById(v, R.id.dialog_stats_likeIcon);
        ThemeManager.setImageResource(
                like, R.drawable.ic_heart_light, R.drawable.ic_heart_dark);

        ImageView view = ButterKnife.findById(v, R.id.dialog_stats_viewIcon);
        ThemeManager.setImageResource(
                view, R.drawable.ic_eye_light, R.drawable.ic_eye_dark);
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    private void setState(@StateRule int stateTo) {
        if (stateTo == SUCCESS_STATE && state == LOADING_STATE) {
            AnimUtils.animHide(progress);
            AnimUtils.animShow(dataContainer);
        }
        this.state = stateTo;
    }

    // interface.

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
