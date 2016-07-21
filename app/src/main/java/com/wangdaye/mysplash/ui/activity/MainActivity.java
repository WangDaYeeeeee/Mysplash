package com.wangdaye.mysplash.ui.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.ui.fragment.HomeFragment;
import com.wangdaye.mysplash.utils.DisplayUtils;
import com.wangdaye.mysplash.utils.SafeHandler;

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
    private final int SETTINGS_ACTIVITY = 1;
    private final int ABOUT_ACTIVITY = 2;

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

    /** <br> fragment. */

    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.activity_main_fragment, fragment)
                .commit();
    }

    public void addFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_main_fragment, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    public void removeFragment() {
        getSupportFragmentManager().popBackStack();
    }

    /** <br> interface. */

    // on navigation item selected listener.

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.obtainMessage(SETTINGS_ACTIVITY).sendToTarget();
                    }
                }, 400);
                return true;

            case R.id.action_about:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.obtainMessage(ABOUT_ACTIVITY).sendToTarget();
                    }
                }, 400);
                return true;
        }
        return false;
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case SETTINGS_ACTIVITY:
                break;

            case ABOUT_ACTIVITY:
                break;
        }
    }
}
