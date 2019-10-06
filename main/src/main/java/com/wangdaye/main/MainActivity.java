package com.wangdaye.main;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;

import android.view.View;
import android.widget.TextView;

import com.wangdaye.base.i.Downloadable;
import com.wangdaye.common.base.activity.LoadableActivity;
import com.wangdaye.common.base.fragment.MysplashFragment;
import com.wangdaye.base.DownloadTask;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.base.resource.Resource;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.base.vm.ParamsViewModelFactory;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.network.UrlCollection;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.common.utils.BackToTopUtils;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.main.di.component.DaggerApplicationComponent;
import com.wangdaye.main.home.ui.HomeFragment;
import com.wangdaye.main.collection.ui.CollectionFragment;
import com.wangdaye.main.following.ui.FollowingFeedFragment;
import com.wangdaye.main.multiFilter.ui.MultiFilterFragment;
import com.wangdaye.main.selected.ui.SelectedFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nekocode.rxlifecycle.LifecycleEvent;
import cn.nekocode.rxlifecycle.RxLifecycle;
import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Main activity.
 * */

public class MainActivity extends LoadableActivity<Photo> {

    @BindView(R2.id.activity_main_drawerLayout) DrawerLayout drawer;
    @BindView(R2.id.activity_main_navView) NavigationView nav;

    private AppCompatImageView appIcon;
    private CircularImageView navAvatar;
    private TextView navTitle;
    private TextView navSubtitle;
    private AppCompatImageButton navButton;

    private MainActivityModel mainActivityModel;
    @Inject ParamsViewModelFactory viewModelFactory;

    public static final String MAIN_ACTIVITY = "/main/MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DaggerApplicationComponent.create().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initModel();
        initView();
        ComponentFactory.getAboutModule().checkAndStartIntroduce(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainActivityModel.checkToRequestAuthInformation();
    }

    @Override
    public boolean hasTranslucentNavigationBar() {
        return true;
    }

