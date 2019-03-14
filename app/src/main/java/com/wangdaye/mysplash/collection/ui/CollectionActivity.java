package com.wangdaye.mysplash.collection.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.widget.AppCompatImageView;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.collection.vm.CollectionActivityModel;
import com.wangdaye.mysplash.collection.vm.CollectionPhotosViewModel;
import com.wangdaye.mysplash.common.basic.DaggerViewModelFactory;
import com.wangdaye.mysplash.common.basic.activity.LoadableActivity;
import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.model.Resource;
import com.wangdaye.mysplash.common.db.WallpaperSource;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.download.imp.DownloaderService;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.ui.adapter.MiniTagAdapter;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.common.utils.ShareUtils;
import com.wangdaye.mysplash.common.db.DatabaseHelper;
import com.wangdaye.mysplash.common.download.DownloadHelper;
import com.wangdaye.mysplash.common.download.NotificationHelper;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.utils.bus.CollectionEvent;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.ui.dialog.UpdateCollectionDialog;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.bus.MessageBus;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.presenter.BrowsableDialogMangePresenter;
import com.wangdaye.mysplash.common.utils.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.mysplash.common.utils.presenter.pager.PagerLoadablePresenter;
import com.wangdaye.mysplash.common.utils.presenter.pager.PagerViewManagePresenter;
import com.wangdaye.mysplash.user.ui.UserActivity;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Collection activity.
 *
 * This activity is used to show a collection.
 *
 * */

