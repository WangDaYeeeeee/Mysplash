package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.ChangeCollectionPhotoResult;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Me;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.data.service.CollectionService;
import com.wangdaye.mysplash.common._basic.fragment.MysplashDialogFragment;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.ui.adapter.CollectionMiniAdapter;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Select collection dialog.
 *
 * This dialog is used to deal some operation about collections, like add or remove photos or create
 * new collections.
 *
 * */

public class SelectCollectionDialog extends MysplashDialogFragment
        implements AuthManager.OnAuthDataChangedListener,
        CollectionMiniAdapter.OnCollectionResponseListener, CollectionService.OnRequestACollectionListener {

    @BindView(R.id.dialog_select_collection_container)
    CoordinatorLayout container;

    @BindView(R.id.dialog_select_collection_progressContainer)
    RelativeLayout progressContainer;

    @BindView(R.id.dialog_select_collection_selectorContainer)
    LinearLayout selectorContainer;

    @BindView(R.id.dialog_select_collection_titleBar)
    RelativeLayout selectorTitleBar;

    @BindView(R.id.dialog_select_collection_selectorRefreshView)
    BothWaySwipeRefreshLayout refreshLayout;

    @BindView(R.id.dialog_select_collection_selectorRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.dialog_select_collection_creatorContainer)
    RelativeLayout creatorContainer;

    @BindView(R.id.dialog_select_collection_creatorName)
    EditText nameTxt;

    @BindView(R.id.dialog_select_collection_creatorDescription)
    EditText descriptionTxt;

    @BindView(R.id.dialog_select_collection_creatorCheckBox)
    CheckBox checkBox;

    private OnCollectionsChangedListener listener;

    private Me me;
    private Photo photo;
    private int page; // HTTP request param.
    private CollectionMiniAdapter adapter;
    private CollectionService service;
    private OnRequestCollectionsListener serviceListener;

    @StateRule
    private int state;

    private static final int SHOW_COLLECTIONS_STATE = 0;
    private static final int INPUT_COLLECTION_STATE = 1;
    private static final int CREATE_COLLECTION_STATE = 2;
    @IntDef({SHOW_COLLECTIONS_STATE, INPUT_COLLECTION_STATE, CREATE_COLLECTION_STATE})
    private @interface StateRule {}

    private boolean usable; // if set false, it means the dialog has been destroyed.

    private int processingCount;

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
        Window window = getDialog().getWindow();
        if (window != null) {
            int height;
            if (DisplayUtils.isLandscape(getActivity())) {
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
        AuthManager.getInstance().removeOnWriteDataListener(this);
        AuthManager.getInstance().getCollectionsManager().finishEdit();
        if (serviceListener != null) {
            serviceListener.cancel();
        }
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initData() {
        this.me = AuthManager.getInstance().getMe();
        this.service = CollectionService.getService();
        this.state = SHOW_COLLECTIONS_STATE;
        this.page = 1;

        this.adapter = new CollectionMiniAdapter(getActivity(), photo);
        adapter.setOnCollectionResponseListener(this);

        this.usable = true;
        this.processingCount = 0;
    }

    private void initWidget(View v) {
        setCancelable(true);

        ImageView cover = ButterKnife.findById(v, R.id.dialog_select_collection_cover);
        if (DisplayUtils.isTabletDevice(getActivity())) {
            ImageHelper.loadRegularPhoto(getActivity(), cover, photo, null);
        } else {
            cover.setVisibility(View.GONE);
        }

        progressContainer.setVisibility(View.GONE);
        selectorContainer.setVisibility(View.VISIBLE);

        ImageButton refreshBtn = ButterKnife.findById(v, R.id.dialog_select_collection_selectorRefreshBtn);
        ThemeManager.setImageResource(refreshBtn, R.drawable.ic_refresh_light, R.drawable.ic_refresh_dark);

        refreshLayout.setColorSchemeColors(ThemeManager.getContentColor(getActivity()));
        refreshLayout.setProgressBackgroundColorSchemeColor(ThemeManager.getRootColor(getActivity()));
        refreshLayout.setPermitRefresh(false);
        refreshLayout.setPermitLoad(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recyclerView.addOnScrollListener(new ElevationScrollListener());
        }
        recyclerView.addOnScrollListener(new LoadScrollListener());

        creatorContainer.setVisibility(View.GONE);

        DisplayUtils.setTypeface(getActivity(), nameTxt);
        DisplayUtils.setTypeface(getActivity(), descriptionTxt);
        DisplayUtils.setTypeface(getActivity(), checkBox);
    }

    // control.

    // HTTP request.

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
        AuthManager.getInstance().requestPersonalProfile();
    }

    private void requestCollections() {
        if (serviceListener != null) {
            serviceListener.cancel();
        }
        service.cancel();

        serviceListener = new OnRequestCollectionsListener();
        service.requestUserCollections(me.username, page, Mysplash.DEFAULT_PER_PAGE, serviceListener);
    }

    private void createCollection() {
        String title = nameTxt.getText().toString();
        if (TextUtils.isEmpty(title)) {
            NotificationHelper.showSnackbar(getString(R.string.feedback_name_is_required));
        } else {
            String description = TextUtils.isEmpty(descriptionTxt.getText().toString()) ?
                    null : descriptionTxt.getText().toString();
            boolean privateX = checkBox.isChecked();
            service.createCollection(
                    title,
                    description,
                    privateX,
                    this);
            setState(CREATE_COLLECTION_STATE);
        }
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
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(nameTxt.getWindowToken(), 0);
        manager.hideSoftInputFromWindow(descriptionTxt.getWindowToken(), 0);
    }

    // feedback.

    /**
     * Update item view when a HTTP request completed.
     * For example, add a photo to a collection successful.
     *
     * @param collectionId Collection id.
     * @param add          if set true, it means add photo to a collection. Otherwise, it means
     *                     remove photo from a collection.
     * @param succeed      if set true, it means HTTP request successful, otherwise failed.
     * */
    private void notifySelectCollectionResult(int collectionId, boolean add, boolean succeed) {
        if (-- processingCount == 0) {
            setCancelable(true);
        }

        for (int i = 0;
             i < AuthManager.getInstance().getCollectionsManager().getCollectionList().size();
             i ++) {
            if (AuthManager.getInstance().getCollectionsManager().getCollectionList().get(i).id == collectionId) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int position = i + 1;
                int firstPosition = layoutManager.findFirstVisibleItemPosition();
                int lastPosition = layoutManager.findLastVisibleItemPosition();
                if (firstPosition <= position && position <= lastPosition) {
                    CollectionMiniAdapter.ViewHolder holder
                            = (CollectionMiniAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                    Collection collection
                            = AuthManager.getInstance().getCollectionsManager().getCollectionList().get(i);
                    holder.reloadCoverImage(collection);
                    if (succeed) {
                        holder.setSubtitle(collection);
                        if (add) {
                            holder.setResultState(R.drawable.ic_item_state_succeed);
                        } else {
                            holder.setResultState(android.R.color.transparent);
                        }
                    } else {
                        if (add) {
                            holder.setResultState(android.R.color.transparent);
                            NotificationHelper.showSnackbar(getString(R.string.feedback_add_photo_failed));
                        } else {
                            holder.setResultState(R.drawable.ic_item_state_succeed);
                            NotificationHelper.showSnackbar(getString(R.string.feedback_delete_photo_failed));
                        }
                    }
                }
                return;
            }
        }
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

    // on click listener.

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

    // on scroll listener.

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class ElevationScrollListener extends RecyclerView.OnScrollListener {

        private int scrollY = 0;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy){
            scrollY += dy;
            selectorTitleBar.setElevation(Math.min(5, scrollY));
        }
    }

    private class LoadScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy){
            if (!ViewCompat.canScrollVertically(recyclerView, 1)
                    && !AuthManager.getInstance().getCollectionsManager().isLoadFinish()) {
                refreshLayout.setLoading(true);
            }
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

    // on collection response listener (recycler view adapter item click).

    @Override
    public void onCreateCollection() {
        setState(INPUT_COLLECTION_STATE);
    }

    @Override
    public void onClickCollectionItem(int collectionId, int adapterPosition) {
        processingCount ++;
        setCancelable(false);

        CollectionMiniAdapter.ViewHolder holder
                = (CollectionMiniAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(adapterPosition);
        holder.setProgressState();
        for (int i = 0; i < photo.current_user_collections.size(); i ++) {
            if (collectionId == photo.current_user_collections.get(i).id) {
                service.deletePhotoFromCollection(
                        collectionId,
                        photo.id,
                        new OnChangeCollectionPhotoListener(collectionId, false));
                return;
            }
        }
        service.addPhotoToCollection(
                collectionId,
                photo.id,
                new OnChangeCollectionPhotoListener(collectionId, true));
    }

    // on request collections listener (request collections list.).

    private class OnRequestCollectionsListener
            implements CollectionService.OnRequestCollectionsListener {

        private boolean canceled;

        OnRequestCollectionsListener() {
            canceled = false;
        }

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
                refreshLayout.setLoading(false);
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
                if (response.body().size() < Mysplash.DEFAULT_PER_PAGE) {
                    AuthManager.getInstance().getCollectionsManager().setLoadFinish(true);
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
            if (canceled) {
                return;
            }
            requestCollections();
        }
    }

    // on request a collection listener (create collection).

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

    // on change collection photo listener (add photo or delete photo).

    private class OnChangeCollectionPhotoListener
            implements CollectionService.OnChangeCollectionPhotoListener {

        private int collectionId;
        private boolean add;

        OnChangeCollectionPhotoListener(int collectionId, boolean add) {
            this.collectionId = collectionId;
            this.add = add;
        }

        @Override
        public void onChangePhotoSuccess(Call<ChangeCollectionPhotoResult> call,
                                         Response<ChangeCollectionPhotoResult> response) {
            if (usable) {
                if (response.isSuccessful() && response.body() != null) {
                    if (listener != null) {
                        listener.onUpdateCollection(
                                response.body().collection,
                                response.body().user,
                                response.body().photo);
                    }
                    // update collection.
                    AuthManager.getInstance().getCollectionsManager().updateCollection(response.body().collection);
                    AuthManager.getInstance().getCollectionsManager().finishEdit(response.body().collection.id);
                    // update user.
                    AuthManager.getInstance().updateUser(response.body().user);
                    // update photo.
                    photo = response.body().photo;
                    adapter.updatePhoto(photo);
                    // update view.
                    notifySelectCollectionResult(response.body().collection.id, add, true);
                } else {
                    notifySelectCollectionResult(response.body().collection.id, add, false);
                }
            }
        }

        @Override
        public void onChangePhotoFailed(Call<ChangeCollectionPhotoResult> call, Throwable t) {
            if (usable) {
                AuthManager.getInstance().getCollectionsManager().finishEdit(collectionId);
                notifySelectCollectionResult(collectionId, add, false);
            }
        }
    }
}
