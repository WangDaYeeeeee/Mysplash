package com.wangdaye.about.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.about.R;
import com.wangdaye.about.R2;
import com.wangdaye.about.di.DaggerNetworkServiceComponent;
import com.wangdaye.common.base.dialog.MysplashDialogFragment;
import com.wangdaye.base.unsplash.Total;
import com.wangdaye.common.network.observer.BaseObserver;
import com.wangdaye.common.network.service.StatusService;
import com.wangdaye.common.utils.AnimUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Total dialog.
 *
 * This dialog is used to show total stats for Unsplash.
 *
 * */

public class TotalDialog extends MysplashDialogFragment {

    @BindView(R2.id.dialog_total_container) CoordinatorLayout container;
    @BindView(R2.id.dialog_total_progress) CircularProgressView progress;
    @BindView(R2.id.dialog_total_dataContainer) LinearLayout dataContainer;
    @BindView(R2.id.dialog_total_totalPhotosNum) TextView photoNum;
    @BindView(R2.id.dialog_total_photoDownloadsNum) TextView downloadNum;

    @Inject StatusService service;

    private int state = 0;
    private final int LOADING_STATE = 0;
    private final int SUCCESS_STATE = 1;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        DaggerNetworkServiceComponent.create().inject(this);
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_total, null, false);
        ButterKnife.bind(this, view);
        this.state = LOADING_STATE;
        initWidget();
        service.requestTotal(onRequestTotalObserver);
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

    private void initWidget() {
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

    private BaseObserver<Total> onRequestTotalObserver = new BaseObserver<Total>() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onSucceed(Total total) {
            photoNum.setText(total.total_photos + " PHOTOS");
            downloadNum.setText(total.photo_downloads + " DOWNLOADS");
            setState(SUCCESS_STATE);
        }

        @Override
        public void onFailed() {
            service.requestTotal(this);
        }
    };
}
