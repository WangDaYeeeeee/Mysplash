package com.wangdaye.mysplash.main.selected.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.DaggerViewModelFactory;
import com.wangdaye.mysplash.common.basic.fragment.MysplashFragment;
import com.wangdaye.mysplash.common.basic.model.PagerView;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.utils.presenter.pager.PagerViewManagePresenter;
import com.wangdaye.mysplash.main.selected.SelectedViewModel;

import java.util.Objects;

import javax.inject.Inject;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Selected fragment.
 *
 * This fragment is used to show selected collection from Unsplash.
 *
 * */

public class SelectedFragment extends MysplashFragment
        implements PagerManageView, NestedScrollAppBarLayout.OnNestedScrollingListener {

    @BindView(R.id.fragment_selected_statusBar) StatusBarView statusBar;
    @BindView(R.id.fragment_selected_container) CoordinatorLayout container;

    @BindView(R.id.fragment_selected_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R.id.fragment_selected_toolbar) Toolbar toolbar;

    @BindView(R.id.fragment_selected_collectionView) SelectedView selectedView;
    private SelectedAdapter selectedAdapter;

    private SelectedViewModel selectedViewModel;
    @Inject DaggerViewModelFactory viewModelFactory;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selected, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initModel();
        initView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void initStatusBarStyle() {
        if (getActivity() != null) {
            DisplayUtils.setStatusBarStyle(getActivity(), needSetDarkStatusBar());
        }
    }

    @Override
    public void initNavigationBarStyle() {
        if (getActivity() != null) {
            DisplayUtils.setNavigationBarStyle(
                    getActivity(), 
                    selectedView.getState() == PagerView.State.NORMAL,
                    true);
        }
    }

    @Override
    public boolean needSetDarkStatusBar() {
        return appBar.getY() <= -appBar.getMeasuredHeight();
    }

    @Override
    public boolean needBackToTop() {
        return selectedView.checkNeedBackToTop();
    }

    @Override
    public void backToTop() {
        statusBar.animToInitAlpha();
        if (getActivity() != null) {
            DisplayUtils.setStatusBarStyle(getActivity(), false);
        }
        BackToTopUtils.showTopBar(appBar, selectedView);
        selectedView.scrollToPageTop();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initModel() {
        selectedViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SelectedViewModel.class);
        selectedViewModel.init(ListResource.refreshing(0, Mysplash.DEFAULT_PER_PAGE));
    }

    private void initView() {
        appBar.setOnNestedScrollingListener(this);

        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_menu_light, R.drawable.ic_toolbar_menu_dark);
        toolbar.setTitle(getString(R.string.action_selected));
        toolbar.setOnClickListener(v12 -> backToTop());
        toolbar.setNavigationOnClickListener(v1 -> {
            if (getActivity() != null) {
                DrawerLayout drawer = getActivity().findViewById(R.id.activity_main_drawerLayout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        selectedAdapter = new SelectedAdapter(
                getActivity(),
                Objects.requireNonNull(selectedViewModel.getListResource().getValue()).dataList,
                DisplayUtils.getGirdColumnCount(getActivity()));
        selectedView.setAdapter(selectedAdapter);
        selectedView.setPagerManageView(this);

        selectedViewModel.getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(resource, selectedView, selectedAdapter));
    }

    // interface.

    // pager manage view.

    @Override
    public void onRefresh(int index) {
        selectedViewModel.refresh();
    }

    @Override
    public void onLoad(int index) {
        selectedViewModel.load();
    }

    @Override
    public boolean canLoadMore(int index) {
        return selectedViewModel.getListResource().getValue() != null
                && selectedViewModel.getListResource().getValue().state != ListResource.State.REFRESHING
                && selectedViewModel.getListResource().getValue().state != ListResource.State.LOADING
                && selectedViewModel.getListResource().getValue().state != ListResource.State.ALL_LOADED;
    }

    @Override
    public boolean isLoading(int index) {
        return Objects.requireNonNull(
                selectedViewModel.getListResource().getValue()).state == ListResource.State.LOADING;
    }

    // on nested scrolling listener.

    @Override
    public void onStartNestedScroll() {
        // do nothing.
    }

    @Override
    public void onNestedScrolling() {
        if (getActivity() != null) {
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
    }

    @Override
    public void onStopNestedScroll() {
        // do nothing.
    }
}