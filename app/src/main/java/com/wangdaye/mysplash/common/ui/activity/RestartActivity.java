package com.wangdaye.mysplash.common.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import com.wangdaye.mysplash.common.utils.helper.IntentHelper;

/**
 * Restart activity.
 *
 * This activity is a hub between 2 {@link com.wangdaye.mysplash.main.view.activity.MainActivity}.
 * Because of the "single task" launch mode, we can only start this activity, and destroy the old
 * MainActivity, then start a new one from this activity.
 *
 * */

public class RestartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        IntentHelper.startMainActivity(this);
        overridePendingTransition(android.R.anim.fade_in, 0);
        finish();
    }
}
