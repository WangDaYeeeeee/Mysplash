package com.wangdaye.mysplash.common.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.ModeUtils;

/**
 * Mysplash Activity
 * */

public abstract class MysplashActivity extends AppCompatActivity {
    // model.
    private boolean started = false;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        Mysplash.getInstance().addActivity(this);
        DisplayUtils.setStatusBarTextDark(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Mysplash.getInstance().removeActivity();
    }

    /** <br> model. */

    public void setStarted() {
        started = true;
    }

    public boolean isStarted() {
        return started;
    }

    protected abstract void setTheme();
}