public class CollectionActivity extends LoadableActivity<Photo>
        implements PagerManageView, Toolbar.OnMenuItemClickListener,
        CollectionMenuPopupWindow.OnSelectItemListener, PhotoAdapter.ItemEventCallback,
        NestedScrollAppBarLayout.OnNestedScrollingListener, SwipeBackCoordinatorLayout.OnSwipeListener,
        UpdateCollectionDialog.OnCollectionChangedListener,
        DownloadRepeatDialog.OnCheckOrDownloadListener {

    @BindView(R.id.activity_collection_statusBar) StatusBarView statusBar;
    @BindView(R.id.activity_collection_container) CoordinatorLayout container;
    @BindView(R.id.activity_collection_shadow) View shadow;

    @BindView(R.id.activity_collection_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R.id.activity_collection_coverImage) AppCompatImageView cover;
    @BindView(R.id.activity_collection_toolbar) Toolbar toolbar;
    @BindView(R.id.activity_collection_title) TextView title;
    @BindView(R.id.activity_collection_tagList) RecyclerView tagList;
    @BindView(R.id.activity_collection_description) TextView description;
    @BindView(R.id.activity_collection_avatar) CircleImageView avatar;
    @BindView(R.id.activity_collection_subtitle) TextView subtitle;
    @OnClick(R.id.activity_collection_touchBar) void checkAuthor() {
        try {
            IntentHelper.startUserActivity(
                    this, avatar, appBar,
                    Objects.requireNonNull(getCollection()).user, UserActivity.PAGE_PHOTO);
        } catch (Exception ignore) {
            // do nothing.
        }
    }

    @BindView(R.id.activity_collection_photosView) CollectionPhotosView photosView;
    private PhotoAdapter photoAdapter;

    private CollectionActivityModel activityModel;
    private CollectionPhotosViewModel photosViewModel;
    @Inject DaggerViewModelFactory viewModelFactory;

    @Inject LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter;
    private BrowsableDialogMangePresenter browsableDialogMangePresenter;
    private PagerLoadablePresenter loadMorePresenter;

    public static final String KEY_COLLECTION_ACTIVITY_COLLECTION = "collection_activity_collection";
    public static final String KEY_COLLECTION_ACTIVITY_ID = "collection_activity_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Mysplash.getInstance().finishSameActivity(getClass());
        }
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);
        initModel();
        initView();
    }

    @Override
    protected void setTheme() {
        if (DisplayUtils.isLandscape(this)) {
            DisplayUtils.cancelTranslucentNavigation(this);
        }
    }

    @Override
    public boolean hasTranslucentNavigationBar() {
        return true;
    }

    @Override
    public void handleBackPressed() {
        if (photosView.checkNeedBackToTop()
                && BackToTopUtils.isSetBackToTop(false)) {
            backToTop();
        } else {
            finishSelf(true);
        }
    }

    @Override
    public void backToTop() {
        statusBar.animToInitAlpha();
        DisplayUtils.setStatusBarStyle(this, false);
        BackToTopUtils.showTopBar(appBar, photosView);
        photosView.scrollToPageTop();
    }

    @Override
    public void finishSelf(boolean backPressed) {
        finish();
        if (backPressed) {
            overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
        } else {
            overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
        }
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    @Override
    public List<Photo> loadMoreData(List<Photo> list, int headIndex, boolean headDirection) {
        return loadMorePresenter.loadMore(
                list, headIndex, headDirection,
                photosView, photosView.getRecyclerView(), photoAdapter,
                this, 0);
    }

    // init.

    private void initModel() {
        Collection collection = getIntent().getParcelableExtra(KEY_COLLECTION_ACTIVITY_COLLECTION);
        String collectionId = getIntent().getStringExtra(CollectionActivity.KEY_COLLECTION_ACTIVITY_ID);

        activityModel = ViewModelProviders.of(this, viewModelFactory).get(CollectionActivityModel.class);
        photosViewModel = ViewModelProviders.of(this, viewModelFactory).get(CollectionPhotosViewModel.class);
        if (collection != null) {
            activityModel.init(Resource.success(collection), collection.id, collection.curated);
            photosViewModel.init(
                    ListResource.refreshing(0, Mysplash.DEFAULT_PER_PAGE),
                    collection.id,
                    collection.curated);
        } else if (!TextUtils.isEmpty(collectionId)) {
            activityModel.init(
                    Resource.loading(null),
                    Integer.parseInt(collectionId),
                    Integer.parseInt(collectionId) < 1000);
            photosViewModel.init(
                    ListResource.refreshing(0, Mysplash.DEFAULT_PER_PAGE),
                    Integer.parseInt(collectionId),
                    Integer.parseInt(collectionId) < 1000);
        } else {
            activityModel.init(Resource.loading(null), 1, true);
            photosViewModel.init(
                    ListResource.refreshing(0, Mysplash.DEFAULT_PER_PAGE),
                    CollectionPhotosViewModel.INVALID_COLLECTION_ID,
                    false);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        SwipeBackCoordinatorLayout swipeBackView = findViewById(R.id.activity_collection_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        appBar.setOnNestedScrollingListener(this);

        if (isTheLowestLevel()) {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_home_light, R.drawable.ic_toolbar_home_dark);
        } else {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        }
        toolbar.setNavigationOnClickListener(v -> {
            if (isTheLowestLevel()) {
                IntentHelper.startMainActivity(this);
            }
            finishSelf(true);
        });
        toolbar.inflateMenu(R.menu.activity_collection_toolbar);
        toolbar.setOnMenuItemClickListener(this);

        photoAdapter = new PhotoAdapter(
                this, 
                Objects.requireNonNull(photosViewModel.getListResource().getValue()).dataList,
                DisplayUtils.getGirdColumnCount(this));
        photoAdapter.setItemEventCallback(this);
        photosView.setPhotoAdapter(photoAdapter);
        photosView.setPagerManageView(this);

        browsableDialogMangePresenter = new BrowsableDialogMangePresenter() {
            @Override
            public void finishActivity() {
                finishSelf(true);
            }
        };
        loadMorePresenter = new PagerLoadablePresenter() {
            @Override
            public List<Photo> subList(int fromIndex, int toIndex) {
                return photosViewModel.getListResource().getValue().dataList.subList(fromIndex, toIndex);
            }
        };

        activityModel.getResource().observe(this, resource -> {
            Collection collection = resource.data;
            if (collection == null) {
                if (resource.status == Resource.Status.LOADING) {
                    browsableDialogMangePresenter.load(this);
                } else {
                    browsableDialogMangePresenter.error(this, () ->
                            activityModel.requestACollection());
                }
                return;
            }

            browsableDialogMangePresenter.success();

            photosViewModel.setCollectionId(collection.id);
            photosViewModel.setCurated(collection.curated);

            ImageHelper.loadCollectionCover(this, cover, collection, null);

            title.setText(collection.title);

            if (collection.tags == null || collection.tags.size() == 0) {
                tagList.setVisibility(View.GONE);
            } else {
                tagList.setLayoutManager(
                        new LinearLayoutManager(
                                this, LinearLayoutManager.HORIZONTAL, false));
                tagList.setAdapter(new MiniTagAdapter(collection.tags));
            }

            if (TextUtils.isEmpty(collection.description)) {
                description.setVisibility(View.GONE);
            } else {
                description.setText(collection.description);
            }

            toolbar.getMenu().getItem(1).setVisible(
                    CollectionMenuPopupWindow.isUsable(this, collection));

            ImageHelper.loadAvatar(this, avatar, collection.user, null);

            subtitle.setText(getString(R.string.by) + " " + collection.user.name);

            boolean myCollection = !TextUtils.isEmpty(AuthManager.getInstance().getUsername())
                    && AuthManager.getInstance().getUsername().equals(collection.user.username);
            photoAdapter.setShowDeleteButton(myCollection);
            if (photosViewModel.getListResource().getValue().dataList.size() == 0
                    && photosViewModel.getListResource().getValue().state != ListResource.State.REFRESHING
                    && photosViewModel.getListResource().getValue().state != ListResource.State.LOADING) {
                PagerViewManagePresenter.initRefresh(photosViewModel, photoAdapter);
            }
        });
        activityModel.getDeleted().observe(this, deleted -> {
            if (deleted) {
                finishSelf(true);
            }
        });

        photosViewModel.getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(resource, photosView, photoAdapter));

        AnimUtils.translationYInitShow(photosView, 400);
    }

    // downloadTarget.

    public void downloadCollection() {
        if (getCollection() == null) {
            return;
        }
        if (DatabaseHelper.getInstance(this).readDownloadingEntityCount(String.valueOf(getCollection().id)) > 0) {
            NotificationHelper.showSnackbar(getString(R.string.feedback_download_repeat));
        } else if (FileUtils.isCollectionExists(this, String.valueOf(getCollection().id))) {
            DownloadRepeatDialog dialog = new DownloadRepeatDialog();
            dialog.setDownloadKey(getCollection());
            dialog.setOnCheckOrDownloadListener(this);
            dialog.show(getSupportFragmentManager(), null);
        } else {
            requestPermissionAndDownload(getCollection());
        }
    }

    /**
     * @param target {@link Photo} or {@link Collection}.
     * */
    private void requestPermissionAndDownload(@NonNull Object target) {
        requestReadWritePermission((Downloadable) target, downloadable -> {
            if (downloadable instanceof Collection) {
                DownloadHelper.getInstance(this)
                        .addMission(this, ((Collection) downloadable));
            } else if (downloadable instanceof Photo) {
                DownloadHelper.getInstance(this)
                        .addMission(this, (Photo) downloadable, DownloaderService.DOWNLOAD_TYPE);
            }
        });
    }

    // data.

    @Nullable
    public Collection getCollection() {
        return Objects.requireNonNull(activityModel.getResource().getValue()).data;
    }

    // interface.

    // pager manage view.

    @Override
    public void onRefresh(int index) {
        photosViewModel.refresh();
    }

    @Override
    public void onLoad(int index) {
        photosViewModel.load();
    }

    @Override
    public boolean canLoadMore(int index) {
        return photosViewModel.getListResource().getValue() != null
                && photosViewModel.getListResource().getValue().state != ListResource.State.REFRESHING
                && photosViewModel.getListResource().getValue().state != ListResource.State.LOADING
                && photosViewModel.getListResource().getValue().state != ListResource.State.ALL_LOADED;
    }

    @Override
    public boolean isLoading(int index) {
        return Objects.requireNonNull(
                photosViewModel.getListResource().getValue()).state == ListResource.State.LOADING;
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share: {
                if (getCollection() != null) {
                    ShareUtils.shareCollection(getCollection());
                }
                break;
            }
            case R.id.action_menu: {
                CollectionMenuPopupWindow window = new CollectionMenuPopupWindow(
                        this, toolbar, getCollection());
                window.setOnSelectItemListener(this);
                break;
            }
        }
        return true;
    }

    @Override
    public void onSelectItem(int id) {
        switch (id) {
            case CollectionMenuPopupWindow.ITEM_EDIT:
                UpdateCollectionDialog dialog = new UpdateCollectionDialog();
                dialog.setCollection(getCollection());
                dialog.setOnCollectionChangedListener(this);
                dialog.show(getSupportFragmentManager(), null);
                break;

            case CollectionMenuPopupWindow.ITEM_DOWNLOAD:
                downloadCollection();
                break;

            case CollectionMenuPopupWindow.ITEM_SET_AS_SOURCE:
                if (getCollection() != null) {
                    DatabaseHelper.getInstance(this)
                            .writeWallpaperSource(new WallpaperSource(getCollection()));
                }
                break;

            case CollectionMenuPopupWindow.ITEM_REMOVE_SOURCE:
                if (getCollection() != null) {
                    DatabaseHelper.getInstance(this).deleteWallpaperSource(getCollection().id);
                }
                break;
        }
    }

    // item event callback.

    @Override
    public void onLikeOrDislikePhoto(Photo photo, int adapterPosition, boolean setToLike) {
        likeOrDislikePhotoPresenter.likeOrDislikePhoto(photo, setToLike);
    }

    @Override
    public void onDownload(Photo photo) {
        requestPermissionAndDownload(photo);
    }

    // on nested scrolling listener.

    @Override
    public void onStartNestedScroll() {
        // do nothing.
    }

    @Override
    public void onNestedScrolling() {
        if (appBar.getY() > -appBar.getMeasuredHeight()) {
            // the app bar layout can be seen.
            if (!statusBar.isInitState()) {
                statusBar.animToInitAlpha();
                DisplayUtils.setStatusBarStyle(this, false);
            }
        } else {
            // the app bar layout has been hidden.
            if (statusBar.isInitState()) {
                statusBar.animToDarkerAlpha();
                DisplayUtils.setStatusBarStyle(this, true);
            }
        }
    }

    @Override
    public void onStopNestedScroll() {
        // do nothing.
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        if (dir == SwipeBackCoordinatorLayout.UP_DIR) {
            return photosView.canSwipeBack(dir)
                    && appBar.getY() <= -appBar.getMeasuredHeight();
        } else {
            return photosView.canSwipeBack(dir)
                    && appBar.getY() >= 0;
        }
    }

    @Override
    public void onSwipeProcess(float percent) {
        shadow.setAlpha(SwipeBackCoordinatorLayout.getBackgroundAlpha(percent));
    }

    @Override
    public void onSwipeFinish(int dir) {
        finishSelf(false);
    }

    // on collection changed listener.

    @Override
    public void onEditCollection(Collection c) {
        AuthManager.getInstance().getCollectionsManager().updateCollection(c);
        MessageBus.getInstance().post(new CollectionEvent(c, CollectionEvent.Event.UPDATE));
    }

    @Override
    public void onDeleteCollection(Collection c) {
        AuthManager.getInstance().getCollectionsManager().deleteCollection(c);
        MessageBus.getInstance().post(new CollectionEvent(c, CollectionEvent.Event.DELETE));

        User user = AuthManager.getInstance().getUser();
        if (user != null) {
            user.total_collections --;
            MessageBus.getInstance().post(user);
        }
    }

    // on check or downloadTarget listener. (Collection)

    @Override
    public void onCheck(Object obj) {
        IntentHelper.startCheckCollectionActivity(this, String.valueOf(((Collection) obj).id));
    }

    @Override
    public void onDownload(Object obj) {
        requestPermissionAndDownload(obj);
    }
}