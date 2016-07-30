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
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.tools.DownloadTools;
import com.wangdaye.mysplash.common.view.activity.MysplashActivity;
import com.wangdaye.mysplash.main.model.activity.FragmentManageObject;
import com.wangdaye.mysplash.main.model.activity.i.FragmentManageModel;
import com.wangdaye.mysplash.main.presenter.activity.FragmentManageImp;
import com.wangdaye.mysplash.main.presenter.activity.i.FragmentManagePresenter;
import com.wangdaye.mysplash.main.view.activity.i.FragmentManageView;
import com.wangdaye.mysplash.main.view.fragment.HomeFragment;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.SafeHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Main activity.
 * */

public class MainActivity extends MysplashActivity
        implements FragmentManageView,
        SafeHandler.HandlerContainer, NavigationView.OnNavigationItemSelectedListener {
    // model.
    private FragmentManageModel fragmentManageModel;

    // view
    private SafeHandler<MainActivity> handler;

    // presenter.
    private FragmentManagePresenter fragmentManagePresenter;

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

    /** <br> presenter. */

    private void initPresenter() {
        this.fragmentManagePresenter = new FragmentManageImp(fragmentManageModel, this);
    }

    /** <br> view. */

    private void initView() {
        this.handler = new SafeHandler<>(this);

        NavigationView nav = (NavigationView) findViewById(R.id.activity_main_navView);
        nav.setNavigationItemSelectedListener(this);

        View header = nav.getHeaderView(0);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) header.getLayoutParams();
        params.setMargins(0, DisplayUtils.getStatusBarHeight(getResources()), 0, 0);
        header.setLayoutParams(params);
        ImageView icon = (ImageView) header.findViewById(R.id.container_nav_header_icon);
        Glide.with(this)
                .load(R.drawable.ic_launcher)
                .crossFade(300)
                .into(icon);
    }

    /** <br> model. */

    private void initModel() {
        this.fragmentManageModel = new FragmentManageObject();
    }

    /** <br> interface. */

    // on navigation item selected listener.

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        fragmentManagePresenter.selectDrawerItem(item.getItemId());
        return true;
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

    // handler.

    @Override
    public void handleMessage(Message message) {
        fragmentManagePresenter.processMessage(this, message.what);
    }
}
