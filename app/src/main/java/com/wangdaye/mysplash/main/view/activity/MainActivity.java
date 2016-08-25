package com.wangdaye.mysplash.main.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.tools.AuthManager;
import com.wangdaye.mysplash._common.data.tools.DownloadManager;
import com.wangdaye.mysplash._common.i.model.DrawerModel;
import com.wangdaye.mysplash._common.i.presenter.DrawerPresenter;
import com.wangdaye.mysplash._common.i.presenter.FragmentManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.MeManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash._common.i.view.DrawerView;
import com.wangdaye.mysplash._common.i.view.MeManageView;
import com.wangdaye.mysplash._common.ui.activity.AboutActivity;
import com.wangdaye.mysplash._common.ui.activity.SettingsActivity;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;
import com.wangdaye.mysplash.main.model.activity.DrawerObject;
import com.wangdaye.mysplash.main.model.activity.FragmentManageObject;
import com.wangdaye.mysplash._common.i.model.FragmentManageModel;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;
import com.wangdaye.mysplash._common.i.view.FragmentManageView;
import com.wangdaye.mysplash._common.i.view.MessageManageView;
import com.wangdaye.mysplash.main.presenter.activity.DrawerImplementor;
import com.wangdaye.mysplash.main.presenter.activity.FragmentManageImplementor;
import com.wangdaye.mysplash.main.presenter.activity.MeManageImplementor;
import com.wangdaye.mysplash.main.presenter.activity.MessageManageImplementor;
import com.wangdaye.mysplash.main.view.fragment.CategoryFragment;
import com.wangdaye.mysplash.main.view.fragment.HomeFragment;
import com.wangdaye.mysplash._common.utils.SafeHandler;
import com.wangdaye.mysplash.main.view.fragment.SearchFragment;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Main activity.
 * */

