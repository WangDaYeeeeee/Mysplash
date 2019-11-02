package com.wangdaye.collection;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.alibaba.android.arouter.facade.annotation.Route;
import com.wangdaye.base.i.Downloadable;
import com.wangdaye.collection.di.component.DaggerApplicationComponent;
import com.wangdaye.collection.ui.CollectionMenuPopupWindow;
import com.wangdaye.collection.ui.CollectionPhotosView;
import com.wangdaye.collection.ui.UpdateCollectionDialog;
import com.wangdaye.collection.vm.CollectionActivityModel;
import com.wangdaye.collection.vm.CollectionPhotosViewModel;
import com.wangdaye.common.base.activity.LoadableActivity;
import com.wangdaye.base.DownloadTask;
import com.wangdaye.base.i.PagerManageView;
import com.wangdaye.base.pager.ListPager;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.resource.Resource;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.CollectionEvent;
import com.wangdaye.common.base.vm.ParamsViewModelFactory;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.presenter.BrowsableDialogMangePresenter;
import com.wangdaye.common.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.common.presenter.pager.PagerLoadablePresenter;
import com.wangdaye.common.presenter.pager.PagerViewManagePresenter;
import com.wangdaye.common.utils.helper.RoutingHelper;
import com.wangdaye.common.ui.adapter.photo.PhotoAdapter;
import com.wangdaye.common.ui.adapter.tag.MiniTagAdapter;
import com.wangdaye.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.ui.widget.NestedScrollAppBarLayout;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.ui.widget.windowInsets.StatusBarView;
import com.wangdaye.common.utils.AnimUtils;
import com.wangdaye.common.utils.BackToTopUtils;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.utils.FileUtils;
import com.wangdaye.common.utils.ShareUtils;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.collection.base.PhotoItemEventHelper;

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

