package com.wangdaye.mysplash._common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Total;
import com.wangdaye.mysplash._common.data.service.StatusService;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Total dialog.
 * */

public class TotalDialog extends DialogFragment
        implements StatusService.OnRequestTotalListener {
    // widget
    private CircularProgressView progress;
    private LinearLayout dataContainer;
    private TextView photoNum;
    private TextView downloadNum;

    // data
    private StatusService service;

    private int state = 0;
    private final int LOADING_STATE = 0;
    private final int SUCCESS_STATE = 1;

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Mysplash.getInstance().setActivityInBackstage(true);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_total, null, false);
        initWidget(view);
        service.requestTotal(this);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Mysplash.getInstance().setActivityInBackstage(false);
        if (service != null) {
            service.cancel();
        }
    }

    /** <br> UI. */

    private void initWidget(View v) {
        state = LOADING_STATE;
        this.service = StatusService.getService();

        this.progress = (CircularProgressView) v.findViewById(R.id.dialog_total_progress);
        progress.setVisibility(View.VISIBLE);

        this.dataContainer = (LinearLayout) v.findViewById(R.id.dialog_total_dataContainer);
        dataContainer.setVisibility(View.GONE);

        this.photoNum = (TextView) v.findViewById(R.id.dialog_total_totalPhotosNum);
        DisplayUtils.setTypeface(getActivity(), photoNum);

        this.downloadNum = (TextView) v.findViewById(R.id.dialog_total_photoDownloadsNum);
        DisplayUtils.setTypeface(getActivity(), downloadNum);

        if (Mysplash.getInstance().isLightTheme()) {
            ((ImageView) v.findViewById(R.id.dialog_total_photoDownloadsIcon))
                    .setImageResource(R.drawable.ic_download_light);
            ((ImageView) v.findViewById(R.id.dialog_total_totalPhotosIcon))
                    .setImageResource(R.drawable.ic_image_light);
        } else {
            ((ImageView) v.findViewById(R.id.dialog_total_photoDownloadsIcon))
                    .setImageResource(R.drawable.ic_download_dark);
            ((ImageView) v.findViewById(R.id.dialog_total_totalPhotosIcon))
                    .setImageResource(R.drawable.ic_image_dark);
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

    /** <br> interface. */

    // on request stats listener.

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestTotalSuccess(Call<Total> call, Response<Total> response) {
        if (response.isSuccessful() && response.body() != null) {
            photoNum.setText(response.body().total_photos + " PHOTOS");
            downloadNum.setText(response.body().photo_downloads + " DOWNLOADS");
            setState(SUCCESS_STATE);
        } else if (Integer.parseInt(response.headers().get("X-Ratelimit-Remaining")) < 0) {
            dismiss();
            RateLimitDialog dialog = new RateLimitDialog();
            dialog.show(getFragmentManager(), null);
        } else {
            service.requestTotal(this);
        }
    }

    @Override
    public void onRequestTotalFailed(Call<Total> call, Throwable t) {
        service.requestTotal(this);
    }
}
