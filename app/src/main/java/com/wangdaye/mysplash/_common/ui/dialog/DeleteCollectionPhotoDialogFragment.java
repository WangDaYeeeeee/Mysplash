package com.wangdaye.mysplash._common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.ChangeCollectionPhotoResult;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.data.service.CollectionService;
import com.wangdaye.mysplash._common._basic.MysplashDialogFragment;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.helper.NotificationHelper;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Delete collection photo dialog.
 * */

public class DeleteCollectionPhotoDialogFragment extends MysplashDialogFragment
        implements View.OnClickListener, CollectionService.OnChangeCollectionPhotoListener {
    // widget
    private CoordinatorLayout container;
    private RelativeLayout confirmContainer;
    private CircularProgressView progressView;

    private OnDeleteCollectionListener listener;

    // data
    private CollectionService service;

    private Collection collection;
    private Photo photo;
    private int position;

    private int state;
    private static final int CONFIRM_STATE = 0;
    private static final int DELETE_STATE = 1;

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete_collection_photo, null, false);
        initData();
        initWidget(view);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service.cancel();
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    /** <br> UI. */

    private void initWidget(View v) {
        this.container = (CoordinatorLayout) v.findViewById(R.id.dialog_select_collection_container);

        this.confirmContainer = (RelativeLayout) v.findViewById(R.id.dialog_delete_collection_photo_confirmContainer);
        confirmContainer.setVisibility(View.VISIBLE);

        Button deleteBtn = (Button) v.findViewById(R.id.dialog_delete_collection_photo_deleteBtn);
        deleteBtn.setOnClickListener(this);

        Button cancelBtn = (Button) v.findViewById(R.id.dialog_delete_collection_photo_cancelBtn);
        cancelBtn.setOnClickListener(this);

        this.progressView = (CircularProgressView) v.findViewById(R.id.dialog_delete_collection_photo_progress);
        progressView.setVisibility(View.GONE);
    }

    private void setState(int newState) {
        switch (newState) {
            case CONFIRM_STATE:
                setCancelable(true);
                if (state == DELETE_STATE) {
                    AnimUtils.animShow(confirmContainer);
                    AnimUtils.animHide(progressView);
                }
                break;

            case DELETE_STATE:
                setCancelable(false);
                if (state == CONFIRM_STATE) {
                    AnimUtils.animShow(progressView);
                    AnimUtils.animHide(confirmContainer);
                }
                break;
        }
        state = newState;
    }

    private void notifyFailed() {
        NotificationHelper.showSnackbar(
                getString(R.string.feedback_delete_photo_failed),
                Snackbar.LENGTH_SHORT);
    }

    /** <br> data. */

    private void initData() {
        this.service = CollectionService.getService();
        this.state = CONFIRM_STATE;
    }

    public void setDeleteInfo(Collection c, Photo p, int position) {
        collection = c;
        photo = p;
        this.position = position;
    }

    /** <br> interface. */

    // on delete collection listener.

    public interface OnDeleteCollectionListener {
        void onDeletePhotoSuccess(ChangeCollectionPhotoResult result, int position);
    }

    public void setOnDeleteCollectionListener(OnDeleteCollectionListener l) {
        listener = l;
    }

    // on click listener.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_delete_collection_photo_deleteBtn:
                setState(DELETE_STATE);
                service.deletePhotoFromCollection(collection.id, photo.id, this);
                break;

            case R.id.dialog_delete_collection_photo_cancelBtn:
                dismiss();
                break;
        }
    }

    // on change collection photo listener.

    @Override
    public void onChangePhotoSuccess(Call<ChangeCollectionPhotoResult> call,
                                     Response<ChangeCollectionPhotoResult> response) {
        if (response.isSuccessful() && response.body() != null) {
            if (listener != null) {
                listener.onDeletePhotoSuccess(response.body(), position);
            }
            dismiss();
        } else {
            setState(CONFIRM_STATE);
            notifyFailed();
        }
    }

    @Override
    public void onChangePhotoFailed(Call<ChangeCollectionPhotoResult> call, Throwable t) {
        setState(CONFIRM_STATE);
        notifyFailed();
    }
}
