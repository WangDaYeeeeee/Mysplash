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

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.CategoryManageModel;
import com.wangdaye.mysplash._common.i.presenter.CategoryManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;
import com.wangdaye.mysplash._common.ui.fragment.SaveInstanceFragment;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
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

public class CategoryFragment extends SaveInstanceFragment
        implements PopupManageView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, NotificationUtils.SnackbarContainer {
    // model.
    private CategoryManageModel categoryManageModel;

    // view.
    private CoordinatorLayout container;
    private AppBarLayout appBar;
    private Toolbar toolbar;
    private CategoryPhotosView photosView;

    // presenter.
    private CategoryManagePresenter categoryManagePresenter;
    private ToolbarPresenter toolbarPresenter;
    private PopupManagePresenter popupManagePresenter;

    /** <br> life cycle. */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
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
    public SaveInstanceFragment readBundle(@Nullable Bundle savedInstanceState) {
        setBundle(savedInstanceState);
        return this;
    }

    @Override
    public void writeBundle(Bundle outState) {
        photosView.writeBundle(outState);
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.categoryManagePresenter = new CategoryManageImplementor(categoryManageModel);
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
        toolbar.setTitle(ValueUtils.getToolbarTitleByCategory(getActivity(), categoryManagePresenter.getCategoryId()));
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

    private void initModel(int categoryId) {
        this.categoryManageModel = new CategoryManageObject(categoryId);
    }

    // interface.

    public boolean needPagerBackToTop() {
        return photosView.needPagerBackToTop();
    }

    public SaveInstanceFragment setCategory(int category) {
        initModel(category);
        return this;
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
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return toolbarPresenter.touchMenuItem((MysplashActivity) getActivity(), item.getItemId());
    }

    // snackbar container.

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    // view.

    // popup manage view.

    @Override
    public void responsePopup(String value, int position) {
        photosView.setOrder(value);
        photosView.initRefresh();
    }
}