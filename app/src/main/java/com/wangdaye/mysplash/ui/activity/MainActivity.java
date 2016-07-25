package com.wangdaye.mysplash.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.data.constant.Mysplash;
import com.wangdaye.mysplash.ui.fragment.CategoryFragment;
import com.wangdaye.mysplash.ui.fragment.HomeFragment;
import com.wangdaye.mysplash.utils.DisplayUtils;
import com.wangdaye.mysplash.utils.SafeHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main activity.
 * */

public class MainActivity extends AppCompatActivity
        implements SafeHandler.HandlerContainer, NavigationView.OnNavigationItemSelectedListener {
    // widget
    private SafeHandler<MainActivity> handler;

    // data
    private boolean started = false;
    private List<Fragment> fragmentList;
    private int menuItemNow = R.id.action_home;
    private final int HOME_FRAGMENT = 1;
    private final int SETTINGS_ACTIVITY = -1;
    private final int ABOUT_ACTIVITY = -2;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtils.setStatusBarTransparent(this);
        DisplayUtils.setStatusBarTextDark(this);
        DisplayUtils.setWindowTop(this,
                getString(R.string.app_name),
                ContextCompat.getColor(this, R.color.colorPrimary));
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!started) {
            started = true;
            initData();
            initWidget();
            changeFragment(new HomeFragment());
        }
    }

    /** <br> UI. */

    private void initWidget() {
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

    /** <br> data. */

    private void initData() {
        this.fragmentList = new ArrayList<>();
    }

    /** <br> fragment. */

    public void changeFragment(Fragment fragment) {
        if (fragmentList.size() > 0) {
            clearFragment();
        }
        fragmentList.add(fragment);
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.activity_main_fragment, fragment)
                .commit();
    }

    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_main_fragment, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    public void removeFragment() {
        fragmentList.remove(fragmentList.size() - 1);
        getSupportFragmentManager().popBackStack();
    }

    private void clearFragment() {
        while (fragmentList.size() > 0) {
            removeFragment();
        }
    }

    /** <br> interface. */

    // on navigation item selected listener.

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == menuItemNow) {
            return false;
        }
        if (item.getItemId() != R.id.action_settings && item.getItemId() != R.id.action_about) {
            menuItemNow = item.getItemId();
        }
        switch (item.getItemId()) {
            case R.id.action_home:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.obtainMessage(HOME_FRAGMENT).sendToTarget();
                    }
                }, 400);
                break;

            case R.id.action_category_buildings:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.obtainMessage(Mysplash.CATEGORY_BUILDINGS_ID).sendToTarget();
                    }
                }, 400);
                break;

            case R.id.action_category_food_drink:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.obtainMessage(Mysplash.CATEGORY_FOOD_DRINK_ID).sendToTarget();
                    }
                }, 400);
                break;

            case R.id.action_category_nature:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.obtainMessage(Mysplash.CATEGORY_NATURE_ID).sendToTarget();
                    }
                }, 400);
                break;

            case R.id.action_category_objects:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.obtainMessage(Mysplash.CATEGORY_OBJECTS_ID).sendToTarget();
                    }
                }, 400);
                break;

            case R.id.action_category_people:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.obtainMessage(Mysplash.CATEGORY_PEOPLE_ID).sendToTarget();
                    }
                }, 400);
                break;

            case R.id.action_category_technology:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.obtainMessage(Mysplash.CATEGORY_TECHNOLOGY_ID).sendToTarget();
                    }
                }, 400);
                break;

            case R.id.action_settings:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.obtainMessage(SETTINGS_ACTIVITY).sendToTarget();
                    }
                }, 400);
                break;

            case R.id.action_about:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.obtainMessage(ABOUT_ACTIVITY).sendToTarget();
                    }
                }, 400);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawerLayout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case HOME_FRAGMENT:
                HomeFragment homeFragment = new HomeFragment();
                changeFragment(homeFragment);
                break;

            case SETTINGS_ACTIVITY:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                break;

            case ABOUT_ACTIVITY:
                Intent about = new Intent(this, AboutActivity.class);
                startActivity(about);
                break;

            default:
                CategoryFragment categoryFragment = new CategoryFragment();
                categoryFragment.setCategory(message.what);
                changeFragment(categoryFragment);
                break;
        }
    }
}
