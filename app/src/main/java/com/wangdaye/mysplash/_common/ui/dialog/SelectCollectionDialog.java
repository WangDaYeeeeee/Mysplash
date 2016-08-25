package com.wangdaye.mysplash._common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.data.AddPhotoToCollectionResult;
import com.wangdaye.mysplash._common.data.data.Collection;
import com.wangdaye.mysplash._common.data.data.Me;
import com.wangdaye.mysplash._common.data.data.Photo;
import com.wangdaye.mysplash._common.data.service.CollectionService;
import com.wangdaye.mysplash._common.data.tools.AuthManager;
import com.wangdaye.mysplash._common.ui.adapter.CollectionMiniAdapter;
import com.wangdaye.mysplash._common.ui.toast.MaterialToast;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Select collection dialog.
 * */

public class SelectCollectionDialog extends DialogFragment
        implements View.OnClickListener, CollectionMiniAdapter.OnCollectionResponseListener,
        CollectionService.OnRequestCollectionsListener, CollectionService.OnRequestACollectionListener,
        CollectionService.OnAddPhotoToCollectionListener {
    // widget
    private CircularProgressView progressView;

    private LinearLayout selectorContainer;

    private LinearLayout creatorContainer;
    private EditText nameTxt;
    private EditText descriptionTxt;
    private CheckBox checkBox;

    private OnCollectionsChangedListener listener;

    // data
    private Me me;
    private Photo photo;
    private int page;
    private CollectionMiniAdapter adapter;
    private CollectionService service;

    private int state;
    private final int LOAD_COLLECTIONS_STATE = 1;
    private final int SHOW_COLLECTIONS_STATE = 2;
    private final int INPUT_COLLECTION_STATE = 3;
    private final int CREATE_COLLECTION_STATE = 4;
    private final int ADD_PHOTO_STATE = 5;

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Mysplash.getInstance().setActivityInBackstage(true);
        Context contextThemeWrapper;
        if (ThemeUtils.getInstance(getActivity()).isLightTheme()) {
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MysplashTheme_light_Common);
        } else {
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MysplashTheme_dark_Common);
        }
        View view = LayoutInflater.from(getActivity())
                .cloneInContext(contextThemeWrapper)
                .inflate(R.layout.dialog_select_collection, null, false);
        initData();
        initWidget(view);
        requestCollections();
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
        this.progressView = (CircularProgressView) v.findViewById(R.id.dialog_select_collection_progressView);
        progressView.setVisibility(View.VISIBLE);

        this.selectorContainer = (LinearLayout) v.findViewById(R.id.dialog_select_collection_selectorContainer);
        selectorContainer.setVisibility(View.GONE);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.dialog_select_collection_selectorRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        this.creatorContainer = (LinearLayout) v.findViewById(R.id.dialog_select_collection_creatorContainer);
        creatorContainer.setVisibility(View.GONE);

        this.nameTxt = (EditText) v.findViewById(R.id.dialog_select_collection_creatorName);
        TypefaceUtils.setTypeface(getActivity(), nameTxt);

        this.descriptionTxt = (EditText) v.findViewById(R.id.dialog_select_collection_creatorDescription);
        TypefaceUtils.setTypeface(getActivity(), descriptionTxt);

        this.checkBox = (CheckBox) v.findViewById(R.id.dialog_select_collection_creatorCheckBox);
        TypefaceUtils.setTypeface(getActivity(), checkBox);

        Button createBtn = (Button) v.findViewById(R.id.dialog_select_collection_creatorCreateBtn);
        createBtn.setOnClickListener(this);

        Button cancelBtn = (Button) v.findViewById(R.id.dialog_select_collection_creatorCancelBtn);
        cancelBtn.setOnClickListener(this);
    }

    private void setState(int newState) {
        switch (newState) {
            case LOAD_COLLECTIONS_STATE:
                setCancelable(true);
                break;

            case SHOW_COLLECTIONS_STATE:
                setCancelable(true);
                if (state == LOAD_COLLECTIONS_STATE) {
                    AnimUtils.animShow(selectorContainer);
                    AnimUtils.animHide(progressView);
                } else if (state == CREATE_COLLECTION_STATE) {
                    AnimUtils.animShow(selectorContainer);
                    AnimUtils.animHide(progressView);
                } else if (state == INPUT_COLLECTION_STATE) {
                    AnimUtils.animShow(selectorContainer);
                    AnimUtils.animHide(creatorContainer);
                } else if (state == ADD_PHOTO_STATE) {
                    AnimUtils.animShow(selectorContainer);
                    AnimUtils.animHide(progressView);
                }
                break;

            case INPUT_COLLECTION_STATE:
                setCancelable(true);
                if (state == SHOW_COLLECTIONS_STATE) {
                    AnimUtils.animShow(creatorContainer);
                    AnimUtils.animHide(selectorContainer);
                } else if (state == CREATE_COLLECTION_STATE) {
                    AnimUtils.animShow(creatorContainer);
                    AnimUtils.animHide(progressView);
                }
                break;

            case CREATE_COLLECTION_STATE:
                setCancelable(false);
                if (state == INPUT_COLLECTION_STATE) {
                    AnimUtils.animShow(progressView);
                    AnimUtils.animHide(creatorContainer);
                }
                break;

            case ADD_PHOTO_STATE:
                setCancelable(false);
                if (state == SHOW_COLLECTIONS_STATE) {
                    AnimUtils.animShow(progressView);
                    AnimUtils.animHide(selectorContainer);
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

    /** <br> data. */

    private void initData() {
        this.me = AuthManager.getInstance().getMe();
        this.adapter = new CollectionMiniAdapter(getActivity(), new ArrayList<Collection>());
        adapter.setOnClickCreateItemListener(this);
        this.page = 1;
        this.service = CollectionService.getService();
        this.state = LOAD_COLLECTIONS_STATE;
    }

    public void setPhoto(Photo p) {
        photo = p;
    }

    private void requestCollections() {
        service.requestUserCollections(me, page, 10, this);
    }

    private void createCollection() {
        String title = nameTxt.getText().toString();
        if (TextUtils.isEmpty(title)) {
            MaterialToast.makeText(
                    getActivity(),
                    getString(R.string.feedback_name_is_required),
                    null,
                    MaterialToast.LENGTH_SHORT).show();
        } else {
            String description = TextUtils.isEmpty(descriptionTxt.getText().toString()) ?
                    null : descriptionTxt.getText().toString();
            boolean privateX = checkBox.isSelected();
            service.createCollection(
                    title,
                    description,
                    privateX,
                    this);
            setState(CREATE_COLLECTION_STATE);
        }
    }

    /** <br> interface. */

    public interface OnCollectionsChangedListener {
        void onAddCollection(Collection c);
        void onAddPhotoToCollection(Collection c, Me me);
    }

    public void setOnCollectionsChangedListener(OnCollectionsChangedListener l) {
        listener = l;
    }

    // on click.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_select_collection_creatorCreateBtn:
                hideKeyboard();
                createCollection();
                break;

            case R.id.dialog_select_collection_creatorCancelBtn:
                hideKeyboard();
                setState(SHOW_COLLECTIONS_STATE);
                break;
        }
    }

    // on click create item listener.

    @Override
    public void onCreateCollection() {
        setState(INPUT_COLLECTION_STATE);
    }

    @Override
    public void onAddToCollection(int collection_id) {
        service.addPhotoToCollection(collection_id, photo.id, this);
        setState(ADD_PHOTO_STATE);
    }

    // on request collections listener.

    @Override
    public void onRequestCollectionsSuccess(Call<List<Collection>> call, Response<List<Collection>> response) {
        if (response.isSuccessful()) {
            for (int i = 0; i < response.body().size(); i ++) {
                adapter.insertItem(response.body().get(i), adapter.getRealItemCount());
            }
            if (response.body().size() < 10) {
                setState(SHOW_COLLECTIONS_STATE);
            } else {
                page ++;
                requestCollections();
            }
        } else {
            requestCollections();
        }
    }

    @Override
    public void onRequestCollectionsFailed(Call<List<Collection>> call, Throwable t) {
        requestCollections();
    }

    // on request a collection listener.

    @Override
    public void onRequestACollectionSuccess(Call<Collection> call, Response<Collection> response) {
        if (response.isSuccessful()) {
            adapter.insertItem(response.body(), 0);
            setState(SHOW_COLLECTIONS_STATE);
            nameTxt.setText("");
            descriptionTxt.setText("");
            checkBox.setSelected(false);
            if (listener != null) {
                listener.onAddCollection(response.body());
            }
        } else {
            createCollection();
        }
    }

    @Override
    public void onRequestACollectionFailed(Call<Collection> call, Throwable t) {
        createCollection();
    }

    // on add photo to collection listener.

    @Override
    public void onAddPhotoSuccess(Call<AddPhotoToCollectionResult> call,
                                  Response<AddPhotoToCollectionResult> response) {
        if (response.isSuccessful()) {
            if (listener != null) {
                listener.onAddPhotoToCollection(response.body().collection, response.body().me);
            }
            dismiss();
        } else {
            MaterialToast.makeText(
                    getActivity(),
                    getString(R.string.feedback_add_photo_to_collection_failed),
                    null,
                    MaterialToast.LENGTH_SHORT).show();
            setState(SHOW_COLLECTIONS_STATE);
        }
    }

    @Override
    public void onAddPhotoFailed(Call<AddPhotoToCollectionResult> call, Throwable t) {
        MaterialToast.makeText(
                getActivity(),
                getString(R.string.feedback_add_photo_to_collection_failed),
                null,
                MaterialToast.LENGTH_SHORT).show();
        setState(SHOW_COLLECTIONS_STATE);
    }
}
