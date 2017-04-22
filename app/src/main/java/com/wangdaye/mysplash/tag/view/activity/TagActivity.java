package com.wangdaye.mysplash.tag.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.ReadWriteActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.i.model.DownloadModel;
import com.wangdaye.mysplash.common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash.common.i.presenter.SwipeBackManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash.common.i.view.SwipeBackManageView;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;
import com.wangdaye.mysplash.tag.model.activity.DownloadObject;
import com.wangdaye.mysplash.tag.presenter.activity.DownloadImplementor;
import com.wangdaye.mysplash.tag.presenter.activity.SwipeBackManageImplementor;
import com.wangdaye.mysplash.tag.presenter.activity.ToolbarImplementor;
import com.wangdaye.mysplash.tag.view.widget.TagPhotosView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Category activity.
 * */

public class TagActivity extends ReadWriteActivity
        implements SwipeBackManageView,
        View.OnClickListener, PhotoAdapter.OnDownloadPhotoListener,
        NestedScrollAppBarLayout.OnNestedScrollingListener,
        SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R.id.activity_tag_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_tag_statusBar)
    StatusBarView statusBar;

    @BindView(R.id.activity_tag_appBar)
    NestedScrollAppBarLayout appBar;

    @BindView(R.id.activity_tag_tagPhotosView)
    TagPhotosView photosView;

    private ToolbarPresenter toolbarPresenter;

    private DownloadModel downloadModel;
    private DownloadPresenter downloadPresenter;

    private SwipeBackManagePresenter swipeBackManagePresenter;

    public static final String KEY_TAG_ACTIVITY_TAG = "tag_activity_tag";

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
        setContentView(R.layout.activity_tag);
        initModel();
        initPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            ButterKnife.bind(this);
            initView();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Mysplash.PHOTO_ACTIVITY) {
            Photo photo = data.getParcelableExtra(PhotoActivity.KEY_PHOTO_ACTIVITY_PHOTO);
            if (photo != null) {
                photosView.updatePhoto(photo);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (photosView != null) {
            photosView.cancelRequest();
        }
    }

    @Override
    protected void setTheme() {
        if (ThemeManager.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Common);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save large data.
        SavedStateFragment f = new SavedStateFragment();
        if (photosView != null) {
            f.setPhotoList(photosView.getPhotos());
        }
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
            finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
        }
    }

    @Override
    protected void backToTop() {
        statusBar.animToInitAlpha();
        DisplayUtils.setStatusBarStyle(this, false);
        BackToTopUtils.showTopBar(appBar, photosView);
        photosView.scrollToTop();
    }

    @Override
    public void finishActivity(int dir) {
        finish();
        overridePendingTransition(0, R.anim.activity_slide_out_bottom);
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initModel() {
        this.downloadModel = new DownloadObject();
    }

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor();
        this.downloadPresenter = new DownloadImplementor(downloadModel);
        this.swipeBackManagePresenter = new SwipeBackManageImplementor(this);
    }

    private void initView() {
        SwipeBackCoordinatorLayout swipeBackView = ButterKnife.findById(
                this, R.id.activity_tag_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        appBar.setOnNestedScrollingListener(this);

        String tag = getIntent().getStringExtra(KEY_TAG_ACTIVITY_TAG).toLowerCase();
        if (TextUtils.isEmpty(tag)) {
            tag = "unsplash";
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_tag_toolbar);
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        toolbar.setTitle(tag.substring(0, 1).toUpperCase() + tag.substring(1));
        toolbar.setNavigationOnClickListener(this);

        photosView.setActivity(this);
        photosView.setTag(tag);

        BaseSavedStateFragment f = SavedStateFragment.getData(this);
        if (f != null && f instanceof SavedStateFragment) {
            photosView.setPhotos(((SavedStateFragment) f).getPhotoList());
        } else {
            photosView.initRefresh();
        }
    }

    // control.

    public void touchToolbar() {
        backToTop();
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
                toolbarPresenter.touchNavigatorIcon(this);
                break;
        }
    }

    @OnClick(R.id.activity_tag_toolbar) void clickToolbar() {
        toolbarPresenter.touchToolbar(this);
    }

    // on download photo listener. (photo adapter)

    @Override
    public void onDownload(Photo photo) {
        downloadPresenter.setDownloadKey(photo);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            downloadPresenter.download(this);
        } else {
            requestReadWritePermission();
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
            if (!statusBar.isInitState()) {
                statusBar.animToInitAlpha();
                DisplayUtils.setStatusBarStyle(this, false);
            }
        } else {
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

    // on swipe listener.(swipe back listener)

    @Override
    public boolean canSwipeBack(int dir) {
        return swipeBackManagePresenter.checkCanSwipeBack(dir);
    }

    @Override
    public void onSwipeProcess(float percent) {
        container.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
    }

    @Override
    public void onSwipeFinish(int dir) {
        swipeBackManagePresenter.swipeBackFinish(this, dir);
    }

    // view.

    // swipe back manage view.

    @Override
    public boolean checkCanSwipeBack(int dir) {
        if (dir == SwipeBackCoordinatorLayout.UP_DIR) {
            return photosView.canSwipeBack(dir);
        } else {
            return photosView.canSwipeBack(dir);
        }
    }
}
