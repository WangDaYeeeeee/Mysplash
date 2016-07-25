package com.wangdaye.mysplash.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.ui.fragment.SettingsFragment;
import com.wangdaye.mysplash.ui.widget.StatusBarView;
import com.wangdaye.mysplash.utils.DisplayUtils;

/**
 * Settings activity.
 * */

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    // data
    private boolean started = false;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtils.setStatusBarTransparent(this);
        DisplayUtils.setStatusBarTextDark(this);
        DisplayUtils.setWindowTop(this,
                getString(R.string.action_settings),
                ContextCompat.getColor(this, R.color.colorPrimary));
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!started) {
            started = true;
            initWidget();
            getFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.activity_settings_container, new SettingsFragment())
                    .commit();
        }
    }

    /** <br> UI. */

    private void initWidget() {
        StatusBarView statusBar = (StatusBarView) findViewById(R.id.activity_settings_statusBar);
        if (Build.VERSION.SDK_INT <Build.VERSION_CODES.M) {
            statusBar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            statusBar.setMask(true);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_settings_toolbar);
        toolbar.setNavigationOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                finish();
                break;
        }
    }
}
