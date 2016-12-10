package com.wangdaye.mysplash._common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.ChangeCollectionPhotoResult;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.data.entity.unsplash.Me;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common.data.service.CollectionService;
import com.wangdaye.mysplash._common.ui._basic.MysplashDialogFragment;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;
import com.wangdaye.mysplash._common.ui.adapter.CollectionMiniAdapter;
import com.wangdaye.mysplash._common.utils.AnimUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Select collection dialog.
 * */

public class SelectCollectionDialog extends MysplashDialogFragment
        implements View.OnClickListener, AuthManager.OnAuthDataChangedListener,
        CollectionMiniAdapter.OnCollectionResponseListener, CollectionService.OnRequestACollectionListener,
        CollectionService.OnChangeCollectionPhotoListener {
    // widget
    private CoordinatorLayout container;

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
    private OnRequestCollectionsListener serviceListener;

    private int state;
    private final int SHOW_COLLECTIONS_STATE = 0;
    private final int INPUT_COLLECTION_STATE = 1;
    private final int CREATE_COLLECTION_STATE = 2;
    private final int ADD_PHOTO_STATE = 3;

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_select_collection, null, false);
        initData();
        initWidget(view);
        AuthManager.getInstance().addOnWriteDataListener(this);
        if (!AuthManager.getInstance().getCollectionsManager().isLoadFinish()) {
            AuthManager.getInstance().getCollectionsManager().clearCollections();
            initRefresh();
        }
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AuthManager.getInstance().removeOnWriteDataListener(this);
        service.cancel();
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    /** <br> UI. */

    private void initWidget(View v) {
        setCancelable(true);

        this.container = (CoordinatorLayout) v.findViewById(R.id.dialog_select_collection_container);

        this.progressView = (CircularProgressView) v.findViewById(R.id.dialog_select_collection_progressView);
        progressView.setVisibility(View.GONE);

        this.selectorContainer = (LinearLayout) v.findViewById(R.id.dialog_select_collection_selectorContainer);
        selectorContainer.setVisibility(View.VISIBLE);

        ImageButton refreshBtn = (ImageButton) v.findViewById(R.id.dialog_select_collection_selectorRefreshBtn);
        if (Mysplash.getInstance().isLightTheme()) {
            refreshBtn.setImageResource(R.drawable.ic_refresh_light);
        } else {
            refreshBtn.setImageResource(R.drawable.ic_refresh_dark);
        }
        refreshBtn.setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.dialog_select_collection_selectorRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        this.creatorContainer = (LinearLayout) v.findViewById(R.id.dialog_select_collection_creatorContainer);
        creatorContainer.setVisibility(View.GONE);

        this.nameTxt = (EditText) v.findViewById(R.id.dialog_select_collection_creatorName);
        DisplayUtils.setTypeface(getActivity(), nameTxt);

        this.descriptionTxt = (EditText) v.findViewById(R.id.dialog_select_collection_creatorDescription);
        DisplayUtils.setTypeface(getActivity(), descriptionTxt);

        this.checkBox = (CheckBox) v.findViewById(R.id.dialog_select_collection_creatorCheckBox);
        DisplayUtils.setTypeface(getActivity(), checkBox);

        Button createBtn = (Button) v.findViewById(R.id.dialog_select_collection_creatorCreateBtn);
        createBtn.setOnClickListener(this);

        Button cancelBtn = (Button) v.findViewById(R.id.dialog_select_collection_creatorCancelBtn);
        cancelBtn.setOnClickListener(this);
    }

    private void setState(int newState) {
        switch (newState) {
            case SHOW_COLLECTIONS_STATE:
                setCancelable(true);
                if (state == CREATE_COLLECTION_STATE) {
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

    private void notifyCreateFailed() {
        Toast.makeText(
                getActivity(),
                getString(R.string.feedback_create_collection_failed),
                Toast.LENGTH_SHORT).show();
    }

    private void notifyAddFailed() {
        Toast.makeText(
                getActivity(),
                getString(R.string.feedback_add_photo_failed),
                Toast.LENGTH_SHORT).show();
    }

    /** <br> data. */

    private void initData() {
        this.me = AuthManager.getInstance().getMe();
        this.service = CollectionService.getService();
        this.state = SHOW_COLLECTIONS_STATE;
        this.page = 1;

        this.adapter = new CollectionMiniAdapter(getActivity());
        adapter.setOnClickCreateItemListener(this);
    }

    public void setPhoto(Photo p) {
        photo = p;
    }

    private void initRefresh() {
        if (AuthManager.getInstance().getState() == AuthManager.FREEDOM_STATE) {
            if (AuthManager.getInstance().getMe() == null) {
                requestProfile();
            } else {
                int listSize = AuthManager.getInstance()
                        .getCollectionsManager()
                        .getCollectionList()
                        .size();
                if (listSize > 0) {
                    AuthManager.getInstance().getCollectionsManager().clearCollections();
                    adapter.notifyItemRangeRemoved(1, listSize);
                    adapter.notifyItemChanged(1);
                }
                page = 1;
                requestCollections();
            }
        }
    }

    private void requestProfile() {
        AuthManager.getInstance().refreshPersonalProfile();
    }

    private void requestCollections() {
        if (serviceListener != null) {
            serviceListener.cancel();
        }
        service.cancel();

        serviceListener = new OnRequestCollectionsListener();
        service.requestUserCollections(me.username, page, 10, serviceListener);
    }

    private void createCollection() {
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
        void onAddPhotoToCollection(Collection c, User u);
    }

    public void setOnCollectionsChangedListener(OnCollectionsChangedListener l) {
        listener = l;
    }

    // on click.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_select_collection_selectorRefreshBtn:
                initRefresh();
                break;

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

    // on auth data changed listener.

    @Override
    public void onWriteAccessToken() {
        // do nothing.
    }

    @Override
    public void onWriteUserInfo() {
        if (me == null) {
            me = AuthManager.getInstance().getMe();
            requestCollections();
        }
    }

    @Override
    public void onWriteAvatarPath() {
        // do nothing.
    }

    @Override
    public void onLogout() {
        // do nothing.
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

    private class OnRequestCollectionsListener
            implements CollectionService.OnRequestCollectionsListener {
        // data
        private boolean canceled;

        OnRequestCollectionsListener() {
            canceled = false;
        }

        // data.
        public void cancel() {
            this.canceled = true;
        }

        // interface.

        @Override
        public void onRequestCollectionsSuccess(Call<List<Collection>> call, Response<List<Collection>> response) {
            if (canceled) {
                return;
            }
            if (response.isSuccessful() && response.body() != null) {
                if (response.body().size() > 0) {
                    int startPosition = AuthManager.getInstance()
                            .getCollectionsManager()
                            .getCollectionList()
                            .size() + 1;
                    AuthManager.getInstance()
                            .getCollectionsManager()
                            .addCollections(response.body());
                    adapter.notifyItemRangeInserted(startPosition, response.body().size());
                }
                if (response.body().size() < 10) {
                    AuthManager.getInstance().getCollectionsManager().setLoadFinish(true);
                    adapter.notifyItemRemoved(
                            AuthManager.getInstance()
                                    .getCollectionsManager()
                                    .getCollectionList()
                                    .size() + 1);
                } else {
                    page ++;
                    requestCollections();
                }
            } else if (Integer.parseInt(response.headers().get("X-Ratelimit-Remaining")) < 0) {
                dismiss();
                RateLimitDialog dialog = new RateLimitDialog();
                dialog.show(getFragmentManager(), null);
            } else {
                requestCollections();
            }
        }

        @Override
        public void onRequestCollectionsFailed(Call<List<Collection>> call, Throwable t) {
            if (canceled) {
                return;
            }
            requestCollections();
        }
    }

    // on request a collection listener.

    @Override
    public void onRequestACollectionSuccess(Call<Collection> call, Response<Collection> response) {
        if (response.isSuccessful() && response.body() != null) {
            AuthManager.getInstance().getCollectionsManager().addCollectionToFirst(response.body());
            adapter.notifyItemInserted(1);
            setState(SHOW_COLLECTIONS_STATE);
            nameTxt.setText("");
            descriptionTxt.setText("");
            checkBox.setSelected(false);
            if (listener != null) {
                listener.onAddCollection(response.body());
            }
        } else if (Integer.parseInt(response.headers().get("X-Ratelimit-Remaining")) < 0) {
            dismiss();
            RateLimitDialog dialog = new RateLimitDialog();
            dialog.show(getFragmentManager(), null);
        } else {
            setState(INPUT_COLLECTION_STATE);
            notifyCreateFailed();
        }
    }

    @Override
    public void onRequestACollectionFailed(Call<Collection> call, Throwable t) {
        setState(INPUT_COLLECTION_STATE);
        notifyCreateFailed();
    }

    // on change collection photo listener.

    @Override
    public void onChangePhotoSuccess(Call<ChangeCollectionPhotoResult> call,
                                     Response<ChangeCollectionPhotoResult> response) {
        if (response.isSuccessful()) {
            if (listener != null) {
                AuthManager.getInstance().getCollectionsManager().updateCollection(response.body().collection);
                listener.onAddPhotoToCollection(response.body().collection, response.body().user);
            }
            dismiss();
        } else if (Integer.parseInt(response.headers().get("X-Ratelimit-Remaining")) < 0) {
            dismiss();
            RateLimitDialog dialog = new RateLimitDialog();
            dialog.show(getFragmentManager(), null);
        } else {
            setState(SHOW_COLLECTIONS_STATE);
            notifyAddFailed();
        }
    }

    @Override
    public void onChangePhotoFailed(Call<ChangeCollectionPhotoResult> call, Throwable t) {
        setState(SHOW_COLLECTIONS_STATE);
        notifyAddFailed();
    }
}
