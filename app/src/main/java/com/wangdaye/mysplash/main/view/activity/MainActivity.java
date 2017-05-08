package com.wangdaye.mysplash.main.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.ReadWriteActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.FollowingResult;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.i.model.DownloadModel;
import com.wangdaye.mysplash.common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash.common._basic.MysplashFragment;
import com.wangdaye.mysplash.common.ui.activity.RestartActivity;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.i.model.DrawerModel;
import com.wangdaye.mysplash.common.i.presenter.DrawerPresenter;
import com.wangdaye.mysplash.common.i.presenter.FragmentManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.MeManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash.common.i.view.DrawerView;
import com.wangdaye.mysplash.common.i.view.MeManageView;
import com.wangdaye.mysplash.common.ui.activity.IntroduceActivity;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.manager.ShortcutsManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.utils.manager.ThreadManager;
import com.wangdaye.mysplash.main.model.activity.DownloadObject;
import com.wangdaye.mysplash.main.model.activity.DrawerObject;
import com.wangdaye.mysplash.main.model.activity.FragmentManageObject;
import com.wangdaye.mysplash.common.i.model.FragmentManageModel;
import com.wangdaye.mysplash.common.i.view.MessageManageView;
import com.wangdaye.mysplash.main.presenter.activity.DownloadImplementor;
import com.wangdaye.mysplash.main.presenter.activity.DrawerImplementor;
import com.wangdaye.mysplash.main.presenter.activity.FragmentManageImplementor;
import com.wangdaye.mysplash.main.presenter.activity.MeManageImplementor;
import com.wangdaye.mysplash.main.presenter.activity.MessageManageImplementor;
import com.wangdaye.mysplash.common.utils.widget.SafeHandler;
import com.wangdaye.mysplash.main.view.fragment.HomeFragment;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main activity.
 * */