    @Override
    public void handleBackPressed() {
        DrawerLayout drawer = findViewById(R.id.activity_main_drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            MysplashFragment f = getTopFragment();
            if (f != null && f.needBackToTop() && BackToTopUtils.isSetBackToTop(true)) {
                f.backToTop();
            } else if (f instanceof HomeFragment) {
                finishSelf(true);
            } else {
                mainActivityModel.selectDrawerItem(R.id.action_home);
            }
        }
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    public void finishSelf(boolean backPressed) {
        finish();
    }

    @Nullable
    @Override
    protected SwipeBackCoordinatorLayout provideSwipeBackView() {
        return null;
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        MysplashFragment fragment = getTopFragment();
        if (fragment == null) {
            return null;
        } else {
            return fragment.getSnackbarContainer();
        }
    }

    @Override
    public List<Photo> loadMoreData(List<Photo> list, int headIndex, boolean headDirection) {
        MysplashFragment fragment = getTopFragment();
        if (fragment != null) {
            int id = Objects.requireNonNull(mainActivityModel.getDrawerSelectedId().getValue());
            if (id == R.id.action_home) {
                if (fragment instanceof HomeFragment) {
                    return ((HomeFragment) fragment).loadMoreData(list, headIndex, headDirection);
                }
            } else if (id == R.id.action_following) {
                if (fragment instanceof FollowingFeedFragment) {
                    return ((FollowingFeedFragment) fragment).loadMoreData(list, headIndex, headDirection);
                }
            } else if (id == R.id.action_multi_filter) {
                if (fragment instanceof MultiFilterFragment) {
                    return ((MultiFilterFragment) fragment).loadMoreData(list, headIndex, headDirection);
                }
            }
        }
        return new ArrayList<>();
    }

    // init.

    private void initModel() {
        mainActivityModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityModel.class);
        if (!AuthManager.getInstance().isAuthorized() || AuthManager.getInstance().getUser() == null) {
            mainActivityModel.init(R.id.action_home, Resource.error(null));
        } else {
            mainActivityModel.init(R.id.action_home, Resource.success(AuthManager.getInstance().getUser()));
        }
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        nav.inflateMenu(R.menu.activity_main_drawer);
        nav.setNavigationItemSelectedListener(item -> {
            drawer.closeDrawer(GravityCompat.START);
            Observable.create(Emitter::onComplete)
                    .compose(RxLifecycle.bind(this).disposeObservableWhen(LifecycleEvent.DESTROY))
                    .delay(400, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> {
                        int i = item.getItemId();
                        if (i == R.id.action_change_theme) {
                            changeTheme();
                        } else if (i == R.id.action_download_manage) {
                            ComponentFactory.getDownloaderService().startDownloadManageActivity(this);
                        } else if (i == R.id.action_settings) {
                            ComponentFactory.getSettingsService().startSettingsActivity(this);
                        } else if (i == R.id.action_about) {
                            ComponentFactory.getAboutModule().startAboutActivity(this);
                        } else {
                            mainActivityModel.selectDrawerItem(item.getItemId());
                        }
                    }).subscribe();
            return true;
        });

        nav.post(() -> {
            nav.getMenu().getItem(9).setVisible(getWindowInsets().bottom != 0);
            nav.getMenu().getItem(9).setEnabled(false);
        });

        View header = nav.getHeaderView(0);
        header.setOnClickListener(v ->
                ComponentFactory.getMeModule().startMeActivity(
                        this, navAvatar, header, ProfilePager.PAGE_PHOTO)
        );

        this.navAvatar = header.findViewById(R.id.container_nav_header_avatar);
        this.appIcon = header.findViewById(R.id.container_nav_header_appIcon);
        ImageHelper.loadResourceImage(this, appIcon, R.drawable.ic_launcher);

        this.navTitle = header.findViewById(R.id.container_nav_header_title);
        this.navSubtitle = header.findViewById(R.id.container_nav_header_subtitle);
        this.navButton = header.findViewById(R.id.container_nav_header_button);
        navButton.setOnClickListener(v -> {
            if (!AuthManager.getInstance().isAuthorized()) {
                ComponentFactory.getMeModule().startLoginActivity(this);
            } else {
                AuthManager.getInstance().logout();
            }
        });

        mainActivityModel.getDrawerSelectedId().observe(this, id -> {
            nav.setCheckedItem(id);
            changeFragment(id);
        });
        mainActivityModel.getUserResource().observe(this, state -> {
            if (!AuthManager.getInstance().isAuthorized()) {
                appIcon.setVisibility(View.VISIBLE);
                navAvatar.setVisibility(View.GONE);
                navButton.setImageResource(R.drawable.ic_plus_mini);
                navTitle.setText(getString(R.string.app_name));
                navSubtitle.setText(getString(R.string.feedback_login_text));
            } else {
                navAvatar.setVisibility(View.VISIBLE);
                appIcon.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    navAvatar.setTransitionName(AuthManager.getInstance().getAccessToken());
                }
                navButton.setImageResource(R.drawable.ic_close_mini);
                if (AuthManager.getInstance().getUser() == null) {
                    ImageHelper.loadAvatar(this, navAvatar, new User(), null);
                    navTitle.setText("");
                    navSubtitle.setText("...");
                } else {
                    ImageHelper.loadAvatar(
                            this, navAvatar, AuthManager.getInstance().getUser(), null);
                    navTitle.setText(AuthManager.getInstance().getUser().name);
                    navSubtitle.setText(AuthManager.getInstance().getEmail());
                }
            }

            if (AuthManager.getInstance().isAuthorized() && UrlCollection.hasNode()) {
                nav.getMenu().getItem(1).setVisible(true);
            } else {
                nav.getMenu().getItem(1).setVisible(false);
            }
        });
    }

    // control.

    public void downloadPhoto(Photo photo) {
        requestReadWritePermission(photo, new RequestPermissionCallback() {
            @Override
            public void onGranted(Downloadable downloadable) {
                ComponentFactory.getDownloaderService().addTask(
                        MainActivity.this,
                        (Photo) downloadable,
                        DownloadTask.DOWNLOAD_TYPE,
                        ComponentFactory.getSettingsService().getDownloadScale()
                );
            }

            @Override
            public void onDenied(Downloadable downloadable) {
                NotificationHelper.showSnackbar(
                        MainActivity.this, getString(R.string.feedback_need_permission));
            }
        });
    }

    private void changeTheme() {
        DisplayUtils.changeTheme(this);
        AppCompatDelegate.setDefaultNightMode(
                ThemeManager.getInstance(this).isLightTheme()
                        ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES
        );
        recreate();
    }

    public void changeFragment(int code) {
        MysplashFragment newF = null;
        MysplashFragment oldF = null;

        List<MysplashFragment> list = getFragmentList(true);
        for (int i = 0; i < list.size(); i ++) {
            if (!list.get(i).isHidden()) {
                oldF = list.get(i);
            }
            if (getFragmentCode(list.get(i)) == code) {
                newF = list.get(i);
            }
            if (newF != null && oldF != null) {
                break;
            }
        }
        if (oldF == null) {
            if (newF == null) {
                newF = buildFragmentByCode(code);
            }
            replaceFragment(newF);
        } else if (newF == null) {
            newF = buildFragmentByCode(code);
            showAndHideNewFragment(newF, oldF);
        } else {
            showAndHideFragment(newF, oldF);
        }
    }

    public List<MysplashFragment> getFragmentList(boolean includeHidden) {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        List<MysplashFragment> resultList = new ArrayList<>();
        for (int i = 0; i < fragmentList.size(); i ++) {
            if (fragmentList.get(i) instanceof MysplashFragment
                    && (includeHidden || !fragmentList.get(i).isHidden())) {
                resultList.add((MysplashFragment) fragmentList.get(i));
            }
        }
        return resultList;
    }

    @Nullable
    public MysplashFragment getTopFragment() {
        List<MysplashFragment> list = getFragmentList(false);
        if (list.size() > 0) {
            return list.get(list.size() - 1);
        } else {
            return null;
        }
    }

    private void replaceFragment(MysplashFragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.activity_main_fragment, f)
                .commit();
        initFragmentSystemBar(f, true);
    }

    private void showAndHideFragment(MysplashFragment newF, MysplashFragment oldF) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(oldF)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .show(newF)
                .commit();
        initFragmentSystemBar(newF, false);
    }

    private void showAndHideNewFragment(MysplashFragment newF, MysplashFragment oldF) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(oldF)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.activity_main_fragment, newF)
                .show(newF)
                .commit();
        initFragmentSystemBar(newF, true);
    }

    private void initFragmentSystemBar(MysplashFragment f, boolean newInstance) {
        f.initStatusBarStyle(this, newInstance);
        f.initNavigationBarStyle(this, newInstance);
    }

    private MysplashFragment buildFragmentByCode(int code) {
        if (code == R.id.action_following) {
            return new FollowingFeedFragment();
        } else if (code == R.id.action_collection) {
            return new CollectionFragment();
        } else if (code == R.id.action_multi_filter) {
            return new MultiFilterFragment();
        } else if (code == R.id.action_selected) {
            return new SelectedFragment();
        }
        return new HomeFragment();
    }

    private int getFragmentCode(MysplashFragment f) {
        if (f instanceof HomeFragment) {
            return R.id.action_home;
        } else if (f instanceof FollowingFeedFragment) {
            return R.id.action_following;
        } else if (f instanceof CollectionFragment) {
            return R.id.action_collection;
        } else if (f instanceof MultiFilterFragment) {
            return R.id.action_multi_filter;
        } else { // SelectedFragment.
            return R.id.action_selected;
        }
    }
}