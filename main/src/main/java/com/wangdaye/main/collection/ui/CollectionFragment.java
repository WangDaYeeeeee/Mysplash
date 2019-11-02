package com.wangdaye.main.collection.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.base.pager.ListPager;
import com.wangdaye.common.base.vm.ParamsViewModelFactory;
import com.wangdaye.common.ui.adapter.collection.CollectionItemEventHelper;
import com.wangdaye.common.utils.BackToTopUtils;
import com.wangdaye.main.MainActivity;
import com.wangdaye.main.R;
import com.wangdaye.main.R2;
import com.wangdaye.main.collection.vm.FeaturedCollectionsViewModel;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.base.fragment.MysplashFragment;
import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.i.PagerView;
import com.wangdaye.common.ui.adapter.collection.CollectionAdapter;
import com.wangdaye.base.i.PagerManageView;
import com.wangdaye.common.ui.widget.windowInsets.StatusBarView;
import com.wangdaye.common.ui.widget.NestedScrollAppBarLayout;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.common.presenter.pager.PagerViewManagePresenter;
import com.wangdaye.main.di.component.DaggerApplicationComponent;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Collection fragment.
 *
 * This fragment is used to show the collections.
 *
 * */

public class CollectionFragment extends MysplashFragment
        implements PagerManageView, NestedScrollAppBarLayout.OnNestedScrollingListener {

    @BindView(R2.id.fragment_collection_statusBar) StatusBarView statusBar;
    @BindView(R2.id.fragment_collection_container) CoordinatorLayout container;

    @BindView(R2.id.fragment_collection_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R2.id.fragment_collection_toolbar) Toolbar toolbar;

    @BindView(R2.id.fragment_collection_collectionsView) CollectionsView collectionsView;
    private CollectionAdapter collectionAdapter;

    private FeaturedCollectionsViewModel collectionsViewModel;
    @Inject ParamsViewModelFactory viewModelFactory;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        DaggerApplicationComponent.create().inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
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
    public void initStatusBarStyle(Activity activity, boolean newInstance) {
        DisplayUtils.setStatusBarStyle(
                activity, !newInstance && needSetDarkStatusBar());
    }

    @Override
    public void initNavigationBarStyle(Activity activity, boolean newInstance) {
        DisplayUtils.setNavigationBarStyle(
                activity,
                !newInstance
                        && collectionsView != null
                        && collectionsView.getState() == PagerView.State.NORMAL,
                ((MainActivity) activity).hasTranslucentNavigationBar()
        );
    }

    @Override
    public boolean needSetDarkStatusBar() {
        return appBar.getY() <= -appBar.getMeasuredHeight();
    }

    @Override
    public boolean needBackToTop() {
        return collectionsView.checkNeedBackToTop();
    }

    @Override
    public void backToTop() {
        statusBar.switchToInitAlpha();
        if (getActivity() != null) {
            DisplayUtils.setStatusBarStyle(getActivity(), false);
        }
        BackToTopUtils.showTopBar(appBar, collectionsView);
        collectionsView.scrollToPageTop();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initModel() {
        collectionsViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(FeaturedCollectionsViewModel.class);
        collectionsViewModel.init(ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE));
    }

    private void initView() {
        appBar.setOnNestedScrollingListener(this);

        toolbar.setTitle(R.string.action_collection);
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_menu_light, R.drawable.ic_toolbar_menu_dark);
        toolbar.setOnClickListener(v12 -> backToTop());
        toolbar.setNavigationOnClickListener(v1 -> {
            if (getActivity() != null) {
                DrawerLayout drawer = getActivity().findViewById(R.id.activity_main_drawerLayout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        collectionAdapter = new CollectionAdapter(
                Objects.requireNonNull(collectionsViewModel.getListResource().getValue()).dataList
        ).setItemEventCallback(new CollectionItemEventHelper((MysplashActivity) getActivity()));
        collectionsView.setAdapterAndMangeView(collectionAdapter, this);

        collectionsViewModel.getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(
                        resource, collectionsView, collectionAdapter)
        );
    }

    // interface.

    // pager manage view.

    @Override
    public void onRefresh(int index) {
        collectionsViewModel.refresh();
    }

    @Override
    public void onLoad(int index) {
        collectionsViewModel.load();
    }

    @Override
    public boolean canLoadMore(int index) {
        ListResource resource = collectionsViewModel.getListResource().getValue();
        return resource != null
                && resource.state != ListResource.State.REFRESHING
                && resource.state != ListResource.State.LOADING
                && resource.state != ListResource.State.ALL_LOADED;
    }

    @Override
    public boolean isLoading(int index) {
        return Objects.requireNonNull(
                collectionsViewModel.getListResource().getValue()).state == ListResource.State.LOADING;
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
                    statusBar.switchToDarkerAlpha();
                    DisplayUtils.setStatusBarStyle(getActivity(), true);
                }
            } else {
                if (!statusBar.isInitState()) {
                    statusBar.switchToInitAlpha();
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