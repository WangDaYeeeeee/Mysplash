package com.wangdaye.photo.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.common.base.dialog.MysplashDialogFragment;
import com.wangdaye.base.DownloadTask;
import com.wangdaye.photo.R;
import com.wangdaye.photo.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Download type dialog.
 *
 * This dialog is used to select download type.
 * {@link DownloadTask#DOWNLOAD_TYPE}
 * {@link DownloadTask#SHARE_TYPE}
 * {@link DownloadTask#WALLPAPER_TYPE}
 * {@link DownloadTask.DownloadTypeRule}
 *
 * */

public class DownloadTypeDialog extends MysplashDialogFragment {

    @BindView(R2.id.dialog_download_type_container) CoordinatorLayout container;

    @OnClick(R2.id.dialog_download_type_download)
    void download() {
        if (listener != null) {
            listener.onSelectType(DownloadTask.DOWNLOAD_TYPE);
        }
        dismiss();
    }

    @OnClick(R2.id.dialog_download_type_share)
    void share() {
        if (listener != null) {
            listener.onSelectType(DownloadTask.SHARE_TYPE);
        }
        dismiss();
    }

    @OnClick(R2.id.dialog_download_type_wallpaper)
    void wallpaper() {
        if (listener != null) {
            listener.onSelectType(DownloadTask.WALLPAPER_TYPE);
        }
        dismiss();
    }

    private OnSelectTypeListener listener;

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_download_type, null, false);
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
        TextView downloadText = v.findViewById(R.id.dialog_download_type_downloadTxt);
        downloadText.setText(getResources().getStringArray(R.array.download_options)[0]);

        TextView shareText = v.findViewById(R.id.dialog_download_type_shareTxt);
        shareText.setText(getResources().getStringArray(R.array.download_options)[1]);

        TextView wallpaperText = v.findViewById(R.id.dialog_download_type_wallpaperTxt);
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
}
