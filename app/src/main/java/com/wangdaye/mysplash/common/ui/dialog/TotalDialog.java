package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Total;
import com.wangdaye.mysplash.common.data.service.network.StatusService;
import com.wangdaye.mysplash.common.basic.fragment.MysplashDialogFragment;
import com.wangdaye.mysplash.common.utils.AnimUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Total dialog.
 *
 * This dialog is used to show total stats for Unsplash.
 *
 * */

public class TotalDialog extends MysplashDialogFragment
        implements StatusService.OnRequestTotalListener {

    @BindView(R.id.dialog_total_container)
    CoordinatorLayout container;

    @BindView(R.id.dialog_total_progress)
    CircularProgressView progress;

    @BindView(R.id.dialog_total_dataContainer)
    LinearLayout dataContainer;

    @BindView(R.id.dialog_total_totalPhotosNum)
    TextView photoNum;

    @BindView(R.id.dialog_total_photoDownloadsNum)
    TextView downloadNum;

    private StatusService service;

    private int state = 0;
    private final int LOADING_STATE = 0;
    private final int SUCCESS_STATE = 1;

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_total, null, false);
        ButterKnife.bind(this, view);
        this.state = LOADING_STATE;
        initWidget(view);
        service.requestTotal(this);
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
        this.service = StatusService.getService();

        progress.setVisibility(View.VISIBLE);
        dataContainer.setVisibility(View.GONE);
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

    // interface.

    // on request stats listener.

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestTotalSuccess(Call<Total> call, Response<Total> response) {
        if (response.isSuccessful() && response.body() != null) {
            photoNum.setText(response.body().total_photos + " PHOTOS");
            downloadNum.setText(response.body().photo_downloads + " DOWNLOADS");
            setState(SUCCESS_STATE);
        } else {
            service.requestTotal(this);
        }
    }

    @Override
    public void onRequestTotalFailed(Call<Total> call, Throwable t) {
        service.requestTotal(this);
    }
}
