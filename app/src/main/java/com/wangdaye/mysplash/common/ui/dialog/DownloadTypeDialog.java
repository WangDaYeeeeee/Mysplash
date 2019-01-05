package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.fragment.MysplashDialogFragment;
import com.wangdaye.mysplash.common.data.service.downloader.DownloaderService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Download type dialog.
 *
 * This dialog is used to select download type.
 * {@link DownloaderService#DOWNLOAD_TYPE}
 * {@link DownloaderService#SHARE_TYPE}
 * {@link DownloaderService#WALLPAPER_TYPE}
 * {@link DownloaderService.DownloadTypeRule}
 *
 * */

public class DownloadTypeDialog extends MysplashDialogFragment {

    @BindView(R.id.dialog_download_type_container)
    CoordinatorLayout container;

    private OnSelectTypeListener listener;

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_download_type, null, false);
        ButterKnife.bind(this, view);
        initWidget(view);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    private void initWidget(View v) {
        TextView downloadText = ButterKnife.findById(v, R.id.dialog_download_type_downloadTxt);
        downloadText.setText(getResources().getStringArray(R.array.download_options)[0]);

        TextView shareText = ButterKnife.findById(v, R.id.dialog_download_type_shareTxt);
        shareText.setText(getResources().getStringArray(R.array.download_options)[1]);

        TextView wallpaperText = ButterKnife.findById(v, R.id.dialog_download_type_wallpaperTxt);
        wallpaperText.setText(getResources().getStringArray(R.array.download_options)[2]);
    }

    // interface.

    // on select type listener.

    public interface OnSelectTypeListener {
        void onSelectType(int type);
    }

    public void setOnSelectTypeListener(OnSelectTypeListener l) {
        listener = l;
    }

    // on click listener.

    @OnClick(R.id.dialog_download_type_download)
    void download() {
        if (listener != null) {
            listener.onSelectType(DownloaderService.DOWNLOAD_TYPE);
        }
        dismiss();
    }

    @OnClick(R.id.dialog_download_type_share)
    void share() {
        if (listener != null) {
            listener.onSelectType(DownloaderService.SHARE_TYPE);
        }
        dismiss();
    }

    @OnClick(R.id.dialog_download_type_wallpaper)
    void wallpaper() {
        if (listener != null) {
            listener.onSelectType(DownloaderService.WALLPAPER_TYPE);
        }
        dismiss();
    }
}