public class MainActivity extends ReadWriteActivity
        implements MessageManageView, MeManageView, DrawerView,
        View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
        PhotoAdapter.OnDownloadPhotoListener, AuthManager.OnAuthDataChangedListener,
        SafeHandler.HandlerContainer {

    @BindView(R.id.activity_main_drawerLayout)
    DrawerLayout drawer;

    @BindView(R.id.activity_main_navView)
    NavigationView nav;

    private ImageView appIcon;
    private CircleImageView navAvatar;
    private TextView navTitle;
    private TextView navSubtitle;
    private ImageButton navButton;

    private SafeHandler<MainActivity> handler;

    private FragmentManageModel fragmentManageModel;
    private FragmentManagePresenter fragmentManagePresenter;

    private MessageManagePresenter messageManagePresenter;

    private MeManagePresenter meManagePresenter;

    private DrawerModel drawerModel;
    private DrawerPresenter drawerPresenter;

    private DownloadModel downloadModel;
    private DownloadPresenter downloadPresenter;

    public static final String ACTION_SEARCH = "com.wangdaye.mysplash.Search";

    private final String KEY_MAIN_ACTIVITY_FRAGMENT_ID = "main_activity_fragment_id";
    private final String KEY_MAIN_ACTIVITY_SELECTED_ID = "main_activity_selected_id";

    private final int REFRESH_PROFILE = 1;
    private final int REFRESH_SHORTCUTS = 2;
    private final int DRAW_PROFILE = 3;

    private Runnable initRunnable = new Runnable() {
        @Override
        public void run() {
            AuthManager.getInstance().addOnWriteDataListener(MainActivity.this);
            if (AuthManager.getInstance().isAuthorized()
                    && (TextUtils.isEmpty(AuthManager.getInstance().getUsername())
                    || AuthManager.getInstance().getNumericId() < 0)) {
                handler.obtainMessage(REFRESH_PROFILE).sendToTarget();
            } else {
                handler.obtainMessage(REFRESH_SHORTCUTS).sendToTarget();
            }
            IntroduceActivity.checkAndStartIntroduce(MainActivity.this);
            handler.obtainMessage(DRAW_PROFILE).sendToTarget();
        }
    };

    public static class SavedStateFragment extends BaseSavedStateFragment {
        // data
        private List<Photo> homeNewList;
        private List<Photo> homeFeaturedList;
        private List<Collection> homeCollectionList;

        private List<Photo> searchPhotoList;
        private List<Collection> searchCollectionList;
        private List<User> searchUserList;

        private List<FollowingResult> followingFeedList;

        private List<Photo> multiFilterList;

        private List<Photo> categoryList;

        // data.

        public List<Photo> getHomeNewList() {
            return homeNewList;
        }

        public void setHomeNewList(List<Photo> homeNewList) {
            this.homeNewList = homeNewList;
        }

        public List<Photo> getHomeFeaturedList() {
            return homeFeaturedList;
        }

        public void setHomeFeaturedList(List<Photo> homeFeaturedList) {
            this.homeFeaturedList = homeFeaturedList;
        }

        public List<Collection> getHomeCollectionList() {
            return homeCollectionList;
        }

        public void setHomeCollectionList(List<Collection> homeCollectionList) {
            this.homeCollectionList = homeCollectionList;
        }

        public List<User> getSearchUserList() {
            return searchUserList;
        }

        public void setSearchUserList(List<User> searchUserList) {
            this.searchUserList = searchUserList;
        }

        public List<Photo> getSearchPhotoList() {
            return searchPhotoList;
        }

        public void setSearchPhotoList(List<Photo> searchPhotoList) {
            this.searchPhotoList = searchPhotoList;
        }

        public List<Collection> getSearchCollectionList() {
            return searchCollectionList;
        }

        public void setSearchCollectionList(List<Collection> searchCollectionList) {
            this.searchCollectionList = searchCollectionList;
        }

        public List<FollowingResult> getFollowingFeedList() {
            return followingFeedList;
        }

        public void setFollowingFeedList(List<FollowingResult> followingFeedList) {
            this.followingFeedList = followingFeedList;
        }

        public List<Photo> getMultiFilterList() {
            return multiFilterList;
        }

        public void setMultiFilterList(List<Photo> multiFilterList) {
            this.multiFilterList = multiFilterList;
        }

        public List<Photo> getCategoryList() {
            return categoryList;
        }

        public void setCategoryList(List<Photo> categoryList) {
            this.categoryList = categoryList;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initModel(savedInstanceState);
        initPresenter();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (isSearchIntent(intent)) {
            changeFragment(R.id.action_search);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            ButterKnife.bind(this);
            initView();
            buildFragmentStack();
            ThreadManager.getInstance().execute(initRunnable);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MysplashFragment fragment = getTopFragment();
        if (fragment != null && data != null) {
            fragment.handleActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == Mysplash.ME_ACTIVITY) {
            drawMeAvatar();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AuthManager.getInstance().refreshPersonalNotifications();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AuthManager.getInstance().removeOnWriteDataListener(this);
        AuthManager.getInstance().cancelRequest();
    }

    @Override
    protected void setTheme() {
        if (ThemeManager.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Main);
        } else {
            setTheme(R.style.MysplashTheme_dark_Main);
        }
        if (DisplayUtils.isLandscape(this)) {
            DisplayUtils.cancelTranslucentNavigation(this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save large data.
        SavedStateFragment f = new SavedStateFragment();
        List<MysplashFragment> fragmentList = fragmentManagePresenter.getFragmentList(this, true);
        for (int i = 0; i < fragmentList.size(); i ++) {
            fragmentList.get(i).writeLargeData(f);
        }
        f.saveData(this);

        // save normal data.
        super.onSaveInstanceState(outState);
        outState.putInt(
                KEY_MAIN_ACTIVITY_FRAGMENT_ID,
                fragmentManagePresenter.getId());
        outState.putInt(
                KEY_MAIN_ACTIVITY_SELECTED_ID,
                drawerPresenter.getCheckedItemId());
    }

    @Override
    public void handleBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            MysplashFragment f = getTopFragment();
            if (f != null
                    && f.needBackToTop() && BackToTopUtils.isSetBackToTop(true)) {
                f.backToTop();
            } else if (f instanceof HomeFragment) {
                finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
            } else {
                changeFragment(R.id.action_home);
            }
        }
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    public void finishActivity(int dir) {
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

    // init.

    private void initModel(@Nullable Bundle savedInstanceState) {
        int fragmentId = R.id.action_home;
        int selectedId = R.id.action_home;
        if (savedInstanceState != null) {
            fragmentId = savedInstanceState.getInt(KEY_MAIN_ACTIVITY_FRAGMENT_ID, fragmentId);
            selectedId = savedInstanceState.getInt(KEY_MAIN_ACTIVITY_SELECTED_ID, selectedId);
        } else if (isSearchIntent(getIntent())) {
            fragmentId = R.id.action_search;
            selectedId = R.id.action_search;
        }

        this.fragmentManageModel = new FragmentManageObject(fragmentId, getIntent());
        this.drawerModel = new DrawerObject(selectedId);
        this.downloadModel = new DownloadObject();
    }

    private void initPresenter() {
        this.fragmentManagePresenter = new FragmentManageImplementor(fragmentManageModel);
        this.messageManagePresenter = new MessageManageImplementor(this);
        this.meManagePresenter = new MeManageImplementor(this);
        this.drawerPresenter = new DrawerImplementor(drawerModel, this);
        this.downloadPresenter = new DownloadImplementor(downloadModel);
    }

    private void initView() {
        this.handler = new SafeHandler<>(this);

        if (ThemeManager.getInstance(this).isLightTheme()) {
            nav.inflateMenu(R.menu.activity_main_drawer_light);
        } else {
            nav.inflateMenu(R.menu.activity_main_drawer_dark);
        }
        nav.setCheckedItem(drawerPresenter.getCheckedItemId());
        nav.setNavigationItemSelectedListener(this);

        if (AuthManager.getInstance().isAuthorized()) {
            nav.getMenu().getItem(1).setVisible(true);
        } else {
            nav.getMenu().getItem(1).setVisible(false);
        }

        View header = nav.getHeaderView(0);
        header.setOnClickListener(this);

        this.navAvatar = ButterKnife.findById(header, R.id.container_nav_header_avatar);

        this.appIcon = ButterKnife.findById(header, R.id.container_nav_header_appIcon);
        ImageHelper.loadIcon(this, appIcon, R.drawable.ic_launcher);

        this.navTitle = ButterKnife.findById(header, R.id.container_nav_header_title);
        DisplayUtils.setTypeface(this, navTitle);

        this.navSubtitle = ButterKnife.findById(header, R.id.container_nav_header_subtitle);
        DisplayUtils.setTypeface(this, navSubtitle);

        this.navButton = ButterKnife.findById(header, R.id.container_nav_header_button);
        navButton.setOnClickListener(this);
    }

    private void buildFragmentStack() {
        BaseSavedStateFragment f = SavedStateFragment.getData(this);
        if (f != null && f instanceof SavedStateFragment) {
            List<MysplashFragment> fragmentList = fragmentManagePresenter.getFragmentList(this, true);
            for (int i = 0; i < fragmentList.size(); i ++) {
                fragmentList.get(i).readLargeData(f);
            }
        } else {
            int id = fragmentManagePresenter.getId();
            changeFragment(id);
        }
    }

    // control.

    private void changeTheme() {
        DisplayUtils.changeTheme(this);
        reboot();
    }

    public void reboot() {
        Intent intent = new Intent(this, RestartActivity.class);
        startActivity(intent);
        overridePendingTransition(0, android.R.anim.fade_out);
        finish();
    }

    public void changeFragment(int code) {
        drawerPresenter.setCheckedItemId(code);
        fragmentManagePresenter.changeFragment(this, code);
    }

    @Nullable
    public MysplashFragment getTopFragment() {
        return fragmentManagePresenter.getTopFragment(this);
    }

    private boolean isSearchIntent(Intent intent) {
        return !(intent == null || TextUtils.isEmpty(intent.getAction()))
                && intent.getAction().equals(ACTION_SEARCH);
    }

    // permission.

    @Override
    protected void requestReadWritePermissionSucceed(int requestCode) {
        downloadPresenter.download(this);
    }

    // interface.

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.container_nav_header:
                meManagePresenter.touchMeAvatar(this);
                break;

            case R.id.container_nav_header_button:
                meManagePresenter.touchMeButton(this);
                break;
        }
    }

    // on navigation item select listener.

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerPresenter.touchNavItem(item.getItemId());
        return true;
    }

    // on download photo listener. (photo adapter)

    @Override
    public void onDownload(Photo photo) {
        downloadPresenter.setDownloadKey(photo);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            downloadPresenter.download(this);
        } else {
            requestReadWritePermission();
        }
    }

    // on write data listener. (authorize manager)

    @SuppressLint("SetTextI18n")
    @Override
    public void onWriteAccessToken() {
        nav.getMenu().getItem(1).setVisible(true);
        meManagePresenter.responseWriteAccessToken();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onWriteUserInfo() {
        meManagePresenter.responseWriteUserInfo();
    }

    @Override
    public void onWriteAvatarPath() {
        meManagePresenter.responseWriteAvatarPath();
    }

    @Override
    public void onLogout() {
        nav.getMenu().getItem(1).setVisible(false);
        meManagePresenter.responseLogout();
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case REFRESH_PROFILE:
                AuthManager.getInstance().requestPersonalProfile();
                break;

            case REFRESH_SHORTCUTS:
                if (Build.VERSION.SDK_INT >= 25) {
                    ShortcutsManager.refreshShortcuts(MainActivity.this);
                }
                break;

            case DRAW_PROFILE:
                drawMeAvatar();
                drawMeTitle();
                drawMeSubtitle();
                drawMeButton();
                break;

            default:
                messageManagePresenter.responseMessage(message.what, message.obj);
                break;
        }
    }

    // view.

    // message manage view.

    @Override
    public void sendMessage(final int what, final Object o) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.obtainMessage(what, o).sendToTarget();
            }
        }, 400);
    }

    @Override
    public void responseMessage(int what, Object o) {
        switch (what) {
            case R.id.action_checkable:
                // do nothing.
                break;

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
                changeFragment(what);
                break;
        }
    }

    // me manage view.

    @Override
    public void drawMeAvatar() {
        if (!AuthManager.getInstance().isAuthorized()) {
            appIcon.setVisibility(View.VISIBLE);
            navAvatar.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(AuthManager.getInstance().getAvatarPath())) {
            navAvatar.setVisibility(View.VISIBLE);
            appIcon.setVisibility(View.GONE);
            ImageHelper.loadAvatar(this, navAvatar, new User(), null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                navAvatar.setTransitionName(AuthManager.getInstance().getAccessToken());
            }
        } else {
            navAvatar.setVisibility(View.VISIBLE);
            appIcon.setVisibility(View.GONE);
            ImageHelper.loadAvatar(this, navAvatar, AuthManager.getInstance().getAvatarPath(), null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                navAvatar.setTransitionName(AuthManager.getInstance().getAccessToken());
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void drawMeTitle() {
        if (!AuthManager.getInstance().isAuthorized()) {
            navTitle.setText("LOGIN");
        } else if (TextUtils.isEmpty(AuthManager.getInstance().getFirstName())
                || TextUtils.isEmpty(AuthManager.getInstance().getLastName())) {
            navTitle.setText("");
        } else {
            navTitle.setText(AuthManager.getInstance().getFirstName()
                    + " " + AuthManager.getInstance().getLastName());
        }
    }

    @Override
    public void drawMeSubtitle() {
        if (!AuthManager.getInstance().isAuthorized()) {
            navSubtitle.setText(getString(R.string.feedback_login_text));
        } else if (TextUtils.isEmpty(AuthManager.getInstance().getEmail())) {
            navSubtitle.setText("...");
        } else {
            navSubtitle.setText(AuthManager.getInstance().getEmail());
        }
    }

    @Override
    public void drawMeButton() {
        if (!AuthManager.getInstance().isAuthorized()) {
            ThemeManager.setImageResource(
                    navButton, R.drawable.ic_plus_mini_light, R.drawable.ic_plus_mini_dark);
        } else {
            ThemeManager.setImageResource(
                    navButton, R.drawable.ic_close_mini_light, R.drawable.ic_close_mini_dark);
        }
    }

    // drawer view.

    @Override
    public void touchNavItem(int id) {
        messageManagePresenter.sendMessage(id, null);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void setCheckedItem(int id) {
        if (id != R.id.action_home
                && id != R.id.action_following
                && id != R.id.action_multi_filter
                && id != R.id.action_category) {
            nav.setCheckedItem(R.id.action_checkable);
        } else {
            nav.setCheckedItem(id);
        }
    }
}