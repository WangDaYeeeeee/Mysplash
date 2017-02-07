package com.wangdaye.mysplash.category.view.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.i.model.CategoryManageModel;
import com.wangdaye.mysplash._common.i.model.DownloadModel;
import com.wangdaye.mysplash._common.i.presenter.CategoryManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.SwipeBackManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.i.view.CategoryManageView;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.i.view.SwipeBackManageView;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.ValueUtils;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash.category.model.activity.CategoryManageObject;
import com.wangdaye.mysplash.category.model.activity.DownloadObject;
import com.wangdaye.mysplash.category.presenter.activity.CategoryFragmentPopupManageImplementor;
import com.wangdaye.mysplash.category.presenter.activity.CategoryManageImplementor;
import com.wangdaye.mysplash.category.presenter.activity.DownloadImplementor;
import com.wangdaye.mysplash.category.presenter.activity.SwipeBackManageImplementor;
import com.wangdaye.mysplash.category.presenter.activity.ToolbarImplementor;
import com.wangdaye.mysplash.category.view.widget.CategoryPhotosView;

/**
 * Category activity.
 * */

public class CategoryActivity extends MysplashActivity
        implements CategoryManageView, PopupManageView, SwipeBackManageView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, PhotoAdapter.OnDownloadPhotoListener,
        SwipeBackCoordinatorLayout.OnSwipeListener {
    // model.
    private CategoryManageModel categoryManageModel;
    private DownloadModel downloadModel;

    // view.
    private CoordinatorLayout container;
    private StatusBarView statusBar;
    private Toolbar toolbar;
    private CategoryPhotosView photosView;

    // presenter.
    private CategoryManagePresenter categoryManagePresenter;
    private ToolbarPresenter toolbarPresenter;
    private PopupManagePresenter popupManagePresenter;
    private DownloadPresenter downloadPresenter;
    private SwipeBackManagePresenter swipeBackManagePresenter;

    //data
    public static final String KEY_CATEGORY_ACTIVITY_ID = "category_activity_id";

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initModel();
        initPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initView();
        }
    }

    @Override
    protected void setTheme() {
        if (Mysplash.getInstance().isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Common);
        }
    }

    @Override
    protected void backToTop() {
        photosView.scrollToTop();
    }

    @Override
    protected boolean needSetStatusBarTextDark() {
        return true;
    }

    @Override
    public void finishActivity(int dir) {
        finish();
        overridePendingTransition(0, R.anim.activity_slide_out_bottom);
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
    public View getSnackbarContainer() {
        return container;
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.categoryManagePresenter = new CategoryManageImplementor(categoryManageModel, this);
        this.toolbarPresenter = new ToolbarImplementor();
        this.popupManagePresenter = new CategoryFragmentPopupManageImplementor(this);
        this.downloadPresenter = new DownloadImplementor(downloadModel);
        this.swipeBackManagePresenter = new SwipeBackManageImplementor(this);
    }

    /** <br> view. */

    // init.

    private void initView() {
        this.statusBar = (StatusBarView) findViewById(R.id.activity_category_statusBar);
        if (DisplayUtils.isNeedSetStatusBarMask()) {
            statusBar.setBackgroundResource(R.color.colorPrimary_light);
            statusBar.setMask(true);
        }

        this.container = (CoordinatorLayout) findViewById(R.id.activity_category_container);

        SwipeBackCoordinatorLayout swipeBackView
                = (SwipeBackCoordinatorLayout) findViewById(R.id.activity_category_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        this.toolbar = (Toolbar) findViewById(R.id.activity_category_toolbar);
        if (Mysplash.getInstance().isLightTheme()) {
            toolbar.inflateMenu(R.menu.activity_category_toolbar_light);
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
        } else {
            toolbar.inflateMenu(R.menu.activity_category_toolbar_dark);
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
        }
        toolbar.setTitle(
                ValueUtils.getToolbarTitleByCategory(
                        this, categoryManagePresenter.getCategoryId()));
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnClickListener(this);

        this.photosView = (CategoryPhotosView) findViewById(R.id.activity_category_categoryPhotosView);
        photosView.setActivity(this);
        photosView.setCategory(categoryManagePresenter.getCategoryId());
        if (getBundle() != null) {
            photosView.readBundle(getBundle());
        } else {
            photosView.initRefresh();
        }
    }

    // interface.

    public void touchToolbar() {
        backToTop();
    }

    /** <br> model. */

    // init.

    private void initModel() {
        int categoryId = getIntent().getIntExtra(
                KEY_CATEGORY_ACTIVITY_ID,
                Mysplash.CATEGORY_BUILDINGS_ID);
        this.categoryManageModel = new CategoryManageObject(categoryId);
        this.downloadModel = new DownloadObject();
    }

    public void showPopup() {
        popupManagePresenter.showPopup(this, toolbar, photosView.getOrder(), 0);
    }

    /** <br> permission. */

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission(int permissionCode, int type) {
        switch (permissionCode) {
            case Mysplash.WRITE_EXTERNAL_STORAGE:
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(
                            new String[] {
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            type);
                } else {
                    downloadPresenter.download();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult);
        for (int i = 0; i < permission.length; i ++) {
            switch (permission[i]) {
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    if (grantResult[i] == PackageManager.PERMISSION_GRANTED) {
                        downloadPresenter.download();
                    } else {
                        NotificationUtils.showSnackbar(
                                getString(R.string.feedback_need_permission),
                                Snackbar.LENGTH_SHORT);
                    }
                    break;
            }
        }
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                toolbarPresenter.touchNavigatorIcon(this);
                break;

            case R.id.activity_category_toolbar:
                toolbarPresenter.touchToolbar(this);
                break;
        }
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            downloadPresenter.download();
        } else {
            requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, DownloadHelper.DOWNLOAD_TYPE);
        }
    }

    // on swipe listener.(swipe back listener)

    @Override
    public boolean canSwipeBack(int dir) {
        return swipeBackManagePresenter.checkCanSwipeBack(dir);
    }

    @Override
    public void onSwipeProcess(float percent) {
        statusBar.setAlpha(1 - percent);
        container.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
    }

    @Override
    public void onSwipeFinish(int dir) {
        swipeBackManagePresenter.swipeBackFinish(this, dir);
    }

    // view.

    // category manage view.

    @Override
    public void setCategory(int categoryId) {
        // do nothing.
    }

    // popup manage view.

    @Override
    public void responsePopup(String value, int position) {
        photosView.setOrder(value);
        photosView.initRefresh();
    }

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
