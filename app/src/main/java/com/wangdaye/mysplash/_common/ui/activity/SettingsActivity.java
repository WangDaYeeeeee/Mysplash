package com.wangdaye.mysplash._common.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash._common.ui.fragment.SettingsFragment;
import com.wangdaye.mysplash._common.ui.widget.StatusBarView;

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
            initWidget();
            getFragmentManager()
                    .beginTransaction()
                    .setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.activity_settings_container, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    protected void setTheme() {
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Common);
        }
    }

    /** <br> UI.. */

    private void initWidget() {
        StatusBarView statusBar = (StatusBarView) findViewById(R.id.activity_settings_statusBar);
        if (ThemeUtils.getInstance(this).isNeedSetStatusBarMask()) {
            statusBar.setMask(true);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_settings_toolbar);
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
        }
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
