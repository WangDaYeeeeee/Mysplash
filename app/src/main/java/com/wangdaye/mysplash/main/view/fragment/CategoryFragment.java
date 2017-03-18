package com.wangdaye.mysplash.main.view.fragment;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.CategoryManageModel;
import com.wangdaye.mysplash._common.i.presenter.CategoryManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.i.view.CategoryManageView;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common._basic.MysplashFragment;
import com.wangdaye.mysplash._common.ui.popup.SearchCategoryPopupWindow;
import com.wangdaye.mysplash._common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.main.model.fragment.CategoryManageObject;
import com.wangdaye.mysplash.main.presenter.fragment.CategoryFragmentPopupManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.CategoryManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.ToolbarImplementor;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.main.view.widget.CategoryPhotosView;
import com.wangdaye.mysplash._common.utils.ValueUtils;

/**
 * Category fragment.
 * */

public class CategoryFragment extends MysplashFragment
        implements CategoryManageView, PopupManageView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener,
        NestedScrollAppBarLayout.OnNestedScrollingListener,
        SearchCategoryPopupWindow.OnSearchCategoryChangedListener {
    // model.
    private CategoryManageModel categoryManageModel;

    // view.
    private StatusBarView statusBar;

    private CoordinatorLayout container;
    private NestedScrollAppBarLayout appBar;
    private Toolbar toolbar;
    private TextView title;
    private ImageButton titleBtn;
    private CategoryPhotosView photosView;

    // presenter.
    private CategoryManagePresenter categoryManagePresenter;
    private ToolbarPresenter toolbarPresenter;
    private PopupManagePresenter popupManagePresenter;

    // data
    private static final String KEY_CATEGORY_FRAGMENT_CATEGORY_ID = "key_category_fragment_category_id";

    /** <br> life cycle. */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        initModel(savedInstanceState);
        initPresenter();
        initView(view, savedInstanceState);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        photosView.cancelRequest();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CATEGORY_FRAGMENT_CATEGORY_ID, categoryManagePresenter.getCategoryId());
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    @Override
    public boolean needSetOnlyWhiteStatusBarText() {
        return appBar.getY() <= -appBar.getMeasuredHeight();
    }

    @Override
    public void backToTop() {
        statusBar.animToInitAlpha();
        setStatusBarStyle(false);
        BackToTopUtils.showTopBar(appBar, photosView);
        photosView.pagerScrollToTop();
    }

    @Override
    public void writeLargeData(MysplashActivity.BaseSavedStateFragment outState) {
        if (photosView != null) {
            ((MainActivity.SavedStateFragment) outState).setCategoryList(photosView.getPhotos());
        }
    }

    @Override
    public void readLargeData(MysplashActivity.BaseSavedStateFragment savedInstanceState) {
        if (photosView != null) {
            photosView.setPhotos(((MainActivity.SavedStateFragment) savedInstanceState).getCategoryList());
        }
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.categoryManagePresenter = new CategoryManageImplementor(categoryManageModel, this);
        this.toolbarPresenter = new ToolbarImplementor();
        this.popupManagePresenter = new CategoryFragmentPopupManageImplementor(this);
    }

    /** <br> view. */

    // init.

    private void initView(View v, Bundle savedInstanceState) {
        this.statusBar = (StatusBarView) v.findViewById(R.id.fragment_category_statusBar);
        statusBar.setInitMaskAlpha();

        this.container = (CoordinatorLayout) v.findViewById(R.id.fragment_category_container);

        this.appBar = (NestedScrollAppBarLayout) v.findViewById(R.id.fragment_category_appBar);
        appBar.setOnNestedScrollingListener(this);

        this.toolbar = (Toolbar) v.findViewById(R.id.fragment_category_toolbar);
        if (Mysplash.getInstance().isLightTheme()) {
            toolbar.inflateMenu(R.menu.fragment_category_toolbar_light);
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_menu_light);
        } else {
            toolbar.inflateMenu(R.menu.fragment_category_toolbar_dark);
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_menu_dark);
        }
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnClickListener(this);

        v.findViewById(R.id.fragment_category_touchBar).setOnClickListener(this);

        this.title = (TextView) v.findViewById(R.id.fragment_category_title);
        title.setText(
                ValueUtils.getToolbarTitleByCategory(
                        getActivity(),
                        categoryManagePresenter.getCategoryId()));

        this.titleBtn = (ImageButton) v.findViewById(R.id.fragment_category_titleBtn);
        if (Mysplash.getInstance().isLightTheme()) {
            titleBtn.setImageResource(R.drawable.ic_menu_down_light);
        } else {
            titleBtn.setImageResource(R.drawable.ic_menu_down_dark);
        }
        titleBtn.setOnClickListener(this);

        this.photosView = (CategoryPhotosView) v.findViewById(R.id.fragment_category_categoryPhotosView);
        photosView.setActivity((MainActivity) getActivity());
        photosView.setCategory(categoryManagePresenter.getCategoryId());
        if (savedInstanceState == null) {
            photosView.initRefresh();
        }
    }

    // interface.

    public void showPopup() {
        popupManagePresenter.showPopup(getActivity(), toolbar, photosView.getOrder(), 0);
    }

    /** <br> model. */

    // init.

    private void initModel(Bundle savedInstanceState) {
        int categoryId = savedInstanceState == null ?
                Mysplash.CATEGORY_BUILDINGS_ID
                :
                savedInstanceState.getInt(KEY_CATEGORY_FRAGMENT_CATEGORY_ID, Mysplash.CATEGORY_BUILDINGS_ID);
        this.categoryManageModel = new CategoryManageObject(categoryId);
    }

    // interface.

    public boolean needPagerBackToTop() {
        return photosView.needPagerBackToTop();
    }

    /** <br> interface. */

    // on click swipeListener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                toolbarPresenter.touchNavigatorIcon((MysplashActivity) getActivity());
                break;

            case R.id.fragment_category_toolbar:
                toolbarPresenter.touchToolbar((MysplashActivity) getActivity());
                break;

            case R.id.fragment_category_touchBar:
            case R.id.fragment_category_titleBtn:
                SearchCategoryPopupWindow popup = new SearchCategoryPopupWindow(
                        getActivity(),
                        titleBtn,
                        categoryManagePresenter.getCategoryId(),
                        false);
                popup.setOnSearchCategoryChangedListener(this);
                break;
        }
    }

    // on menu item click swipeListener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return toolbarPresenter.touchMenuItem((MysplashActivity) getActivity(), item.getItemId());
    }

    // on nested scrolling swipeListener.

    @Override
    public void onStartNestedScroll() {
        // do nothing.
    }

    @Override
    public void onNestedScrolling() {
        if (needSetOnlyWhiteStatusBarText()) {
            if (statusBar.isInitAlpha()) {
                statusBar.animToDarkerAlpha();
                setStatusBarStyle(true);
            }
        } else {
            if (!statusBar.isInitAlpha()) {
                statusBar.animToInitAlpha();
                setStatusBarStyle(false);
            }
        }
    }

    @Override
    public void onStopNestedScroll() {
        // do nothing.
    }

    // on search category changed swipeListener.

    @Override
    public void onSearchCategoryChanged(int categoryId) {
        if (categoryManagePresenter.getCategoryId() != categoryId) {
            categoryManagePresenter.setCategoryId(categoryId);
        }
    }

    // view.

    // category manage view.

    @Override
    public void setCategory(int categoryId) {
        title.setText(
                ValueUtils.getToolbarTitleByCategory(
                        getActivity(),
                        categoryManagePresenter.getCategoryId()));
        photosView.setCategory(categoryId);
        photosView.initRefresh();
    }

    // popup manage view.

    @Override
    public void responsePopup(String value, int position) {
        photosView.setOrder(value);
        photosView.initRefresh();
    }
}