@Route(path = CollectionActivity.COLLECTION_ACTIVITY)
public class CollectionActivity extends LoadableActivity<Photo>
        implements PagerManageView, Toolbar.OnMenuItemClickListener,
        CollectionMenuPopupWindow.OnSelectItemListener, NestedScrollAppBarLayout.OnNestedScrollingListener,
        SwipeBackCoordinatorLayout.OnSwipeListener, UpdateCollectionDialog.OnCollectionChangedListener,
        DownloadRepeatDialog.OnCheckOrDownloadListener {

    @BindView(R2.id.activity_collection_statusBar) StatusBarView statusBar;
    @BindView(R2.id.activity_collection_swipeBackView) SwipeBackCoordinatorLayout swipeBackView;
    @BindView(R2.id.activity_collection_container) CoordinatorLayout container;
    @BindView(R2.id.activity_collection_shadow) View shadow;

    @BindView(R2.id.activity_collection_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R2.id.activity_collection_coverImage) AppCompatImageView cover;
    @BindView(R2.id.activity_collection_toolbar) Toolbar toolbar;
    @BindView(R2.id.activity_collection_title) TextView title;
    @BindView(R2.id.activity_collection_tagList) RecyclerView tagList;
    @BindView(R2.id.activity_collection_description) TextView description;
    @BindView(R2.id.activity_collection_avatar) CircularImageView avatar;
    @BindView(R2.id.activity_collection_subtitle) TextView subtitle;

    @OnClick(R2.id.activity_collection_touchBar) void checkAuthor() {
        try {
            ComponentFactory.getUserModule().startUserActivity(
                    this, avatar, appBar,
                    Objects.requireNonNull(getCollection()).user, ProfilePager.PAGE_PHOTO
            );
        } catch (Exception ignore) {
            // do nothing.
        }
    }

    @BindView(R2.id.activity_collection_photosView) CollectionPhotosView photosView;
    PhotoAdapter photoAdapter;

    private CollectionActivityModel activityModel;
    private CollectionPhotosViewModel photosViewModel;
    @Inject ParamsViewModelFactory viewModelFactory;

    @Inject LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter;
    private BrowsableDialogMangePresenter browsableDialogMangePresenter;
    private PagerLoadablePresenter loadMorePresenter;

    public static final String COLLECTION_ACTIVITY = "/collection/CollectionActivity";
    public static final String KEY_COLLECTION_ACTIVITY_COLLECTION = "collection_activity_collection";
    public static final String KEY_COLLECTION_ACTIVITY_ID = "collection_activity_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DaggerApplicationComponent.create().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);

        initModel();
        initView();
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
        statusBar.switchToInitAlpha();
        DisplayUtils.setStatusBarStyle(this, false);
        BackToTopUtils.showTopBar(appBar, photosView);
        photosView.scrollToPageTop();
    }

    @Override
    public void finishSelf(boolean backPressed) {
        finish();
        if (backPressed) {
            // overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
        } else {
            overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
        }
    }

    @androidx.annotation.Nullable
    @Override
    protected SwipeBackCoordinatorLayout provideSwipeBackView() {
        return swipeBackView;
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
                this, 0
        );
    }

    // init.

    private void initModel() {
        Collection collection = getIntent().getParcelableExtra(KEY_COLLECTION_ACTIVITY_COLLECTION);
        String collectionId = getIntent().getStringExtra(KEY_COLLECTION_ACTIVITY_ID);

        activityModel = ViewModelProviders.of(this, viewModelFactory).get(CollectionActivityModel.class);
        photosViewModel = ViewModelProviders.of(this, viewModelFactory).get(CollectionPhotosViewModel.class);
        if (collection != null) {
            activityModel.init(Resource.success(collection), collection.id, collection.curated);
            photosViewModel.init(
                    ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE),
                    collection.id,
                    collection.curated
            );
        } else if (!TextUtils.isEmpty(collectionId)) {
            activityModel.init(
                    Resource.loading(null),
                    Integer.parseInt(collectionId),
                    Integer.parseInt(collectionId) < 1000
            );
            photosViewModel.init(
                    ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE),
                    Integer.parseInt(collectionId),
                    Integer.parseInt(collectionId) < 1000
            );
        } else {
            activityModel.init(Resource.loading(null), 1, true);
            photosViewModel.init(
                    ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE),
                    CollectionPhotosViewModel.INVALID_COLLECTION_ID,
                    false
            );
        }
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        swipeBackView.setOnSwipeListener(this);

        appBar.setOnNestedScrollingListener(this);

        if (isTheLowestLevel()) {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_home_light, R.drawable.ic_toolbar_home_dark
            );
        } else {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark
            );
        }
        toolbar.setNavigationOnClickListener(v -> {
            if (isTheLowestLevel()) {
                ComponentFactory.getMainModule().startMainActivity(this);
            }
            finishSelf(true);
        });
        toolbar.inflateMenu(R.menu.activity_collection_toolbar);
        toolbar.setOnMenuItemClickListener(this);

        photoAdapter = new PhotoAdapter();
        photoAdapter.setItemEventCallback(
                new PhotoItemEventHelper(
                        this,
                        photoAdapter.getItemList(),
                        likeOrDislikePhotoPresenter
                )
        );
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
                    browsableDialogMangePresenter.error(this, () -> activityModel.requestACollection());
                }
                return;
            }

            browsableDialogMangePresenter.success();

            photosViewModel.setCollectionId(collection.id);
            photosViewModel.setCurated(collection.curated);

            ImageHelper.loadCollectionCover(this, cover, collection, false, null);

            title.setText(collection.title);

            if (collection.tags == null || collection.tags.size() == 0) {
                tagList.setVisibility(View.GONE);
            } else {
                tagList.setLayoutManager(
                        new LinearLayoutManager(
                                this, LinearLayoutManager.HORIZONTAL, false)
                );
                tagList.setAdapter(new MiniTagAdapter(
                        collection.tags,
                        (view, tag) -> ComponentFactory.getSearchModule().startSearchActivity(this, view, tag)
                ));
            }

            if (TextUtils.isEmpty(collection.description)) {
                description.setVisibility(View.GONE);
            } else {
                description.setText(collection.description);
            }

            toolbar.getMenu()
                    .getItem(1)
                    .setVisible(CollectionMenuPopupWindow.isUsable(this, collection));

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
                PagerViewManagePresenter.responsePagerListResourceChangedByDiffUtil(
                        resource, photosView, photoAdapter
                )
        );

        AnimUtils.translationYInitShow(photosView, 400);
    }

    // downloadTarget.

    public void downloadCollection() {
        if (getCollection() == null) {
            return;
        }

        String title = String.valueOf(getCollection().id);
        if (ComponentFactory.getDownloaderService().isDownloading(this, title)) {
            NotificationHelper.showSnackbar(this, getString(R.string.feedback_download_repeat));
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
    public void requestPermissionAndDownload(@NonNull Object target) {
        requestReadWritePermission((Downloadable) target, new RequestPermissionCallback() {
            @Override
            public void onGranted(Downloadable downloadable) {
                Context context = CollectionActivity.this;
                if (downloadable instanceof Collection) {
                    ComponentFactory.getDownloaderService().addTask(context, (Collection) downloadable);
                } else if (downloadable instanceof Photo) {
                    ComponentFactory.getDownloaderService().addTask(
                            context,
                            (Photo) downloadable,
                            DownloadTask.DOWNLOAD_TYPE,
                            ComponentFactory.getSettingsService().getDownloadScale()
                    );
                }
            }

            @Override
            public void onDenied(Downloadable downloadable) {
                NotificationHelper.showSnackbar(
                        CollectionActivity.this, getString(R.string.feedback_need_permission));
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
                photosViewModel.getListResource().getValue()
        ).state == ListResource.State.LOADING;
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_share) {
            if (getCollection() != null) {
                ShareUtils.shareCollection(getCollection());
            }
        } else if (i == R.id.action_menu) {
            new CollectionMenuPopupWindow(
                    this, toolbar, getCollection()
            ).setOnSelectItemListener(this);
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
                    ComponentFactory.getMuzeiService().setAsMuzeiSource(this, getCollection());
                }
                break;

            case CollectionMenuPopupWindow.ITEM_REMOVE_SOURCE:
                if (getCollection() != null) {
                    ComponentFactory.getMuzeiService().removeFromMuzeiSource(this, getCollection());
                }
                break;
        }
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
                statusBar.switchToInitAlpha();
                DisplayUtils.setStatusBarStyle(this, false);
            }
        } else {
            // the app bar layout has been hidden.
            if (statusBar.isInitState()) {
                statusBar.switchToDarkerAlpha();
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
    public boolean canSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
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
    public void onSwipeFinish(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
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
        RoutingHelper.startCheckCollectionActivity(this, String.valueOf(((Collection) obj).id));
    }

    @Override
    public void onDownload(Object obj) {
        requestPermissionAndDownload(obj);
    }
}