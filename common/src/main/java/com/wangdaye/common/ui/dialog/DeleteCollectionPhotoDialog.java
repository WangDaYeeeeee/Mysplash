package com.wangdaye.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.common.R;
import com.wangdaye.common.R2;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.base.dialog.MysplashDialogFragment;
import com.wangdaye.base.unsplash.ChangeCollectionPhotoResult;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.di.component.DaggerNetworkServiceComponent;
import com.wangdaye.common.network.observer.BaseObserver;
import com.wangdaye.common.network.service.CollectionService;
import com.wangdaye.common.utils.AnimUtils;
import com.wangdaye.common.utils.helper.NotificationHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Delete collection photo dialog fragment.
 *
 * This dialog is used to confirm and delete photo from a collection.
 *
 * */

public class DeleteCollectionPhotoDialog extends MysplashDialogFragment {

    @BindView(R2.id.dialog_delete_collection_photo_container) CoordinatorLayout container;
    @BindView(R2.id.dialog_delete_collection_photo_confirmContainer) RelativeLayout confirmContainer;
    @BindView(R2.id.dialog_delete_collection_photo_progress) CircularProgressView progressView;

    @OnClick(R2.id.dialog_delete_collection_photo_deleteBtn) void delete() {
        setState(DELETE_STATE);
        service.deletePhotoFromCollection(collection.id, photo.id, new BaseObserver<ChangeCollectionPhotoResult>() {
            @Override
            public void onSucceed(ChangeCollectionPhotoResult changeCollectionPhotoResult) {
                if (listener != null) {
                    listener.onDeletePhotoSuccess(changeCollectionPhotoResult);
                }
                dismiss();
            }

            @Override
            public void onFailed() {
                setState(CONFIRM_STATE);
                notifyFailed();
            }
        });
    }

    @OnClick(R2.id.dialog_delete_collection_photo_cancelBtn) void cancel() {
        dismiss();
    }

    private OnDeleteCollectionListener listener;

    @Inject CollectionService service;

    private Collection collection;
    private Photo photo;

    @StateRule
    private int state;

    private static final int CONFIRM_STATE = 0;
    private static final int DELETE_STATE = 1;
    @IntDef({CONFIRM_STATE, DELETE_STATE})
    private @interface StateRule {}

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

    public void setDeleteInfo(Collection c, Photo p) {
        collection = c;
        photo = p;
    }

    private void notifyFailed() {
        if (getActivity() != null) {
            NotificationHelper.showSnackbar(
                    (MysplashActivity) getActivity(),
                    getString(R.string.feedback_delete_photo_failed)
            );
        }
    }

    // interface.

    // on delete collection listener.

    public interface OnDeleteCollectionListener {
        void onDeletePhotoSuccess(ChangeCollectionPhotoResult result);
    }

    public void setOnDeleteCollectionListener(OnDeleteCollectionListener l) {
        listener = l;
    }
}
