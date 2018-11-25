package com.wangdaye.mysplash.collection.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.collection.presenter.activity.PopupManageImplementor;
import com.wangdaye.mysplash.common.basic.activity.LoadableActivity;
import com.wangdaye.mysplash.common.data.entity.table.WallpaperSource;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.i.model.DownloadModel;
import com.wangdaye.mysplash.common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash.common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash.common.i.view.PopupManageView;
import com.wangdaye.mysplash.common.ui.adapter.MiniTagAdapter;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.mysplash.common.ui.popup.CollectionMenuPopupWindow;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.i.model.BrowsableModel;
import com.wangdaye.mysplash.common.i.model.EditResultModel;
import com.wangdaye.mysplash.common.i.presenter.BrowsablePresenter;
import com.wangdaye.mysplash.common.i.presenter.EditResultPresenter;
import com.wangdaye.mysplash.common.i.presenter.SwipeBackManagePresenter;
import com.wangdaye.mysplash.common.i.view.BrowsableView;
import com.wangdaye.mysplash.common.i.view.EditResultView;
import com.wangdaye.mysplash.common.i.view.SwipeBackManageView;
import com.wangdaye.mysplash.common.ui.dialog.RequestBrowsableDataDialog;
import com.wangdaye.mysplash.common.ui.dialog.UpdateCollectionDialog;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.collection.model.activity.BorwsableObject;
import com.wangdaye.mysplash.collection.model.activity.DownloadObject;
import com.wangdaye.mysplash.collection.model.activity.EditResultObject;
import com.wangdaye.mysplash.collection.presenter.activity.BrowsableImplementor;
import com.wangdaye.mysplash.collection.presenter.activity.DownloadImplementor;
import com.wangdaye.mysplash.collection.presenter.activity.EditResultImplementor;
import com.wangdaye.mysplash.collection.presenter.activity.SwipeBackManageImplementor;
import com.wangdaye.mysplash.collection.presenter.activity.ToolbarImplementor;
import com.wangdaye.mysplash.collection.view.widget.CollectionPhotosView;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.me.view.activity.MeActivity;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.List;

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
        implements SwipeBackManageView, PopupManageView, EditResultView, BrowsableView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, PhotoAdapter.OnDownloadPhotoListener,
        NestedScrollAppBarLayout.OnNestedScrollingListener, SwipeBackCoordinatorLayout.OnSwipeListener,
        UpdateCollectionDialog.OnCollectionChangedListener,
        DownloadRepeatDialog.OnCheckOrDownloadListener {

    @BindView(R.id.activity_collection_statusBar)
    StatusBarView statusBar;

    @BindView(R.id.activity_collection_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_collection_shadow)
    View shadow;

    @BindView(R.id.activity_collection_appBar)
    NestedScrollAppBarLayout appBar;

    @BindView(R.id.activity_collection_toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_collection_avatar)
    CircleImageView avatarImage;

    @BindView(R.id.activity_collection_photosView)
    CollectionPhotosView photosView;

    private RequestBrowsableDataDialog requestDialog;
    
    private ToolbarPresenter toolbarPresenter;

    private SwipeBackManagePresenter swipeBackManagePresenter;

    private PopupManagePresenter popupManagePresenter;

    private EditResultModel editResultModel;
    private EditResultPresenter editResultPresenter;

    private BrowsableModel browsableModel;
    private BrowsablePresenter browsablePresenter;

    private DownloadModel downloadModel;
    private DownloadPresenter downloadPresenter;

    public static final String KEY_COLLECTION_ACTIVITY_COLLECTION = "collection_activity_collection";
    public static final String KEY_COLLECTION_ACTIVITY_ID = "collection_activity_id";

    public static class SavedStateFragment extends BaseSavedStateFragment {

        private List<Photo> photoList;

        public List<Photo> getPhotoList() {
            return photoList;
        }

        public void setPhotoList(List<Photo> photoList) {
            this.photoList = photoList;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Mysplash.getInstance().finishSameActivity(getClass());
        }

        setContentView(R.layout.activity_collection);
        initModel();
        initPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            ButterKnife.bind(this);
            initView(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        browsablePresenter.cancelRequest();
        photosView.cancelRequest();
    }

    @Override
    protected void setTheme() {
        if (ThemeManager.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_TranslucentNavigation_Collection);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_TranslucentNavigation_Collection);
        }

        if (DisplayUtils.isLandscape(this)) {
            DisplayUtils.cancelTranslucentNavigation(this);
        }
    }

    @Override
    public boolean hasTranslucentNavigationBar() {
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save large data.
        SavedStateFragment f = new SavedStateFragment();
        f.setPhotoList(photosView.getPhotos());
        f.saveData(this);

        // save normal data.
        super.onSaveInstanceState(outState);
    }

    @Override
    public void handleBackPressed() {
        if (photosView.needPagerBackToTop()
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
        photosView.pagerBackToTop();
    }

    @Override
    public void finishSelf(boolean backPressed) {
        Intent result = new Intent();
        // told parent if this collection was deleted.
        result.putExtra(
                MeActivity.KEY_ME_ACTIVITY_DELETE_COLLECTION,
                editResultPresenter.getEditKey() == null);
        // told parent if this collection is created by the user.
        result.putExtra(
                MeActivity.KEY_ME_ACTIVITY_COLLECTION,
                getIntent().getParcelableExtra(KEY_COLLECTION_ACTIVITY_COLLECTION));
        setResult(RESULT_OK, result);
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
    public List<Photo> loadMoreData(List<Photo> list, int headIndex, boolean headDirection, Bundle bundle) {
        return photosView.loadMore(list, headIndex, headDirection);
    }

    @Override
    public Bundle getBundleOfList() {
        return new Bundle();
    }

    @Override
    public void updateData(Photo photo) {
        photosView.updatePhoto(photo, true);
    }

    // init.

    private void initModel() {
        Object o = getIntent().getParcelableExtra(KEY_COLLECTION_ACTIVITY_COLLECTION);
        if (!(o instanceof Collection)) {
            this.editResultModel = new EditResultObject(null);
        } else {
            this.editResultModel = new EditResultObject((Collection) o);
        }

        this.browsableModel = new BorwsableObject(getIntent());
        this.downloadModel = new DownloadObject();
    }

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor();
        this.swipeBackManagePresenter = new SwipeBackManageImplementor(this);
        this.popupManagePresenter = new PopupManageImplementor(this);
        this.editResultPresenter = new EditResultImplementor(editResultModel, this);
        this.browsablePresenter = new BrowsableImplementor(browsableModel, this);
        this.downloadPresenter = new DownloadImplementor(downloadModel);
    }

    @SuppressLint("SetTextI18n")
    private void initView(boolean init) {
        if (init && browsablePresenter.isBrowsable() && editResultPresenter.getEditKey() == null) {
            browsablePresenter.requestBrowsableData();
        } else {
            Collection c = (Collection) editResultPresenter.getEditKey();

            SwipeBackCoordinatorLayout swipeBackView = ButterKnife.findById(
                    this, R.id.activity_collection_swipeBackView);
            swipeBackView.setOnSwipeListener(this);

            appBar.setOnNestedScrollingListener(this);

            ImageView coverImage = ButterKnife.findById(this, R.id.activity_collection_coverImage);
            ImageHelper.loadCollectionCover(this, coverImage, c);

            TextView title = ButterKnife.findById(this, R.id.activity_collection_title);
            title.setText(c.title);

            RecyclerView tagList = ButterKnife.findById(this, R.id.activity_collection_tagList);
            if (c.tags == null || c.tags.size() == 0) {
                tagList.setVisibility(View.GONE);
            } else {
                tagList.setLayoutManager(
                        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                tagList.setAdapter(new MiniTagAdapter(c.tags));
            }

            TextView description = ButterKnife.findById(this, R.id.activity_collection_description);
            if (TextUtils.isEmpty(c.description)) {
                description.setVisibility(View.GONE);
            } else {
                description.setText(c.description);
            }

            if (isTheLowestLevel()) {
                ThemeManager.setNavigationIcon(
                        toolbar, R.drawable.ic_toolbar_home_light, R.drawable.ic_toolbar_home_dark);
            } else {
                ThemeManager.setNavigationIcon(
                        toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
            }
            ThemeManager.inflateMenu(
                    toolbar,
                    R.menu.activity_collection_toolbar_light,
                    R.menu.activity_collection_toolbar_dark);
            toolbar.setOnMenuItemClickListener(this);
            toolbar.setNavigationOnClickListener(this);
            if (CollectionMenuPopupWindow.isUsable(this, getCollection())) {
                toolbar.getMenu().getItem(1).setVisible(true);
            } else {
                toolbar.getMenu().getItem(1).setVisible(false);
            }

            ImageHelper.loadAvatar(this, avatarImage, c.user);

            TextView subtitle = ButterKnife.findById(this, R.id.activity_collection_subtitle);
            subtitle.setText(getString(R.string.by) + " " + c.user.name);

            photosView.initMP(this, (Collection) editResultPresenter.getEditKey());

            BaseSavedStateFragment f = SavedStateFragment.getData(this);
            if (f instanceof SavedStateFragment) {
                photosView.setPhotos(((SavedStateFragment) f).getPhotoList());
            } else {
                photosView.initAnimShow();
                photosView.initRefresh();
            }
        }
    }

    // UI.

    public void showPopup() {
        popupManagePresenter.showPopup(this, toolbar, null, 0);
    }

    // download.

    public void downloadCollection() {
        downloadPresenter.setDownloadKey(getCollection());
        if (DatabaseHelper.getInstance(this)
                .readDownloadingEntityCount(
                        String.valueOf(((Collection) downloadPresenter.getDownloadKey()).id)) > 0) {
            NotificationHelper.showSnackbar(getString(R.string.feedback_download_repeat));
        } else if (FileUtils.isCollectionExists(
                this,
                String.valueOf(((Collection) downloadPresenter.getDownloadKey()).id))) {
            DownloadRepeatDialog dialog = new DownloadRepeatDialog();
            dialog.setDownloadKey(downloadPresenter.getDownloadKey());
            dialog.setOnCheckOrDownloadListener(this);
            dialog.show(getFragmentManager(), null);
        } else {
            requestPermissionAndDownload();
        }
    }

    private void requestPermissionAndDownload() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            downloadPresenter.download(this);
        } else {
            requestReadWritePermission();
        }
    }

    // data.

    public Collection getCollection() {
        return (Collection) editResultPresenter.getEditKey();
    }

    // permission.

    @Override
    protected void requestReadWritePermissionSucceed(int requestCode) {
        downloadPresenter.download(this);
    }

    // interface.

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                if (isTheLowestLevel()) {
                    browsablePresenter.visitPreviousPage();
                }
                toolbarPresenter.touchNavigatorIcon(this);
                break;
        }
    }

    @OnClick(R.id.activity_collection_touchBar) void checkAuthor() {
        IntentHelper.startUserActivity(
                this,
                avatarImage,
                appBar,
                ((Collection) editResultPresenter.getEditKey()).user,
                UserActivity.PAGE_PHOTO);
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return toolbarPresenter.touchMenuItem(this, item.getItemId());
    }

    // on download photo listener. (photo adapter)

    @Override
    public void onDownload(Photo photo) {
        downloadPresenter.setDownloadKey(photo);
        requestPermissionAndDownload();
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
        return swipeBackManagePresenter.checkCanSwipeBack(dir);
    }

    @Override
    public void onSwipeProcess(float percent) {
        shadow.setAlpha(SwipeBackCoordinatorLayout.getBackgroundAlpha(percent));
    }

    @Override
    public void onSwipeFinish(int dir) {
        swipeBackManagePresenter.swipeBackFinish(this, dir);
    }

    // on collection changed listener.

    @Override
    public void onEditCollection(Collection c) {
        AuthManager.getInstance().getCollectionsManager().updateCollection(c);
        editResultPresenter.updateSomething(c);
    }

    @Override
    public void onDeleteCollection(Collection c) {
        AuthManager.getInstance().getCollectionsManager().deleteCollection(c);
        editResultPresenter.deleteSomething(c);
    }

    // on check or download listener. (Collection)

    @Override
    public void onCheck(Object obj) {
        IntentHelper.startCheckCollectionActivity(this, String.valueOf(((Collection) obj).id));
    }

    @Override
    public void onDownload(Object obj) {
        requestPermissionAndDownload();
    }

    // view.

    // swipe back manage view.

    @Override
    public boolean checkCanSwipeBack(int dir) {
        if (dir == SwipeBackCoordinatorLayout.UP_DIR) {
            return photosView.canSwipeBack(dir)
                    && appBar.getY() <= -appBar.getMeasuredHeight();
        } else {
            return photosView.canSwipeBack(dir)
                    && appBar.getY() >= 0;
        }
    }

    // popup manage view.

    @Override
    public void responsePopup(String value, int position) {
        switch (position) {
            case CollectionMenuPopupWindow.ITEM_EDIT:
                UpdateCollectionDialog dialog = new UpdateCollectionDialog();
                dialog.setCollection(getCollection());
                dialog.setOnCollectionChangedListener(this);
                dialog.show(getFragmentManager(), null);
                break;

            case CollectionMenuPopupWindow.ITEM_DOWNLOAD:
                downloadCollection();
                break;

            case CollectionMenuPopupWindow.ITEM_SET_AS_SOURCE:
                WallpaperSource source = DatabaseHelper.getInstance(this)
                        .readWallpaperSource(getCollection().id);
                if (source == null) {
                    source = new WallpaperSource(getCollection());
                    DatabaseHelper.getInstance(this).writeWallpaperSource(source);
                    NotificationHelper.showSnackbar(getString(R.string.feedback_set_as_source_succeed));
                } else {
                    NotificationHelper.showSnackbar(getString(R.string.feedback_set_as_source_failed));
                }
                break;
        }
    }

    // edit result view.

    @Override
    public void drawCreateResult(Object newKey) {
        // do nothing.
    }

    @Override
    public void drawUpdateResult(Object newKey) {
        Collection c = (Collection) newKey;

        TextView title = findViewById(R.id.activity_collection_title);
        title.setText(c.title);

        TextView description = findViewById(R.id.activity_collection_description);
        if (TextUtils.isEmpty(c.description)) {
            description.setVisibility(View.GONE);
        } else {
            description.setText(c.description);
        }
    }

    @Override
    public void drawDeleteResult(Object oldKey) {
        editResultPresenter.setEditKey(null);
        finishSelf(true);
    }

    // browsable view.

    @Override
    public void showRequestDialog() {
        requestDialog = new RequestBrowsableDataDialog();
        requestDialog.show(getFragmentManager(), null);
    }

    @Override
    public void dismissRequestDialog() {
        requestDialog.dismiss();
        requestDialog = null;
    }

    @Override
    public void drawBrowsableView(Object result) {
        getIntent().putExtra(KEY_COLLECTION_ACTIVITY_COLLECTION, (Collection) result);
        initModel();
        initPresenter();
        initView(false);
    }

    @Override
    public void visitPreviousPage() {
        IntentHelper.startMainActivity(this);
    }
}
