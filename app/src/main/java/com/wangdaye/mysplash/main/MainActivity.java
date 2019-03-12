package com.wangdaye.mysplash.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.DaggerViewModelFactory;
import com.wangdaye.mysplash.common.basic.activity.LoadableActivity;
import com.wangdaye.mysplash.common.basic.model.Resource;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.basic.fragment.MysplashFragment;
import com.wangdaye.mysplash.common.download.imp.DownloaderService;
import com.wangdaye.mysplash.common.ui.activity.IntroduceActivity;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.download.DownloadHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.manager.ShortcutsManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.basic.SafeHandler;
import com.wangdaye.mysplash.common.utils.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.mysplash.main.collection.ui.CollectionFragment;
import com.wangdaye.mysplash.main.following.ui.FollowingAdapter;
import com.wangdaye.mysplash.main.following.ui.FollowingFeedFragment;
import com.wangdaye.mysplash.main.home.ui.HomeFragment;
import com.wangdaye.mysplash.main.multiFilter.ui.MultiFilterFragment;
import com.wangdaye.mysplash.main.selected.ui.SelectedFragment;
import com.wangdaye.mysplash.user.ui.UserActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main activity.
 * */

public class MainActivity extends LoadableActivity<Photo>
        implements PhotoAdapter.ItemEventCallback, FollowingAdapter.ItemEventCallback,
        SafeHandler.HandlerContainer {

    @BindView(R.id.activity_main_drawerLayout) DrawerLayout drawer;
    @BindView(R.id.activity_main_navView) NavigationView nav;

    private AppCompatImageView appIcon;
    private CircleImageView navAvatar;
    private TextView navTitle;
    private TextView navSubtitle;
    private AppCompatImageButton navButton;

    private SafeHandler<MainActivity> handler;

    private MainActivityModel mainActivityModel;
    @Inject DaggerViewModelFactory viewModelFactory;

    @Inject LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter;

    public static final String ACTION_SEARCH = "com.wangdaye.mysplash.Search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initModel();
        initView();
        IntroduceActivity.checkAndStartIntroduce(MainActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutsManager.refreshShortcuts(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (isSearchIntent(intent)) {
            mainActivityModel.selectDrawerItem(R.id.action_search);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainActivityModel.checkToRequestAuthInformation();
    }

    @Override
    protected void setTheme() {
        if (DisplayUtils.isLandscape(this)) {
            DisplayUtils.cancelTranslucentNavigation(this);
        }
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
            if (f != null
                    && f.needBackToTop() && BackToTopUtils.isSetBackToTop(true)) {
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
            switch (id) {
                case R.id.action_home:
                    if (fragment instanceof HomeFragment) {
                        return ((HomeFragment) fragment).loadMoreData(list, headIndex, headDirection);
                    }
                    break;

                case R.id.action_following:
                    if (fragment instanceof FollowingFeedFragment) {
                        return ((FollowingFeedFragment) fragment).loadMoreData(list, headIndex, headDirection);
                    }
                    break;

                case R.id.action_multi_filter:
                    if (fragment instanceof MultiFilterFragment) {
                        return ((MultiFilterFragment) fragment).loadMoreData(list, headIndex, headDirection);
                    }
                    break;
            }
        }
        return new ArrayList<>();
    }

    // init.

    private void initModel() {
        mainActivityModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityModel.class);
        if (!AuthManager.getInstance().isAuthorized()
                || AuthManager.getInstance().getUser() == null) {
            mainActivityModel.init(
                    isSearchIntent(getIntent()) ? R.id.action_search : R.id.action_home,
                    Resource.error(null));
        } else {
            mainActivityModel.init(
                    isSearchIntent(getIntent()) ? R.id.action_search : R.id.action_home,
                    Resource.success(AuthManager.getInstance().getUser()));
        }
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        this.handler = new SafeHandler<>(this);

        nav.inflateMenu(R.menu.activity_main_drawer);
        nav.setNavigationItemSelectedListener(item -> {
            drawer.closeDrawer(GravityCompat.START);
            sendMessage(item.getItemId());
            return true;
        });

        View header = nav.getHeaderView(0);
        header.setOnClickListener(v ->
                IntentHelper.startMeActivity(this, navAvatar, header, UserActivity.PAGE_PHOTO));

        this.navAvatar = header.findViewById(R.id.container_nav_header_avatar);
        this.appIcon = header.findViewById(R.id.container_nav_header_appIcon);
        ImageHelper.loadResourceImage(this, appIcon, R.drawable.ic_launcher);

        this.navTitle = header.findViewById(R.id.container_nav_header_title);
        this.navSubtitle = header.findViewById(R.id.container_nav_header_subtitle);
        this.navButton = header.findViewById(R.id.container_nav_header_button);
        navButton.setOnClickListener(v -> {
            if (!AuthManager.getInstance().isAuthorized()) {
                IntentHelper.startLoginActivity(this);
            } else {
                AuthManager.getInstance().logout();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    ShortcutsManager.refreshShortcuts(Mysplash.getInstance());
                }
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

            if (AuthManager.getInstance().isAuthorized() && Mysplash.hasNode()) {
                nav.getMenu().getItem(1).setVisible(true);
            } else {
                nav.getMenu().getItem(1).setVisible(false);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                ShortcutsManager.refreshShortcuts(Mysplash.getInstance());
            }
        });
    }

    // control.

    private void changeTheme() {
        DisplayUtils.changeTheme(this);
        AppCompatDelegate.setDefaultNightMode(
                ThemeManager.getInstance(this).isLightTheme()
                        ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
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
        DisplayUtils.setStatusBarStyle(this, false);
    }

    private void showAndHideFragment(MysplashFragment newF, MysplashFragment oldF) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(oldF)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .show(newF)
                .commit();
        newF.initStatusBarStyle();
        newF.initNavigationBarStyle();
    }

    private void showAndHideNewFragment(MysplashFragment newF, MysplashFragment oldF) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(oldF)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.activity_main_fragment, newF)
                .show(newF)
                .commit();
        DisplayUtils.setStatusBarStyle(this, false);
        DisplayUtils.setNavigationBarStyle(this, false, true);
    }

    private MysplashFragment buildFragmentByCode(int code) {
        switch (code) {
            case R.id.action_following:
                return new FollowingFeedFragment();

            case R.id.action_collection:
                return new CollectionFragment();

            case R.id.action_multi_filter:
                return new MultiFilterFragment();

            case R.id.action_selected:
                return new SelectedFragment();

            default:
                return new HomeFragment();
        }
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

    private boolean isSearchIntent(Intent intent) {
        return intent != null
                && !TextUtils.isEmpty(intent.getAction())
                && Objects.requireNonNull(intent.getAction()).equals(MainActivity.ACTION_SEARCH);
    }

    private void sendMessage(final int what) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.obtainMessage(what, null).sendToTarget();
            }
        }, 400);
    }

    // interface.

    // item event callback.

    @Override
    public void onLikeOrDislikePhoto(Photo photo, int adapterPosition, boolean setToLike) {
        likeOrDislikePhotoPresenter.likeOrDislikePhoto(photo, setToLike);
    }

    @Override
    public void onDownload(Photo photo) {
        requestReadWritePermission(photo, downloadable ->
                DownloadHelper.getInstance(MainActivity.this)
                        .addMission(MainActivity.this, (Photo) downloadable, DownloaderService.DOWNLOAD_TYPE));
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case R.id.action_change_theme:
                changeTheme();
                break;

            case R.id.action_download_manage:
                IntentHelper.startDownloadManageActivity(this);
                break;

            case R.id.action_settings:
                IntentHelper.startSettingsActivity(this);
                break;

            case R.id.action_about:
                IntentHelper.startAboutActivity(this);
                break;

            default:
                mainActivityModel.selectDrawerItem(message.what);
                break;
        }
    }
}