package com.wangdaye.mysplash.common.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.ModeUtils;
import com.wangdaye.mysplash.common.view.fragment.SettingsFragment;
import com.wangdaye.mysplash.common.widget.StatusBarView;

/**
 * Settings activity.
 * */

public class SettingsActivity extends MysplashActivity
        implements View.OnClickListener {

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initView();
            getFragmentManager()
                    .beginTransaction()
                    .setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.activity_settings_container, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    protected void setTheme() {
        if (ModeUtils.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Common);
        }
    }

    /** <br> view. */

    private void initView() {
        StatusBarView statusBar = (StatusBarView) findViewById(R.id.activity_settings_statusBar);
        if (ModeUtils.getInstance(this).isNeedSetStatusBarMask()) {
            statusBar.setMask(true);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_settings_toolbar);
        toolbar.setNavigationOnClickListener(this);
    }

    /** <br> interface. */

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                finish();
                break;
        }
    }
}
