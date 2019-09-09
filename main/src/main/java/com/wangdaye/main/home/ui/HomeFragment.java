package com.wangdaye.main.home.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wangdaye.base.pager.ListPager;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.base.resource.Resource;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.base.vm.ParamsViewModelFactory;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.utils.AnimUtils;
import com.wangdaye.common.utils.BackToTopUtils;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.main.R;
import com.wangdaye.main.R2;
import com.wangdaye.main.di.component.DaggerApplicationComponent;
import com.wangdaye.main.home.vm.FeaturedHomePhotosViewModel;
import com.wangdaye.main.home.vm.NewHomePhotosViewModel;
import com.wangdaye.common.base.fragment.LoadableFragment;
import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.i.PagerView;
import com.wangdaye.common.ui.adapter.photo.PhotoAdapter;
import com.wangdaye.common.utils.ValueUtils;
import com.wangdaye.common.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.common.presenter.pager.PagerLoadablePresenter;
import com.wangdaye.base.i.PagerManageView;
import com.wangdaye.common.base.vm.PagerManageViewModel;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.ui.widget.AutoHideInkPageIndicator;
import com.wangdaye.common.ui.widget.NestedScrollAppBarLayout;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.ui.adapter.PagerAdapter;
import com.wangdaye.common.ui.widget.windowInsets.StatusBarView;
import com.wangdaye.common.presenter.pager.PagerViewManagePresenter;
import com.wangdaye.main.MainActivity;
import com.wangdaye.main.base.PhotoItemEventHelper;
import com.wangdaye.main.home.vm.AbstractHomePhotosViewModel;
import com.wangdaye.main.home.vm.SearchBarViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.nekocode.rxlifecycle.LifecycleEvent;
import cn.nekocode.rxlifecycle.compact.RxLifecycleCompact;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Home fragment.
 *
 * This fragment is used to show the home page of Mysplash.
 *
 * */

