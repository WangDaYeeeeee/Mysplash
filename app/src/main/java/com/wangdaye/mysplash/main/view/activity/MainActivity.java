package com.wangdaye.mysplash.main.view.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.tools.DownloadTools;
import com.wangdaye.mysplash.common.utils.ModeUtils;
import com.wangdaye.mysplash.common.view.activity.MysplashActivity;
import com.wangdaye.mysplash.main.model.activity.DrawerObject;
import com.wangdaye.mysplash.main.model.activity.i.DrawerModel;
import com.wangdaye.mysplash.main.presenter.activity.DrawerImp;
import com.wangdaye.mysplash.main.presenter.activity.i.DrawerPresenter;
import com.wangdaye.mysplash.main.view.activity.i.DrawerView;
import com.wangdaye.mysplash.main.view.fragment.HomeFragment;
import com.wangdaye.mysplash.common.utils.SafeHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Main activity.
 * */

public class MainActivity extends MysplashActivity
        implements DrawerView,
        NavigationView.OnNavigationItemSelectedListener, SafeHandler.HandlerContainer {
    // model.
    private DrawerModel drawerModel;

    // view
    private SafeHandler<MainActivity> handler;

    // presenter.
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
            changeFragment(new HomeFragment());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadTools.getInstance().cancelAll();
    }

    @Override
    protected void setTheme() {
        if (ModeUtils.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light);
        } else {
            setTheme(R.style.MysplashTheme_dark);
        }
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.drawerPresenter = new DrawerImp(drawerModel, this);
    }

    /** <br> view. */

    private void initView() {
        this.handler = new SafeHandler<>(this);

        NavigationView nav = (NavigationView) findViewById(R.id.activity_main_navView);
        nav.setNavigationItemSelectedListener(this);

        View header = nav.getHeaderView(0);
        ImageView icon = (ImageView) header.findViewById(R.id.container_nav_header_icon);
        Glide.with(this)
                .load(R.drawable.ic_launcher)
                .crossFade(300)
                .into(icon);
    }

    /** <br> model. */

    private void initModel() {
        this.drawerModel = new DrawerObject();
    }

    /** <br> interface. */

    // on navigation item selected listener.

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawerPresenter.selectDrawerItem(item.getItemId());
        return item.getItemId() != R.id.action_null;
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        drawerPresenter.processMessage(this, message.what);
    }

    // view.

    // fragment manage view.

    @Override
    public void changeFragment(Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.activity_main_fragment, f)
                .commit();
    }

    @Override
    public void addFragment(Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_main_fragment, f)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    public void removeFragment() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void sendMessage(final int what) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.obtainMessage(what).sendToTarget();
            }
        }, 400);
    }

    @Override
    public void reboot() {
        int enter_anim = android.R.anim.fade_in;
        int exit_anim = android.R.anim.fade_out;
        finish();
        overridePendingTransition(enter_anim, exit_anim);
        startActivity(getIntent());
        overridePendingTransition(enter_anim, exit_anim);
    }
}
