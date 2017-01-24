package com.wangdaye.mysplash.main.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
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
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui._basic.MysplashFragment;
import com.wangdaye.mysplash._common.ui.popup.SearchCategoryPopupWindow;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.main.model.fragment.CategoryManageObject;
import com.wangdaye.mysplash.main.presenter.fragment.CategoryFragmentPopupManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.CategoryManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.ToolbarImplementor;
import com.wangdaye.mysplash.main.view.widget.CategoryPhotosView;
import com.wangdaye.mysplash._common.utils.ValueUtils;

/**
 * Category fragment.
 * */

public class CategoryFragment extends MysplashFragment
        implements CategoryManageView, PopupManageView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener,
        SearchCategoryPopupWindow.OnSearchCategoryChangedListener {
    // model.
    private CategoryManageModel categoryManageModel;

    // view.
    private CoordinatorLayout container;
    private AppBarLayout appBar;
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
        initModel();
        initPresenter();
        initView(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        photosView.cancelRequest();
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    @Override
    public MysplashFragment readBundle(@Nullable Bundle savedInstanceState) {
        setBundle(savedInstanceState);
        return this;
    }

    @Override
    public void writeBundle(Bundle outState) {
        outState.putInt(KEY_CATEGORY_FRAGMENT_CATEGORY_ID, categoryManagePresenter.getCategoryId());
        photosView.writeBundle(outState);
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.categoryManagePresenter = new CategoryManageImplementor(categoryManageModel, this);
        this.toolbarPresenter = new ToolbarImplementor();
        this.popupManagePresenter = new CategoryFragmentPopupManageImplementor(this);
    }

    /** <br> view. */

    // init.

    private void initView(View v) {
        StatusBarView statusBar = (StatusBarView) v.findViewById(R.id.fragment_category_statusBar);
        if (DisplayUtils.isNeedSetStatusBarMask()) {
            statusBar.setBackgroundResource(R.color.colorPrimary_light);
            statusBar.setMask(true);
        }

        this.container = (CoordinatorLayout) v.findViewById(R.id.fragment_category_container);

        this.appBar = (AppBarLayout) v.findViewById(R.id.fragment_category_appBar);

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
        photosView.setActivity((MysplashActivity) getActivity());
        photosView.setCategory(categoryManagePresenter.getCategoryId());
        if (getBundle() != null) {
            photosView.readBundle(getBundle());
        } else {
            photosView.initRefresh();
        }
    }

    // interface.

    public void backToTop() {
        BackToTopUtils.showTopBar(appBar, photosView);
        photosView.pagerScrollToTop();
    }

    public void showPopup() {
        popupManagePresenter.showPopup(getActivity(), toolbar, photosView.getOrder(), 0);
    }

    /** <br> model. */

    // init.

    private void initModel() {
        int categoryId = getBundle() == null ?
                Mysplash.CATEGORY_BUILDINGS_ID
                :
                getBundle().getInt(KEY_CATEGORY_FRAGMENT_CATEGORY_ID, Mysplash.CATEGORY_BUILDINGS_ID);
        this.categoryManageModel = new CategoryManageObject(categoryId);
    }

    // interface.

    public boolean needPagerBackToTop() {
        return photosView.needPagerBackToTop();
    }

    /** <br> interface. */

    // on click listener.

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

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return toolbarPresenter.touchMenuItem((MysplashActivity) getActivity(), item.getItemId());
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