public class HomeFragment extends LoadableFragment<Photo>
        implements PagerManageView, ViewPager.OnPageChangeListener,
        NestedScrollAppBarLayout.OnNestedScrollingListener {

    @BindView(R2.id.fragment_home_statusBar) StatusBarView statusBar;
    @BindView(R2.id.fragment_home_container) CoordinatorLayout container;

    @BindView(R2.id.fragment_home_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R2.id.fragment_home_searchBar) CardView searchBar;
    @OnClick(R2.id.fragment_home_searchBar) void doSearch() {
        ComponentFactory.getSearchModule().startSearchActivity(getActivity(), searchBar, null);
    }
    @OnClick(R2.id.fragment_home_menuButton) void showDrawer() {
        DrawerLayout drawer = requireActivity().findViewById(R.id.activity_main_drawerLayout);
        drawer.openDrawer(GravityCompat.START);
    }
    @BindView(R2.id.fragment_home_avatar) CircularImageView avatar;
    @OnClick(R2.id.fragment_home_avatar) void showProfile() {
        ComponentFactory.getMeModule().startMeActivity(
                requireActivity(), avatar, searchBar, ProfilePager.PAGE_PHOTO);
    }

    @BindView(R2.id.fragment_home_searchHint) TextView searchHint;

    @BindView(R2.id.fragment_home_logo) LinearLayout logo;
    @BindView(R2.id.fragment_home_appIcon) ImageView appIcon;

    @BindView(R2.id.fragment_home_viewPager) ViewPager viewPager;
    @BindView(R2.id.fragment_home_indicator) AutoHideInkPageIndicator indicator;

    private PagerView[] pagers = new PagerView[pageCount()];
    private PhotoAdapter[] adapters = new PhotoAdapter[pageCount()];

    private PagerLoadablePresenter loadablePresenter;
    @Inject LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter;

    private SearchBarViewModel searchBarViewModel;
    private PagerManageViewModel pagerManageModel;
    private AbstractHomePhotosViewModel[] pagerModels = new AbstractHomePhotosViewModel[pageCount()];
    @Inject ParamsViewModelFactory viewModelFactory;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        DaggerApplicationComponent.create().inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initModel();
        initView(getView());
    }

    @Override
    public void onResume() {
        super.onResume();
        AnimUtils.animShow(logo, 300, logo.getAlpha(), 1);
        AnimUtils.animHide(searchHint, 300, searchHint.getAlpha(), 0, true);

        Observable.timer(5, TimeUnit.SECONDS)
                .compose(RxLifecycleCompact.bind(this).disposeObservableWhen(LifecycleEvent.PAUSE))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    AnimUtils.animShow(searchHint);
                    AnimUtils.animHide(logo);
                }).subscribe();
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
                        && pagers[getCurrentPagerPosition()] != null
                        && pagers[getCurrentPagerPosition()].getState() == PagerView.State.NORMAL,
                ((MainActivity) activity).hasTranslucentNavigationBar()
        );
    }

    @Override
    public boolean needSetDarkStatusBar() {
        return appBar.getY() <= -appBar.getMeasuredHeight();
    }

    @Override
    public boolean needBackToTop() {
        return pagers[getCurrentPagerPosition()].checkNeedBackToTop();
    }

    @Override
    public void backToTop() {
        statusBar.switchToInitAlpha();
        if (getActivity() != null) {
            DisplayUtils.setStatusBarStyle(getActivity(), false);
        }
        BackToTopUtils.showTopBar(appBar, viewPager);
        pagers[getCurrentPagerPosition()].scrollToPageTop();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    @Override
    public List<Photo> loadMoreData(List<Photo> list, int headIndex, boolean headDirection) {
        return loadablePresenter.loadMore(
                list, headIndex, headDirection,
                pagers[getCurrentPagerPosition()],
                pagers[getCurrentPagerPosition()].getRecyclerView(),
                adapters[getCurrentPagerPosition()],
                this, getCurrentPagerPosition()
        );
    }

    // init.

    private void initModel() {
        searchBarViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SearchBarViewModel.class);
        if (!AuthManager.getInstance().isAuthorized() || AuthManager.getInstance().getUser() == null) {
            searchBarViewModel.init(Resource.error(null));
        } else {
            searchBarViewModel.init(Resource.success(AuthManager.getInstance().getUser()));
        }

        pagerManageModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PagerManageViewModel.class);
        pagerManageModel.init(newPage());

        pagerModels[newPage()] = ViewModelProviders.of(this, viewModelFactory)
                .get(NewHomePhotosViewModel.class);
        pagerModels[newPage()].init(
                ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE),
                ComponentFactory.getSettingsService().getDefaultPhotoOrder(),
                ValueUtils.getRandomPageList(Photo.TOTAL_NEW_PHOTOS_COUNT, ListPager.DEFAULT_PER_PAGE),
                getResources().getStringArray(R.array.photo_order_values)[3]
        );

        pagerModels[featuredPage()] = ViewModelProviders.of(this, viewModelFactory)
                .get(FeaturedHomePhotosViewModel.class);
        pagerModels[featuredPage()].init(
                ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE),
                ComponentFactory.getSettingsService().getDefaultPhotoOrder(),
                ValueUtils.getRandomPageList(Photo.TOTAL_FEATURED_PHOTOS_COUNT, ListPager.DEFAULT_PER_PAGE),
                getResources().getStringArray(R.array.photo_order_values)[3]
        );
    }

    private void initView(View v) {
        appBar.setOnNestedScrollingListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchBar.setTransitionName(getClass().getSimpleName() + "-searchBar");
            avatar.setTransitionName(getClass().getSimpleName() + "-avatar");
        }

        searchBarViewModel.getUserResource().observe(this, state -> {
            if (!AuthManager.getInstance().isAuthorized()) {
                avatar.setVisibility(View.GONE);
            } else {
                avatar.setVisibility(View.VISIBLE);
                if (AuthManager.getInstance().getUser() == null) {
                    ImageHelper.loadAvatar(
                            requireActivity(), avatar, new User(), null);
                } else {
                    ImageHelper.loadAvatar(
                            requireActivity(), avatar, AuthManager.getInstance().getUser(), null);
                }
            }
        });

        ImageHelper.loadResourceImage(requireActivity(), appIcon, R.drawable.ic_launcher);

        initPages(v);
        loadablePresenter = new PagerLoadablePresenter() {
            @Override
            public List<Photo> subList(int fromIndex, int toIndex) {
                return Objects.requireNonNull(
                        pagerModels[getCurrentPagerPosition()].getListResource().getValue()
                ).dataList.subList(fromIndex, toIndex);
            }
        };
    }

    private void initPages(View v) {
        adapters[newPage()] = new PhotoAdapter(
                Objects.requireNonNull(pagerModels[newPage()].getListResource().getValue()).dataList
        ).setItemEventCallback(
                new PhotoItemEventHelper(
                        (MainActivity) getActivity(),
                        Objects.requireNonNull(pagerModels[newPage()].getListResource().getValue()).dataList,
                        likeOrDislikePhotoPresenter
                )
        );

        adapters[featuredPage()] = new PhotoAdapter(
                Objects.requireNonNull(pagerModels[featuredPage()].getListResource().getValue()).dataList
        ).setItemEventCallback(
                new PhotoItemEventHelper(
                        (MainActivity) getActivity(),
                        Objects.requireNonNull(pagerModels[featuredPage()].getListResource().getValue()).dataList,
                        likeOrDislikePhotoPresenter
                )
        );

        List<View> pageList = new ArrayList<>(
                Arrays.asList(
                        new HomePhotosView(
                                (MainActivity) getActivity(),
                                adapters[newPage()],
                                getCurrentPagerPosition() == newPage(),
                                newPage(),
                                this
                        ), new HomePhotosView(
                                (MainActivity) getActivity(),
                                adapters[featuredPage()],
                                getCurrentPagerPosition() == featuredPage(),
                                featuredPage(),
                                this
                        )
                )
        );
        for (int i = newPage(); i < pageCount(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] homeTabs = getResources().getStringArray(R.array.home_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, homeTabs);

        PagerAdapter adapter = new PagerAdapter(pageList, tabList);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getCurrentPagerPosition(), false);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = v.findViewById(R.id.fragment_home_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        indicator.setViewPager(viewPager);
        indicator.setAlpha(0f);

        pagerManageModel.getPagerPosition().observe(this, position -> {
            for (int i = newPage(); i < pageCount(); i ++) {
                pagers[i].setSelected(i == position);
            }
            if (getActivity() != null) {
                DisplayUtils.setNavigationBarStyle(
                        getActivity(),
                        pagers[position].getState() == PagerView.State.NORMAL,
                        ((MainActivity) getActivity()).hasTranslucentNavigationBar()
                );
            }
            ListResource resource = pagerModels[position].getListResource().getValue();
            if (resource != null
                    && resource.dataList.size() == 0
                    && resource.state != ListResource.State.REFRESHING
                    && resource.state != ListResource.State.LOADING) {
                PagerViewManagePresenter.initRefresh(pagerModels[position], adapters[position]);
            }
        });

        pagerModels[newPage()].getPhotosOrder().observe(this, s -> {
            if (!pagerModels[newPage()].getLatestOrder().equals(s)) {
                pagerModels[newPage()].setLatestOrder(s);
                PagerViewManagePresenter.initRefresh(pagerModels[newPage()], adapters[newPage()]);
            }
        });
        pagerModels[featuredPage()].getPhotosOrder().observe(this, s ->{
            if (!pagerModels[featuredPage()].getLatestOrder().equals(s)) {
                pagerModels[featuredPage()].setLatestOrder(s);
                PagerViewManagePresenter.initRefresh(pagerModels[featuredPage()], adapters[featuredPage()]);
            }
        });
        pagerModels[newPage()].getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(
                        resource,
                        pagers[newPage()],
                        adapters[newPage()]
                )
        );
        pagerModels[featuredPage()].getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(
                        resource,
                        pagers[featuredPage()],
                        adapters[featuredPage()]
                )
        );
    }

    private int getCurrentPagerPosition() {
        if (pagerManageModel.getPagerPosition().getValue() == null) {
            return newPage();
        } else {
            return pagerManageModel.getPagerPosition().getValue();
        }
    }

    private static int newPage() {
        return 0;
    }

    private static int featuredPage() {
        return 1;
    }

    private static int pageCount() {
        return 2;
    }

    // interface.

    // pager manage view.

    @Override
    public void onRefresh(int index) {
        pagerModels[index].refresh();
    }

    @Override
    public void onLoad(int index) {
        pagerModels[index].load();
    }

    @Override
    public boolean canLoadMore(int index) {
        ListResource resource = pagerModels[index].getListResource().getValue();
        return resource != null
                && resource.state != ListResource.State.REFRESHING
                && resource.state != ListResource.State.LOADING
                && resource.state != ListResource.State.ALL_LOADED;
    }

    @Override
    public boolean isLoading(int index) {
        return Objects.requireNonNull(
                pagerModels[index].getListResource().getValue()).state == ListResource.State.LOADING;
    }

    // on menu item click listener.
/*
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_search) {

        } else if (i == R.id.action_filter) {
            PhotoOrderPopupWindow window = new PhotoOrderPopupWindow(
                    getActivity(),
                    toolbar,
                    pagerModels[getCurrentPagerPosition()].getPhotosOrder().getValue(),
                    PhotoOrderPopupWindow.NORMAL_TYPE
            );
            window.setOnPhotoOrderChangedListener(orderValue ->
                    pagerModels[getCurrentPagerPosition()].setPhotosOrder(orderValue)
            );
        }
        return true;
    }
*/
    // on page changed listener.

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        pagerManageModel.setPagerPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (appBar.getY() <= -appBar.getMeasuredHeight()) {
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    indicator.setDisplayState(true);
                    break;

                case ViewPager.SCROLL_STATE_IDLE:
                    indicator.setDisplayState(false);
                    break;
            }
        }
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
