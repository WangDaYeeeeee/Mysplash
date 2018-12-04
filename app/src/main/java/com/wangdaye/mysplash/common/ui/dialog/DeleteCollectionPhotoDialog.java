package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.ChangeCollectionPhotoResult;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.service.network.CollectionService;
import com.wangdaye.mysplash.common.basic.fragment.MysplashDialogFragment;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Delete collection photo dialog fragment.
 *
 * This dialog is used to confirm and delete photo from a collection.
 *
 * */

public class DeleteCollectionPhotoDialog extends MysplashDialogFragment
        implements CollectionService.OnChangeCollectionPhotoListener {

    @BindView(R.id.dialog_delete_collection_photo_container)
    CoordinatorLayout container;

    @BindView(R.id.dialog_delete_collection_photo_confirmContainer)
    RelativeLayout confirmContainer;

    @BindView(R.id.dialog_delete_collection_photo_progress)
    CircularProgressView progressView;

    private OnDeleteCollectionListener listener;

    private CollectionService service;

    private Collection collection;
    private Photo photo;
    private int position;

    @StateRule
    private int state;

    private static final int CONFIRM_STATE = 0;
    private static final int DELETE_STATE = 1;
    @IntDef({CONFIRM_STATE, DELETE_STATE})
    private @interface StateRule {}

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_delete_collection_photo, null, false);
        ButterKnife.bind(this, view);
        initData();
        initWidget();
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
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    private void initData() {
        this.service = CollectionService.getService();
        this.state = CONFIRM_STATE;
    }

    private void initWidget() {
        confirmContainer.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.GONE);
    }

    private void setState(@StateRule int newState) {
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

    public void setDeleteInfo(Collection c, Photo p, int position) {
        collection = c;
        photo = p;
        this.position = position;
    }

    private void notifyFailed() {
        NotificationHelper.showSnackbar(getString(R.string.feedback_delete_photo_failed));
    }

    // interface.

    // on delete collection listener.

    public interface OnDeleteCollectionListener {
        void onDeletePhotoSuccess(ChangeCollectionPhotoResult result, int position);
    }

    public void setOnDeleteCollectionListener(OnDeleteCollectionListener l) {
        listener = l;
    }

    // on click listener.

    @OnClick(R.id.dialog_delete_collection_photo_deleteBtn) void delete() {
        setState(DELETE_STATE);
        service.deletePhotoFromCollection(collection.id, photo.id, this);
    }

    @OnClick(R.id.dialog_delete_collection_photo_cancelBtn) void cancel() {
        dismiss();
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
