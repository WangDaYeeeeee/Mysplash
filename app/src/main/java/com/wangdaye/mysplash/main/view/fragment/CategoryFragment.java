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
import com.wangdaye.mysplash.common._basic.fragment.LoadableFragment;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.i.model.CategoryManageModel;
import com.wangdaye.mysplash.common.i.presenter.CategoryManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash.common.i.view.CategoryManageView;
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.popup.SearchCategoryPopupWindow;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.i.view.PopupManageView;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.main.model.fragment.CategoryManageObject;
import com.wangdaye.mysplash.main.presenter.fragment.CategoryFragmentPopupManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.CategoryManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.ToolbarImplementor;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.main.view.widget.CategoryPhotosView;
import com.wangdaye.mysplash.common.utils.ValueUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Category fragment.
 *
 * This fragment is used to show photos in a category.
 *
 * */

public class CategoryFragment extends LoadableFragment<Photo>
        implements CategoryManageView, PopupManageView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener,
        NestedScrollAppBarLayout.OnNestedScrollingListener,
        SearchCategoryPopupWindow.OnSearchCategoryChangedListener {

    @BindView(R.id.fragment_category_statusBar)
    StatusBarView statusBar;

    @BindView(R.id.fragment_category_container)
    CoordinatorLayout container;

    @BindView(R.id.fragment_category_appBar)
    NestedScrollAppBarLayout appBar;

    @BindView(R.id.fragment_category_toolbar)
    Toolbar toolbar;

    @BindView(R.id.fragment_category_title)
    TextView title;

    @BindView(R.id.fragment_category_titleBtn)
    ImageButton titleBtn;

    @BindView(R.id.fragment_category_categoryPhotosView)
    CategoryPhotosView photosView;

    private CategoryManageModel categoryManageModel;
    private CategoryManagePresenter categoryManagePresenter;

    private ToolbarPresenter toolbarPresenter;

    private PopupManagePresenter popupManagePresenter;

    private static final String KEY_CATEGORY_FRAGMENT_CATEGORY_ID = "key_category_fragment_category_id";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this, view);
        initModel(savedInstanceState);
        initPresenter();
        initView(savedInstanceState);
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
    public void initStatusBarStyle() {
        DisplayUtils.setStatusBarStyle(getActivity(), needSetDarkStatusBar());
    }

    @Override
    public void initNavigationBarStyle() {
        DisplayUtils.setNavigationBarStyle(getActivity(), photosView.isNormalState(), false);
    }

    @Override
    public boolean needSetDarkStatusBar() {
        return appBar.getY() <= -appBar.getMeasuredHeight();
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

    @Override
    public boolean needBackToTop() {
        return photosView.needPagerBackToTop();
    }

    @Override
    public void backToTop() {
        statusBar.animToInitAlpha();
        DisplayUtils.setStatusBarStyle(getActivity(), false);
        BackToTopUtils.showTopBar(appBar, photosView);
        photosView.pagerScrollToTop();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    @Override
    public List<Photo> loadMoreData(List<Photo> list, int headIndex, boolean headDirection, Bundle bundle) {
        int id = categoryManagePresenter.getCategoryId();
        if (bundle.getInt(KEY_CATEGORY_FRAGMENT_CATEGORY_ID, -1) == id) {
            return photosView.loadMore(list, headIndex, headDirection);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Bundle getBundleOfList(Bundle bundle) {
        bundle.putInt(KEY_CATEGORY_FRAGMENT_CATEGORY_ID, categoryManagePresenter.getCategoryId());
        return bundle;
    }

    @Override
    public void updateData(Photo photo) {
        photosView.updatePhoto(photo, true);
    }

    // init.

    private void initModel(Bundle savedInstanceState) {
        int categoryId = savedInstanceState == null ?
                Mysplash.CATEGORY_BUILDINGS_ID
                :
                savedInstanceState.getInt(KEY_CATEGORY_FRAGMENT_CATEGORY_ID, Mysplash.CATEGORY_BUILDINGS_ID);
        this.categoryManageModel = new CategoryManageObject(categoryId);
    }

    private void initPresenter() {
        this.categoryManagePresenter = new CategoryManageImplementor(categoryManageModel, this);
        this.toolbarPresenter = new ToolbarImplementor();
        this.popupManagePresenter = new CategoryFragmentPopupManageImplementor(this);
    }

    private void initView(Bundle savedInstanceState) {
        appBar.setOnNestedScrollingListener(this);

        ThemeManager.inflateMenu(
                toolbar,
                R.menu.fragment_category_toolbar_light,
                R.menu.fragment_category_toolbar_dark);
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_menu_light, R.drawable.ic_toolbar_menu_dark);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);

        title.setText(
                ValueUtils.getToolbarTitleByCategory(
                        getActivity(),
                        categoryManagePresenter.getCategoryId()));

        ThemeManager.setImageResource(
                titleBtn, R.drawable.ic_menu_down_light, R.drawable.ic_menu_down_dark);

        photosView.setActivity((MainActivity) getActivity());
        photosView.setCategory(categoryManagePresenter.getCategoryId());
        if (savedInstanceState == null) {
            photosView.initRefresh();
        }
    }

    // control.

    public void showPopup() {
        popupManagePresenter.showPopup(getActivity(), toolbar, photosView.getOrder(), 0);
    }

    // interface.

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                toolbarPresenter.touchNavigatorIcon((MysplashActivity) getActivity());
                break;
        }
    }

    @OnClick(R.id.fragment_category_toolbar) void clickToolbar() {
        toolbarPresenter.touchToolbar((MysplashActivity) getActivity());
    }

    @OnClick({
            R.id.fragment_category_touchBar,
            R.id.fragment_category_titleBtn}) void showCategoryList() {
        SearchCategoryPopupWindow popup = new SearchCategoryPopupWindow(
                getActivity(),
                titleBtn,
                categoryManagePresenter.getCategoryId(),
                false);
        popup.setOnSearchCategoryChangedListener(this);
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return toolbarPresenter.touchMenuItem((MysplashActivity) getActivity(), item.getItemId());
    }

    // on nested scrolling listener.

    @Override
    public void onStartNestedScroll() {
        // do nothing.
    }

    @Override
    public void onNestedScrolling() {
        if (needSetDarkStatusBar()) {
            if (statusBar.isInitState()) {
                statusBar.animToDarkerAlpha();
                DisplayUtils.setStatusBarStyle(getActivity(), true);
            }
        } else {
            if (!statusBar.isInitState()) {
                statusBar.animToInitAlpha();
                DisplayUtils.setStatusBarStyle(getActivity(), false);
            }
        }
    }

    @Override
    public void onStopNestedScroll() {
        // do nothing.
    }

    // on search category changed listener.

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