public class MainActivity extends MysplashActivity
        implements FragmentManageView, MessageManageView, MeManageView, DrawerView,
        View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
        AuthManager.OnAuthDataChangedListener, SafeHandler.HandlerContainer {
    // model.
    private FragmentManageModel fragmentManageModel;
    private DrawerModel drawerModel;

    // view
    private CircleImageView navAvatar;
    private ImageView appIcon;
    private TextView navTitle;
    private TextView navSubtitle;
    private ImageButton navButton;
    private SafeHandler<MainActivity> handler;

    // presenter.
    private FragmentManagePresenter fragmentManagePresenter;
    private MessageManagePresenter messageManagePresenter;
    private MeManagePresenter meManagePresenter;
    private DrawerPresenter drawerPresenter;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initModel();
            initView();
            initPresenter();
            fragmentManagePresenter.changeFragment(R.id.action_home);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AuthManager.getInstance().removeOnWriteDataListener(this);
        AuthManager.getInstance().cancelRequest();
        DownloadManager.getInstance().cancelAll();
    }

    @Override
    protected void setTheme() {
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light);
        } else {
            setTheme(R.style.MysplashTheme_dark);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (Mysplash.getInstance().isActivityInBackstage()) {
            super.onBackPressed();
        } else {
            int fragmentCounts = fragmentManagePresenter.getFragmentList().size();
            Fragment f = fragmentManagePresenter.getFragmentList().get(fragmentCounts - 1);
            if (f instanceof HomeFragment
                    && ((HomeFragment) f).needPagerBackToTop()) {
                ((HomeFragment) f).pagerBackToTop();
            } else if (f instanceof SearchFragment
                    && ((SearchFragment) f).needPagerBackToTop()) {
                ((SearchFragment) f).pagerBackToTop();
            } else if (f instanceof CategoryFragment
                    && ((CategoryFragment) f).needPagerBackToTop()) {
                ((CategoryFragment) f).pagerBackToTop();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Mysplash.ME_ACTIVITY:
                drawMeAvatar();
                break;
        }
    }

    public void reboot() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        int enter_anim = android.R.anim.fade_in;
        int exit_anim = android.R.anim.fade_out;
        startActivity(intent);
        overridePendingTransition(enter_anim, exit_anim);
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.fragmentManagePresenter = new FragmentManageImplementor(fragmentManageModel, this);
        this.messageManagePresenter = new MessageManageImplementor(this);
        this.meManagePresenter = new MeManageImplementor(this);
        this.drawerPresenter = new DrawerImplementor(drawerModel, this);
    }

    /** <br> view. */

    // init.

    @SuppressLint("SetTextI18n")
    private void initView() {
        this.handler = new SafeHandler<>(this);

        NavigationView nav = (NavigationView) findViewById(R.id.activity_main_navView);
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            nav.inflateMenu(R.menu.activity_main_drawer_light);
        } else {
            nav.inflateMenu(R.menu.activity_main_drawer_dark);
        }
        nav.setNavigationItemSelectedListener(this);

        View header = nav.getHeaderView(0);
        this.navAvatar = (CircleImageView) header.findViewById(R.id.container_nav_header_avatar);
        navAvatar.setOnClickListener(this);

        this.appIcon = (ImageView) header.findViewById(R.id.container_nav_header_appIcon);
        Glide.with(this)
                .load(R.drawable.ic_launcher)
                .into(appIcon);
        appIcon.setOnClickListener(this);

        this.navTitle = (TextView) header.findViewById(R.id.container_nav_header_title);
        TypefaceUtils.setTypeface(this, navTitle);

        this.navSubtitle = (TextView) header.findViewById(R.id.container_nav_header_subtitle);
        TypefaceUtils.setTypeface(this, navSubtitle);

        this.navButton = (ImageButton) header.findViewById(R.id.container_nav_header_button);
        navButton.setOnClickListener(this);

        drawMeAvatar();
        drawMeTitle();
        drawMeSubtitle();
        drawMeButton();

        if (AuthManager.getInstance().isAuthorized()
                && TextUtils.isEmpty(AuthManager.getInstance().getUsername())) {
            AuthManager.getInstance().refreshPersonalProfile();
        }
    }

    // interface.

    public void insertFragment(int code) {
        fragmentManagePresenter.addFragment(code);
    }

    public void removeFragment() {
        fragmentManagePresenter.popFragment();
    }

    /** <br> model. */

    private void initModel() {
        this.fragmentManageModel = new FragmentManageObject();
        this.drawerModel = new DrawerObject();
        AuthManager.getInstance().addOnWriteDataListener(this);
        // AuthManager.getInstance().refreshPersonalProfile();
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.container_nav_header_avatar:
            case R.id.container_nav_header_appIcon:
                meManagePresenter.touchMeAvatar(this);
                break;

            case R.id.container_nav_header_button:
                meManagePresenter.touchMeButton(this);
                break;
        }
    }

    // on navigation item selected listener.

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawerPresenter.touchNavItem(item.getItemId());
        return true;
    }

    // on write data listener. (authorize manager)

    @SuppressLint("SetTextI18n")
    @Override
    public void onWriteAccessToken() {
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
        meManagePresenter.responseLogout();
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        messageManagePresenter.responseMessage(message.what, message.obj);
    }

    // view.

    // fragment manage view.

    @Override
    public void addFragment(Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_main_fragment, f)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void popFragment() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void changeFragment(Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.activity_main_fragment, f)
                .commit();
    }

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
            case R.id.action_change_theme:
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putBoolean(getString(R.string.key_light_theme), !ThemeUtils.getInstance(this).isLightTheme());
                editor.apply();
                ThemeUtils.getInstance(this).refresh(this);
                reboot();
                break;

            case R.id.action_settings:
                Intent s = new Intent(this, SettingsActivity.class);
                startActivity(s);
                break;

            case R.id.action_about:
                Intent a = new Intent(this, AboutActivity.class);
                startActivity(a);
                break;

            default:
                fragmentManagePresenter.changeFragment(what);
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
            Glide.with(Mysplash.getInstance())
                    .load(R.drawable.default_avatar)
                    .override(128, 128)
                    .into(navAvatar);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                navAvatar.setTransitionName(AuthManager.getInstance().getAccessToken());
            }
        } else {
            navAvatar.setVisibility(View.VISIBLE);
            appIcon.setVisibility(View.GONE);
            Glide.clear(navAvatar);
            Glide.with(Mysplash.getInstance())
                    .load(AuthManager.getInstance().getAvatarPath())
                    .override(128, 128)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(navAvatar);
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
            if (ThemeUtils.getInstance(this).isLightTheme()) {
                navButton.setImageResource(R.drawable.ic_plus_mini_light);
            } else {
                navButton.setImageResource(R.drawable.ic_plus_mini_dark);
            }
        } else {
            if (ThemeUtils.getInstance(this).isLightTheme()) {
                navButton.setImageResource(R.drawable.ic_close_mini_light);
            } else {
                navButton.setImageResource(R.drawable.ic_close_mini_dark);
            }
        }
    }

    // drawer view.

    @Override
    public void touchNavItem(int id) {
        messageManagePresenter.sendMessage(id, null);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
    }
}
