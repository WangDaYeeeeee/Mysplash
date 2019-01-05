package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.service.network.CollectionService;
import com.wangdaye.mysplash.common.basic.fragment.MysplashDialogFragment;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Update collection dialog.
 *
 * This dialog is used to update the collection.
 *
 * */

public class UpdateCollectionDialog extends MysplashDialogFragment
        implements CollectionService.OnRequestACollectionListener,
        CollectionService.OnDeleteCollectionListener {

    @BindView(R.id.dialog_update_collection_container)
    CoordinatorLayout container;

    @BindView(R.id.dialog_update_collection_progressView)
    CircularProgressView progressView;

    @BindView(R.id.dialog_update_collection_contentView)
    LinearLayout contentView;

    @BindView(R.id.dialog_update_collection_name)
    EditText nameTxt;

    @BindView(R.id.dialog_update_collection_description)
    EditText descriptionTxt;

    @BindView(R.id.dialog_update_collection_checkBox)
    CheckBox checkBox;

    @BindView(R.id.dialog_update_collection_baseBtnContainer)
    RelativeLayout baseBtnContainer;

    @BindView(R.id.dialog_update_collection_confirmBtnContainer)
    RelativeLayout confirmBtnContainer;

    private OnCollectionChangedListener listener;

    private Collection collection;
    private CollectionService service;

    @StateRule
    private int state;

    private static final int INPUT_STATE = 1;
    private static final int UPDATE_STATE = 2;
    private static final int CONFIRM_STATE = 3;
    private static final int DELETE_STATE = 4;
    @IntDef({INPUT_STATE, UPDATE_STATE, CONFIRM_STATE, DELETE_STATE})
    private @interface StateRule {}

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_update_collection, null, false);
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
        this.state = INPUT_STATE;
    }

    private void initWidget() {
        progressView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);

        nameTxt.setText(collection.title);

        descriptionTxt.setText(collection.description == null ? "" : collection.description);

        checkBox.setChecked(collection.privateX);

        baseBtnContainer.setVisibility(View.VISIBLE);
        confirmBtnContainer.setVisibility(View.GONE);
    }

    public void setCollection(Collection c) {
        collection = c;
    }

    private void updateCollection() {
        String title = nameTxt.getText().toString();
        if (TextUtils.isEmpty(title)) {
            NotificationHelper.showSnackbar(getString(R.string.feedback_name_is_required));
        } else {
            String description = TextUtils.isEmpty(descriptionTxt.getText().toString()) ?
                    null : descriptionTxt.getText().toString();
            boolean privateX = checkBox.isChecked();
            service.updateCollection(
                    collection.id,
                    title,
                    description,
                    privateX,
                    this);
            setState(UPDATE_STATE);
        }
    }

    private void deleteCollection() {
        service.deleteCollection(collection.id, this);
    }

    private void notifyUpdateFailed() {
        NotificationHelper.showSnackbar(getString(R.string.feedback_update_collection_failed));
    }

    private void notifyDeleteFailed() {
        NotificationHelper.showSnackbar(getString(R.string.feedback_delete_collection_failed));
    }

    private void setState(@StateRule int newState) {
        switch (newState) {
            case INPUT_STATE:
                setCancelable(true);
                if (state == CONFIRM_STATE) {
                    AnimUtils.animShow(baseBtnContainer);
                    AnimUtils.animHide(confirmBtnContainer);
                } else if (state == UPDATE_STATE || state == DELETE_STATE) {
                    AnimUtils.animShow(baseBtnContainer);
                    AnimUtils.animHide(progressView);
                }
                break;

            case UPDATE_STATE:
                setCancelable(false);
                if (state == INPUT_STATE) {
                    AnimUtils.animShow(progressView);
                    AnimUtils.animHide(contentView);
                }
                break;

            case CONFIRM_STATE:
                setCancelable(true);
                if (state == INPUT_STATE) {
                    AnimUtils.animShow(confirmBtnContainer);
                    AnimUtils.animHide(baseBtnContainer);
                }
                break;

            case DELETE_STATE:
                setCancelable(false);
                if (state == CONFIRM_STATE) {
                    AnimUtils.animShow(progressView);
                    AnimUtils.animHide(contentView);
                }
                break;
        }
        state = newState;
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(nameTxt.getWindowToken(), 0);
        manager.hideSoftInputFromWindow(descriptionTxt.getWindowToken(), 0);
    }

    // interface.

    // on collection changed listener.

    public interface OnCollectionChangedListener {
        void onEditCollection(Collection c);
        void onDeleteCollection(Collection c);
    }

    public void setOnCollectionChangedListener(OnCollectionChangedListener l) {
        listener = l;
    }

    // on click listener.

    @OnClick(R.id.dialog_update_collection_saveBtn) void save() {
        hideKeyboard();
        updateCollection();
    }

    @OnClick(R.id.dialog_update_collection_deleteBtn) void transformToDeleteState() {
        setState(CONFIRM_STATE);
    }

    @OnClick(R.id.dialog_update_collection_doDeleteBtn) void delete() {
        deleteCollection();
        setState(DELETE_STATE);
    }

    @OnClick(R.id.dialog_update_collection_cancelBtn) void cancelDelete() {
        setState(INPUT_STATE);
    }

    // on request a collection listener.

    @Override
    public void onRequestACollectionSuccess(Call<Collection> call, Response<Collection> response) {
        if (response.isSuccessful() && response.body() != null) {
            if (listener != null) {
                listener.onEditCollection(response.body());
            }
            dismiss();
        } else {
            setState(INPUT_STATE);
            notifyUpdateFailed();
        }
    }

    @Override
    public void onRequestACollectionFailed(Call<Collection> call, Throwable t) {
        setState(INPUT_STATE);
        notifyUpdateFailed();
    }

    // on delete collection listener.

    @Override
    public void onDeleteCollectionSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful()) {
            if (listener != null) {
                listener.onDeleteCollection(collection);
            }
            dismiss();
        } else {
            setState(INPUT_STATE);
            notifyDeleteFailed();
        }
    }

    @Override
    public void onDeleteCollectionFailed(Call<ResponseBody> call, Throwable t) {
        setState(INPUT_STATE);
        notifyDeleteFailed();
    }
}
