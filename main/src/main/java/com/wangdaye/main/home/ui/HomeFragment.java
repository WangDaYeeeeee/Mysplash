package com.wangdaye.main.home.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;

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
import com.wangdaye.main.home.vm.NewHomePhotosViewModel;
import com.wangdaye.common.base.fragment.LoadableFragment;
import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.i.PagerView;
import com.wangdaye.common.ui.adapter.photo.PhotoAdapter;
import com.wangdaye.common.utils.ValueUtils;
import com.wangdaye.common.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.common.presenter.pager.PagerLoadablePresenter;
import com.wangdaye.base.i.PagerManageView;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.ui.widget.NestedScrollAppBarLayout;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.ui.widget.windowInsets.StatusBarView;
import com.wangdaye.common.presenter.pager.PagerViewManagePresenter;
import com.wangdaye.main.MainActivity;
import com.wangdaye.main.base.PhotoItemEventHelper;
import com.wangdaye.main.home.vm.SearchBarViewModel;

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
        implements PagerManageView, NestedScrollAppBarLayout.OnNestedScrollingListener {

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

    @BindView(R2.id.fragment_home_photosView) HomePhotosView photosView;
    private PhotoAdapter photoAdapter;

    private PagerLoadablePresenter loadablePresenter;
    @Inject LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter;

    private SearchBarViewModel searchBarViewModel;
    private NewHomePhotosViewModel photosViewModel;
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
        initView();
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
                        && photosView != null && photosView.getState() == PagerView.State.NORMAL,
                ((MainActivity) activity).hasTranslucentNavigationBar()
        );
    }

    @Override
    public boolean needSetDarkStatusBar() {
        return appBar.getY() <= -appBar.getMeasuredHeight();
    }

    @Override
    public boolean needBackToTop() {
        return photosView.checkNeedBackToTop();
    }

    @Override
    public void backToTop() {
        statusBar.switchToInitAlpha();
        if (getActivity() != null) {
            DisplayUtils.setStatusBarStyle(getActivity(), false);
        }
        BackToTopUtils.showTopBar(appBar, photosView);
        photosView.scrollToPageTop();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    @Override
    public List<Photo> loadMoreData(List<Photo> list, int headIndex, boolean headDirection) {
        return loadablePresenter.loadMore(
                list, headIndex, headDirection,
                photosView,
                photosView.getRecyclerView(),
                photoAdapter,
                this, 0
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
        
        photosViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(NewHomePhotosViewModel.class);
        photosViewModel.init(
                ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE),
                ComponentFactory.getSettingsService().getDefaultPhotoOrder(),
                ValueUtils.getRandomPageList(Photo.TOTAL_NEW_PHOTOS_COUNT, ListPager.DEFAULT_PER_PAGE),
                getResources().getStringArray(R.array.photo_order_values)[3]
        );
    }

    private void initView() {
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

        photoAdapter = new PhotoAdapter(
                Objects.requireNonNull(photosViewModel.getListResource().getValue()).dataList
        ).setItemEventCallback(
                new PhotoItemEventHelper(
                        (MainActivity) getActivity(),
                        Objects.requireNonNull(photosViewModel.getListResource().getValue()).dataList,
                        likeOrDislikePhotoPresenter
                )
        );
        photosView.setAdapterAndMangeView(photoAdapter, this);

        photosViewModel.getPhotosOrder().observe(this, s -> {
            if (!photosViewModel.getLatestOrder().equals(s)) {
                photosViewModel.setLatestOrder(s);
                PagerViewManagePresenter.initRefresh(photosViewModel, photoAdapter);
            }
        });

        photosViewModel.getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(
                        resource,
                        photosView,
                        photoAdapter
                )
        );
        
        loadablePresenter = new PagerLoadablePresenter() {
            @Override
            public List<Photo> subList(int fromIndex, int toIndex) {
                return Objects.requireNonNull(
                        photosViewModel.getListResource().getValue()
                ).dataList.subList(fromIndex, toIndex);
            }
        };
    }

    // interface.

    // pager manage view.

    @Override
    public void onRefresh(int index) {
        photosViewModel.refresh();
    }

    @Override
    public void onLoad(int index) {
        photosViewModel.load();
    }

    @Override
    public boolean canLoadMore(int index) {
        ListResource resource = photosViewModel.getListResource().getValue();
        return resource != null
                && resource.state != ListResource.State.REFRESHING
                && resource.state != ListResource.State.LOADING
                && resource.state != ListResource.State.ALL_LOADED;
    }

    @Override
    public boolean isLoading(int index) {
        return Objects.requireNonNull(
                photosViewModel.getListResource().getValue()).state == ListResource.State.LOADING;
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
