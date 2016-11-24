package com.wangdaye.mysplash._common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.data.service.CollectionService;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Update collection dialog.
 * */

public class UpdateCollectionDialog extends DialogFragment
        implements View.OnClickListener, CollectionService.OnRequestACollectionListener,
        CollectionService.OnDeleteCollectionListener {
    // widget
    private CircularProgressView progressView;

    private LinearLayout contentView;
    private EditText nameTxt;
    private EditText descriptionTxt;
    private CheckBox checkBox;

    private RelativeLayout baseBtnContainer;
    private RelativeLayout confirmBtnContainer;

    private OnCollectionChangedListener listener;

    // data
    private Collection collection;
    private CollectionService service;

    private int state;
    private final int INPUT_STATE = 1;
    private final int UPDATE_STATE = 2;
    private final int CONFIRM_STATE = 3;
    private final int DELETE_STATE = 4;

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Mysplash.getInstance().setActivityInBackstage(true);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_update_collection, null, false);
        initData();
        initWidget(view);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Mysplash.getInstance().setActivityInBackstage(false);
        service.cancel();
    }

    /** <br> UI. */

    private void initWidget(View v) {
        this.progressView = (CircularProgressView) v.findViewById(R.id.dialog_update_collection_progressView);
        progressView.setVisibility(View.GONE);

        this.contentView = (LinearLayout) v.findViewById(R.id.dialog_update_collection_contentView);
        contentView.setVisibility(View.VISIBLE);

        this.nameTxt = (EditText) v.findViewById(R.id.dialog_update_collection_name);
        DisplayUtils.setTypeface(getActivity(), nameTxt);
        nameTxt.setText(collection.title);

        this.descriptionTxt = (EditText) v.findViewById(R.id.dialog_update_collection_description);
        DisplayUtils.setTypeface(getActivity(), descriptionTxt);
        descriptionTxt.setText(collection.description == null ? "" : collection.description);

        this.checkBox = (CheckBox) v.findViewById(R.id.dialog_update_collection_checkBox);
        checkBox.setSelected(collection.privateX);
        DisplayUtils.setTypeface(getActivity(), checkBox);

        this.baseBtnContainer = (RelativeLayout) v.findViewById(R.id.dialog_update_collection_baseBtnContainer);
        baseBtnContainer.setVisibility(View.VISIBLE);

        this.confirmBtnContainer = (RelativeLayout) v.findViewById(R.id.dialog_update_collection_confirmBtnContainer);
        confirmBtnContainer.setVisibility(View.GONE);

        Button saveBtn = (Button) v.findViewById(R.id.dialog_update_collection_saveBtn);
        saveBtn.setOnClickListener(this);

        Button deleteBtn = (Button) v.findViewById(R.id.dialog_update_collection_deleteBtn);
        deleteBtn.setOnClickListener(this);

        Button doDeleteBtn = (Button) v.findViewById(R.id.dialog_update_collection_doDeleteBtn);
        doDeleteBtn.setOnClickListener(this);

        Button cancelBtn = (Button) v.findViewById(R.id.dialog_update_collection_cancelBtn);
        cancelBtn.setOnClickListener(this);
    }

    private void setState(int newState) {
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

    private void notifyUpdateFailed() {
        Toast.makeText(
                getActivity(),
                getString(R.string.feedback_update_collection_failed),
                Toast.LENGTH_SHORT).show();
    }

    private void notifyDeleteFailed() {
        Toast.makeText(
                getActivity(),
                getString(R.string.feedback_delete_collection_failed),
                Toast.LENGTH_SHORT).show();
    }

    /** <br> data. */

    private void initData() {
        this.service = CollectionService.getService();
        this.state = INPUT_STATE;
    }

    public void setCollection(Collection c) {
        collection = c;
    }

    private void updateCollection() {
        String title = nameTxt.getText().toString();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(
                    getActivity(),
                    getString(R.string.feedback_name_is_required),
                    Toast.LENGTH_SHORT).show();
        } else {
            String description = TextUtils.isEmpty(descriptionTxt.getText().toString()) ?
                    null : descriptionTxt.getText().toString();
            boolean privateX = checkBox.isSelected();
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

    /** <br> interface. */

    public interface OnCollectionChangedListener {
        void onEditCollection(Collection c);
        void onDeleteCollection(Collection c);
    }

    public void setOnCollectionChangedListener(OnCollectionChangedListener l) {
        listener = l;
    }

    // on click.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_update_collection_saveBtn:
                hideKeyboard();
                updateCollection();
                break;

            case R.id.dialog_update_collection_deleteBtn:
                setState(CONFIRM_STATE);
                break;

            case R.id.dialog_update_collection_doDeleteBtn:
                deleteCollection();
                setState(DELETE_STATE);
                break;

            case R.id.dialog_update_collection_cancelBtn:
                setState(INPUT_STATE);
                break;
        }
    }

    // on request a collection listener.

    @Override
    public void onRequestACollectionSuccess(Call<Collection> call, Response<Collection> response) {
        if (response.isSuccessful()) {
            if (listener != null) {
                listener.onEditCollection(response.body());
            }
            dismiss();
        } else if (Integer.parseInt(response.headers().get("X-Ratelimit-Remaining")) < 0) {
            dismiss();
            RateLimitDialog dialog = new RateLimitDialog();
            dialog.show(getFragmentManager(), null);
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
        } else if (Integer.parseInt(response.headers().get("X-Ratelimit-Remaining")) < 0) {
            dismiss();
            RateLimitDialog dialog = new RateLimitDialog();
            dialog.show(getFragmentManager(), null);
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
