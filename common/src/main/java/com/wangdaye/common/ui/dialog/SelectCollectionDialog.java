package com.wangdaye.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wangdaye.common.R;
import com.wangdaye.common.R2;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.base.dialog.MysplashDialogFragment;
import com.wangdaye.base.pager.ListPager;
import com.wangdaye.base.unsplash.ChangeCollectionPhotoResult;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.di.component.DaggerNetworkServiceComponent;
import com.wangdaye.common.network.observer.BaseObserver;
import com.wangdaye.common.network.service.CollectionService;
import com.wangdaye.common.ui.adapter.collection.mini.CollectionMiniAdapter;
import com.wangdaye.common.ui.adapter.collection.mini.ProgressCollection;
import com.wangdaye.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.common.utils.AnimUtils;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.common.utils.manager.ThemeManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.nekocode.rxlifecycle.LifecycleEvent;
import cn.nekocode.rxlifecycle.compact.RxLifecycleCompact;
import io.reactivex.Emitter;
import io.reactivex.Observable;

/**
 * Select collection dialog.
 *
 * This dialog is used to deal some operation about collections, like add or remove photos or create
 * new collections.
 *
 * */

public class SelectCollectionDialog extends MysplashDialogFragment
        implements AuthManager.OnAuthDataChangedListener, CollectionMiniAdapter.ItemEventCallback {

    @BindView(R2.id.dialog_select_collection_container) CoordinatorLayout container;

    @BindView(R2.id.dialog_select_collection_progressContainer) RelativeLayout progressContainer;
    @BindView(R2.id.dialog_select_collection_selectorContainer) LinearLayout selectorContainer;

    @BindView(R2.id.dialog_select_collection_titleBar) RelativeLayout selectorTitleBar;
    @BindView(R2.id.dialog_select_collection_selectorRefreshView) BothWaySwipeRefreshLayout refreshLayout;

    @BindView(R2.id.dialog_select_collection_selectorRecyclerView) RecyclerView recyclerView;

    @BindView(R2.id.dialog_select_collection_creatorContainer) RelativeLayout creatorContainer;
    @BindView(R2.id.dialog_select_collection_creatorNameContainer) TextInputLayout nameTxtContainer;
    @BindView(R2.id.dialog_select_collection_creatorName) TextInputEditText nameTxt;
    @BindView(R2.id.dialog_select_collection_creatorDescription) TextInputEditText descriptionTxt;
    @BindView(R2.id.dialog_select_collection_creatorCheckBox) CheckBox checkBox;

    @OnClick(R2.id.dialog_select_collection_selectorRefreshBtn) void refresh() {
        setCancelable(true);
        initRefresh();
        setLoading(true);
    }

    @OnClick(R2.id.dialog_select_collection_creatorCreateBtn) void create() {
        hideKeyboard();
        createCollection();
    }

    @OnClick(R2.id.dialog_select_collection_creatorCancelBtn) void cancel() {
        hideKeyboard();
        setState(State.SHOW_COLLECTIONS);
    }

    private OnCollectionsChangedListener listener;

    private Photo photo;
    private int page; // HTTP request param.
    private CollectionMiniAdapter adapter;
    @Inject CollectionService collectionService;
    @Inject CollectionService createService;
    @Inject CollectionService addRemoveService;

    private State state;
    private enum State {
        SHOW_COLLECTIONS, INPUT_COLLECTION, CREATE_COLLECTION
    }

    private boolean usable; // if set false, it means the dialog has been destroyed.
    private boolean waitingAuthInformation;

    private List<Collection> editingList;

    private static final int DEFAULT_REQUEST_INTERVAL_SECOND = 5;

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
        View view = LayoutInflater.from(requireActivity()).inflate(
                R.layout.dialog_select_collection, null, false);
        ButterKnife.bind(this, view);
        initData();
        initWidget(view);
        AuthManager.getInstance().addOnWriteDataListener(this);
        if (!AuthManager.getInstance().getCollectionsManager().isLoadFinish()) {
            AuthManager.getInstance().getCollectionsManager().clearCollections();
            initRefresh();
        }
        return new AlertDialog.Builder(requireActivity())
                .setView(view)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            int height;
            if (DisplayUtils.isLandscape(Objects.requireNonNull(requireActivity()))) {
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
        collectionService.cancel();
        createService.cancel();
        AuthManager.getInstance().removeOnWriteDataListener(this);
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initData() {
        this.state = State.SHOW_COLLECTIONS;
        this.page = 0;

        this.editingList = new ArrayList<>();

        List<ProgressCollection> collectionList = new ArrayList<>(
                ProgressCollection.toProgressCollectionList(
                        AuthManager.getInstance().getCollectionsManager().getCollectionList(),
                        editingList
                )
        );
        this.adapter = new CollectionMiniAdapter(requireActivity(), collectionList, photo);
        adapter.setItemEventCallback(this);

        this.usable = true;
        this.waitingAuthInformation = false;
    }

    private void initWidget(View v) {
        setCancelable(true);

        AppCompatImageView cover = v.findViewById(R.id.dialog_select_collection_cover);
        cover.setVisibility(View.GONE);

        progressContainer.setVisibility(View.GONE);
        selectorContainer.setVisibility(View.VISIBLE);

        refreshLayout.setColorSchemeColors(ThemeManager.getContentColor(requireActivity()));
        refreshLayout.setProgressBackgroundColorSchemeColor(ThemeManager.getRootColor(requireActivity()));
        refreshLayout.setRefreshEnabled(false);
        refreshLayout.setLoadEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recyclerView.addOnScrollListener(new ElevationScrollListener());
        }
        recyclerView.addOnScrollListener(new LoadScrollListener());

        updatePhotoAdapter();

        creatorContainer.setVisibility(View.GONE);

        nameTxt.setOnFocusChangeListener((v1, hasFocus) -> nameTxtContainer.setError(null));
    }

    // control.

    private void setLoading(boolean loading) {
        refreshLayout.post(() -> refreshLayout.setLoading(loading));
    }

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
                    updatePhotoAdapter();
                }
                page = 0;
                requestCollections(AuthManager.getInstance().getUser().username);
            }
        }
    }

    private void requestProfile() {
        waitingAuthInformation = true;
        setLoading(true);
        AuthManager.getInstance().requestPersonalProfile();
    }

    private void requestCollections(String username) {
        collectionService.cancel();
        setLoading(true);

        int perPage = ListPager.DEFAULT_PER_PAGE;
        collectionService.requestUserCollections(username, page + 1, perPage, new BaseObserver<List<Collection>>() {
            @Override
            public void onSucceed(List<Collection> collectionList) {
                page ++;

                setLoading(false);

                if (collectionList.size() > 0) {
                    AuthManager.getInstance()
                            .getCollectionsManager()
                            .addCollections(collectionList);
                    updatePhotoAdapter();
                }
                if (collectionList.size() < ListPager.DEFAULT_PER_PAGE) {
                    AuthManager.getInstance().getCollectionsManager().setLoadFinish(true);
                }
            }

            @Override
            public void onFailed() {
                setLoading(false);
                Observable.create(Emitter::onComplete)
                        .compose(
                                RxLifecycleCompact.bind(SelectCollectionDialog.this)
                                        .disposeObservableWhen(LifecycleEvent.DESTROY)
                        ).delay(DEFAULT_REQUEST_INTERVAL_SECOND, TimeUnit.SECONDS)
                        .doOnComplete(() -> requestCollections(AuthManager.getInstance().getUser().username))
                        .subscribe();
            }
        });
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

        createService.createCollection(title, description, privateX, new BaseObserver<Collection>() {
            @Override
            public void onSucceed(Collection collection) {
                AuthManager.getInstance().getCollectionsManager().addCollectionToFirst(collection);
                updatePhotoAdapter();
                setState(State.SHOW_COLLECTIONS);
                nameTxt.setText("");
                descriptionTxt.setText("");
                checkBox.setSelected(false);
                if (listener != null) {
                    listener.onAddCollection(collection);
                }
            }

            @Override
            public void onFailed() {
                setState(State.INPUT_COLLECTION);
                notifyCreateFailed();
            }
        });
        setState(State.CREATE_COLLECTION);
    }

    // state.

    private void setState(State newState) {
        if (state != newState) {
            state = newState;
            switch (state) {
                case SHOW_COLLECTIONS:
                    setCancelable(true);
                    AnimUtils.animShow(selectorContainer);
                    AnimUtils.animHide(progressContainer);
                    AnimUtils.animHide(creatorContainer);
                    break;

                case INPUT_COLLECTION:
                    setCancelable(true);
                    AnimUtils.animHide(selectorContainer);
                    AnimUtils.animHide(progressContainer);
                    AnimUtils.animShow(creatorContainer);
                    break;

                case CREATE_COLLECTION:
                    setCancelable(false);
                    AnimUtils.animHide(selectorContainer);
                    AnimUtils.animShow(progressContainer);
                    AnimUtils.animHide(creatorContainer);
                    break;
            }
        }
    }

    // keyboard.

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) Objects.requireNonNull(requireActivity())
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(nameTxt.getWindowToken(), 0);
            manager.hideSoftInputFromWindow(descriptionTxt.getWindowToken(), 0);
        }
    }

    // feedback.

    private void updatePhotoAdapter() {
        List<ProgressCollection> collectionList = new ArrayList<>(
                ProgressCollection.toProgressCollectionList(
                        AuthManager.getInstance().getCollectionsManager().getCollectionList(),
                        editingList
                )
        );

        adapter.update(photo, collectionList);
    }

    private void notifySelectCollectionResult(Collection collection) {
        for (int i = 0; i < editingList.size(); i ++) {
            if (editingList.get(i).id == collection.id) {
                editingList.remove(i);
                break;
            }
        }
        if (editingList.size() == 0) {
            setCancelable(true);
        }

        updatePhotoAdapter();
    }

    private void notifyCreateFailed() {
        NotificationHelper.showSnackbar(
                (MysplashActivity) requireActivity(),
                getString(R.string.feedback_create_collection_failed)
        );
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
        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
            if (recyclerView.getLayoutManager() == null) {
                return;
            }
            int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findLastVisibleItemPosition();

            if (!refreshLayout.isLoading()
                    && !AuthManager.getInstance().getCollectionsManager().isLoadFinish()
                    && lastVisibleItem >= adapter.getItemCount() - ListPager.DEFAULT_PER_PAGE
                    && dy > 0) {
                requestCollections(AuthManager.getInstance().getUser().username);
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
        if (waitingAuthInformation) {
            waitingAuthInformation = false;
            requestCollections(AuthManager.getInstance().getUser().username);
        }
    }

    @Override
    public void onUpdateFailed() {
        if (AuthManager.getInstance().getUser() == null) {
            Observable.create(Emitter::onComplete)
                    .compose(RxLifecycleCompact.bind(this).disposeObservableWhen(LifecycleEvent.DESTROY))
                    .delay(DEFAULT_REQUEST_INTERVAL_SECOND, TimeUnit.SECONDS)
                    .doOnComplete(this::requestProfile)
                    .subscribe();
        }
    }

    @Override
    public void onLogout() {
        dismiss();
    }

    // item event callback.

    @Override
    public void onCreateCollection() {
        setState(State.INPUT_COLLECTION);
    }

    @Override
    public void onAddPhotoToCollectionOrRemoveIt(Collection collection, Photo photo,
                                                 int adapterPosition, boolean add) {
        for (int i = 0; i < editingList.size(); i ++) {
            if (editingList.get(i).id == collection.id) {
                return;
            }
        }

        editingList.add(collection);
        updatePhotoAdapter();
        setCancelable(false);

        if (add) {
            addRemoveService.addPhotoToCollection(
                    collection.id,
                    photo.id,
                    new OnChangeCollectionPhotoObserver(collection, true)
            );
        } else {
            addRemoveService.deletePhotoFromCollection(
                    collection.id,
                    photo.id,
                    new OnChangeCollectionPhotoObserver(collection, false)
            );
        }
    }

    private class OnChangeCollectionPhotoObserver extends BaseObserver<ChangeCollectionPhotoResult> {

        private Collection collection;
        private boolean add;

        OnChangeCollectionPhotoObserver(Collection collection, boolean add) {
            this.collection = collection;
            this.add = add;
        }

        @Override
        public void onSucceed(ChangeCollectionPhotoResult changeCollectionPhotoResult) {
            if (usable) {
                if (photo.current_user_collections != null) {
                    changeCollectionPhotoResult.photo.current_user_collections = new ArrayList<>(
                            photo.current_user_collections);
                } else {
                    changeCollectionPhotoResult.photo.current_user_collections = new ArrayList<>();
                }

                photo = changeCollectionPhotoResult.photo;

                if (add) {
                    photo.current_user_collections.add(changeCollectionPhotoResult.collection);
                } else {
                    for (int i = 0; i < photo.current_user_collections.size(); i ++) {
                        if (photo.current_user_collections.get(i).id == changeCollectionPhotoResult.collection.id) {
                            photo.current_user_collections.remove(i);
                            break;
                        }
                    }
                }

                if (listener != null) {
                    listener.onUpdateCollection(
                            changeCollectionPhotoResult.collection,
                            changeCollectionPhotoResult.user,
                            photo
                    );
                }
                // update collection.
                AuthManager.getInstance()
                        .getCollectionsManager()
                        .updateCollection(changeCollectionPhotoResult.collection);
                // update user.
                AuthManager.getInstance().updateUser(changeCollectionPhotoResult.user);

                // update view.
                notifySelectCollectionResult(changeCollectionPhotoResult.collection);
            }
        }

        @Override
        public void onFailed() {
            if (usable) {
                notifySelectCollectionResult(collection);
            }
        }
    }
}
