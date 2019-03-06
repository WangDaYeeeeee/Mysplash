package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.json.ChangeCollectionPhotoResult;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.network.service.CollectionService;
import com.wangdaye.mysplash.common.basic.fragment.MysplashDialogFragment;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.download.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.ui.adapter.CollectionMiniAdapter;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Select collection dialog.
 *
 * This dialog is used to deal some operation about collections, like add or remove photos or create
 * new collections.
 *
 * */

public class SelectCollectionDialog extends MysplashDialogFragment
        implements AuthManager.OnAuthDataChangedListener, CollectionMiniAdapter.ItemEventCallback {

    @BindView(R.id.dialog_select_collection_container) CoordinatorLayout container;

    @BindView(R.id.dialog_select_collection_progressContainer) RelativeLayout progressContainer;
    @BindView(R.id.dialog_select_collection_selectorContainer) LinearLayout selectorContainer;

    @BindView(R.id.dialog_select_collection_titleBar) RelativeLayout selectorTitleBar;
    @BindView(R.id.dialog_select_collection_selectorRefreshView) BothWaySwipeRefreshLayout refreshLayout;

    @BindView(R.id.dialog_select_collection_selectorRecyclerView) RecyclerView recyclerView;

    @BindView(R.id.dialog_select_collection_creatorContainer) RelativeLayout creatorContainer;
    @BindView(R.id.dialog_select_collection_creatorNameContainer) TextInputLayout nameTxtContainer;
    @BindView(R.id.dialog_select_collection_creatorName) TextInputEditText nameTxt;
    @BindView(R.id.dialog_select_collection_creatorDescription) TextInputEditText descriptionTxt;
    @BindView(R.id.dialog_select_collection_creatorCheckBox) CheckBox checkBox;

    @OnClick(R.id.dialog_select_collection_selectorRefreshBtn) void refresh() {
        initRefresh();
        refreshLayout.setLoading(true);
    }

    @OnClick(R.id.dialog_select_collection_creatorCreateBtn) void create() {
        hideKeyboard();
        createCollection();
    }

    @OnClick(R.id.dialog_select_collection_creatorCancelBtn) void cancel() {
        hideKeyboard();
        setState(SHOW_COLLECTIONS_STATE);
    }

    private OnCollectionsChangedListener listener;

    private Photo photo;
    private int page; // HTTP request param.
    private CollectionMiniAdapter adapter;
    @Inject CollectionService service;

    @StateRule private int state;

    private static final int SHOW_COLLECTIONS_STATE = 0;
    private static final int INPUT_COLLECTION_STATE = 1;
    private static final int CREATE_COLLECTION_STATE = 2;
    @IntDef({SHOW_COLLECTIONS_STATE, INPUT_COLLECTION_STATE, CREATE_COLLECTION_STATE})
    private @interface StateRule {}

    private boolean usable; // if set false, it means the dialog has been destroyed.

    private int processingCount;

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_select_collection, null, false);
        ButterKnife.bind(this, view);
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
    public void onStart() {
        super.onStart();
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            int height;
            if (DisplayUtils.isLandscape(Objects.requireNonNull(getActivity()))) {
                height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
            } else {
                height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);
            }
            window.setLayout(window.getAttributes().width, height);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        usable = false;
        service.cancel();
        AuthManager.getInstance().removeOnWriteDataListener(this);
        AuthManager.getInstance().getCollectionsManager().finishEdit();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initData() {
        this.state = SHOW_COLLECTIONS_STATE;
        this.page = 1;

        this.adapter = new CollectionMiniAdapter(photo);
        adapter.setItemEventCallback(this);

        this.usable = true;
        this.processingCount = 0;
    }

    private void initWidget(View v) {
        setCancelable(true);

        AppCompatImageView cover = v.findViewById(R.id.dialog_select_collection_cover);
        if (DisplayUtils.isTabletDevice(Objects.requireNonNull(getActivity()))) {
            ImageHelper.loadRegularPhoto(getActivity(), cover, photo, null);
        } else {
            cover.setVisibility(View.GONE);
        }

        progressContainer.setVisibility(View.GONE);
        selectorContainer.setVisibility(View.VISIBLE);

        refreshLayout.setColorSchemeColors(ThemeManager.getContentColor(getActivity()));
        refreshLayout.setProgressBackgroundColorSchemeColor(ThemeManager.getRootColor(getActivity()));
        refreshLayout.setPermitRefresh(false);
        refreshLayout.setPermitLoad(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recyclerView.addOnScrollListener(new ElevationScrollListener());
        }
        recyclerView.addOnScrollListener(new LoadScrollListener());

        creatorContainer.setVisibility(View.GONE);

        nameTxt.setOnFocusChangeListener((v1, hasFocus) -> nameTxtContainer.setError(null));
    }

    // control.

    // HTTP request.

    private void initRefresh() {
        if (AuthManager.getInstance().getState() == AuthManager.State.FREE) {
            if (AuthManager.getInstance().getUser() == null) {
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
                requestCollections(AuthManager.getInstance().getUser().username);
            }
        }
    }

    private void requestProfile() {
        AuthManager.getInstance().requestPersonalProfile();
    }

    private void requestCollections(String username) {
        service.cancel();

        OnRequestCollectionListCallback serviceCallback = new OnRequestCollectionListCallback();
        service.requestUserCollections(username, page, Mysplash.DEFAULT_PER_PAGE, serviceCallback);
    }

    private void createCollection() {
        String title;
        String description = null;

        if (nameTxt.getText() == null || TextUtils.isEmpty(nameTxt.getText().toString())) {
            nameTxtContainer.setError(getString(R.string.feedback_name_is_required));
            return;
        } else {
            title = nameTxt.getText().toString();
        }

        if (descriptionTxt.getText() != null && !TextUtils.isEmpty(descriptionTxt.getText().toString())) {
            description = descriptionTxt.getText().toString();
        }

        boolean privateX = checkBox.isChecked();

        service.createCollection(
                title,
                description,
                privateX,
                new OnRequestACollectionCallback());
        setState(CREATE_COLLECTION_STATE);
    }

    // state.

    private void setState(@StateRule int newState) {
        switch (newState) {
            case SHOW_COLLECTIONS_STATE:
                setCancelable(true);
                if (state == CREATE_COLLECTION_STATE) {
                    AnimUtils.animShow(selectorContainer);
                    AnimUtils.animHide(progressContainer);
                } else if (state == INPUT_COLLECTION_STATE) {
                    AnimUtils.animShow(selectorContainer);
                    AnimUtils.animHide(creatorContainer);
                }
                break;

            case INPUT_COLLECTION_STATE:
                setCancelable(true);
                if (state == SHOW_COLLECTIONS_STATE) {
                    AnimUtils.animShow(creatorContainer);
                    AnimUtils.animHide(selectorContainer);
                } else if (state == CREATE_COLLECTION_STATE) {
                    AnimUtils.animShow(creatorContainer);
                    AnimUtils.animHide(progressContainer);
                }
                break;

            case CREATE_COLLECTION_STATE:
                setCancelable(false);
                if (state == INPUT_COLLECTION_STATE) {
                    AnimUtils.animShow(progressContainer);
                    AnimUtils.animHide(creatorContainer);
                }
                break;
        }
        state = newState;
    }

    // keyboard.

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) Objects.requireNonNull(getActivity())
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(nameTxt.getWindowToken(), 0);
            manager.hideSoftInputFromWindow(descriptionTxt.getWindowToken(), 0);
        }
    }

    // feedback.

    private void notifySelectCollectionResult(Collection collection, Photo photo) {
        if (-- processingCount == 0) {
            setCancelable(true);
        }
        adapter.updateItem(collection, photo);
    }

    private void notifyCreateFailed() {
        NotificationHelper.showSnackbar(getString(R.string.feedback_create_collection_failed));
    }

    // interface.

    public interface OnCollectionsChangedListener {
        void onAddCollection(Collection c);
        void onUpdateCollection(Collection c, User u, Photo p);
    }

    private void setOnCollectionsChangedListener(OnCollectionsChangedListener l) {
        listener = l;
    }

    public void setPhotoAndListener(Photo p, OnCollectionsChangedListener l) {
        photo = p;
        setOnCollectionsChangedListener(l);
    }

    // on scroll listener.

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class ElevationScrollListener extends RecyclerView.OnScrollListener {

        private int scrollY = 0;

        @Override
        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy){
            scrollY += dy;
            selectorTitleBar.setElevation(Math.min(5, scrollY));
        }
    }

    private class LoadScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy){
            if (!recyclerView.canScrollVertically(1)
                    && !AuthManager.getInstance().getCollectionsManager().isLoadFinish()) {
                refreshLayout.setLoading(true);
            }
        }
    }

    // on auth data changed listener.

    @Override
    public void onUpdateAccessToken() {
        // do nothing.
    }

    @Override
    public void onUpdateUser() {
        requestCollections(AuthManager.getInstance().getUser().username);
    }

    @Override
    public void onUpdateFailed() {
        if (AuthManager.getInstance().getUser() == null) {
            requestProfile();
        }
    }

    @Override
    public void onLogout() {
        dismiss();
    }

    // item event callback.

    @Override
    public void onCreateCollection() {
        setState(INPUT_COLLECTION_STATE);
    }

    @Override
    public void onAddPhotoToCollectionOrRemoveIt(Collection collection, Photo photo,
                                                 int adapterPosition, boolean add) {
        processingCount ++;
        setCancelable(false);
        if (add) {
            service.addPhotoToCollection(
                    collection.id,
                    photo.id,
                    new OnChangeCollectionPhotoCallback(collection, photo));
        } else {
            service.deletePhotoFromCollection(
                    collection.id,
                    photo.id,
                    new OnChangeCollectionPhotoCallback(collection, photo));
        }
    }

    private class OnRequestCollectionListCallback extends Callback<List<Collection>> {

        @Override
        public void onSucceed(List<Collection> collectionList) {
            refreshLayout.setLoading(false);
            if (collectionList.size() > 0) {
                int startPosition = AuthManager.getInstance()
                        .getCollectionsManager()
                        .getCollectionList()
                        .size() + 1;
                AuthManager.getInstance()
                        .getCollectionsManager()
                        .addCollections(collectionList);
                adapter.notifyItemRangeInserted(startPosition, collectionList.size());
            }
            if (collectionList.size() < Mysplash.DEFAULT_PER_PAGE) {
                AuthManager.getInstance().getCollectionsManager().setLoadFinish(true);
            } else {
                page ++;
                requestCollections(AuthManager.getInstance().getUser().username);
            }
        }

        @Override
        public void onFailed() {
            requestCollections(AuthManager.getInstance().getUser().username);
        }
    }

    private class OnRequestACollectionCallback extends Callback<Collection> {

        @Override
        public void onSucceed(Collection collection) {
            AuthManager.getInstance().getCollectionsManager().addCollectionToFirst(collection);
            adapter.notifyItemInserted(1);
            setState(SHOW_COLLECTIONS_STATE);
            nameTxt.setText("");
            descriptionTxt.setText("");
            checkBox.setSelected(false);
            if (listener != null) {
                listener.onAddCollection(collection);
            }
        }

        @Override
        public void onFailed() {
            setState(INPUT_COLLECTION_STATE);
            notifyCreateFailed();
        }
    }

    private class OnChangeCollectionPhotoCallback extends Callback<ChangeCollectionPhotoResult> {

        private Collection collection;
        private Photo photo;

        OnChangeCollectionPhotoCallback(Collection collection, Photo photo) {
            this.collection = collection;
            this.photo = photo;
        }

        @Override
        public void onSucceed(ChangeCollectionPhotoResult changeCollectionPhotoResult) {
            if (usable) {
                if (listener != null) {
                    listener.onUpdateCollection(
                            changeCollectionPhotoResult.collection,
                            changeCollectionPhotoResult.user,
                            changeCollectionPhotoResult.photo);
                }
                // update collection.
                AuthManager.getInstance()
                        .getCollectionsManager()
                        .updateCollection(changeCollectionPhotoResult.collection);
                AuthManager.getInstance()
                        .getCollectionsManager()
                        .finishEdit(changeCollectionPhotoResult.collection.id);
                // update user.
                AuthManager.getInstance().updateUser(changeCollectionPhotoResult.user);
                // update view.
                notifySelectCollectionResult(
                        changeCollectionPhotoResult.collection,
                        changeCollectionPhotoResult.photo
                );
            }
        }

        @Override
        public void onFailed() {
            if (usable) {
                AuthManager.getInstance().getCollectionsManager().finishEdit(collection.id);
                notifySelectCollectionResult(collection, photo);
            }
        }
    }
}
