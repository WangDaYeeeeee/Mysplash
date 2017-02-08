package com.wangdaye.mysplash._common.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.manager.ApiManager;
import com.wangdaye.mysplash._common.utils.widget.SafeHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Custom api dialog.
 * */

public class CustomApiActivity extends MysplashActivity
        implements View.OnClickListener, SwipeBackCoordinatorLayout.OnSwipeListener,
        SafeHandler.HandlerContainer {
    // widget
    private SafeHandler<CustomApiActivity> handler;

    private CoordinatorLayout container;
    private StatusBarView statusBar;
    private EditText key;
    private EditText secret;

    // data
    private boolean backPressed = false;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_api);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initWidget();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void setTheme() {
        if (Mysplash.getInstance().isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Common);
        }
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    protected boolean needSetStatusBarTextDark() {
        return true;
    }

    @Override
    public void finishActivity(int dir) {
        finish();
        switch (dir) {
            case SwipeBackCoordinatorLayout.UP_DIR:
                overridePendingTransition(0, R.anim.activity_slide_out_top);
                break;

            case SwipeBackCoordinatorLayout.DOWN_DIR:
                overridePendingTransition(0, R.anim.activity_slide_out_bottom);
                break;
        }
    }

    @Override
    public void handleBackPressed() {
        if (backPressed) {
            finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
        } else {
            backPressed = true;
            NotificationUtils.showSnackbar(
                    getString(R.string.feedback_click_again_to_exit),
                    Snackbar.LENGTH_SHORT);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.obtainMessage(1).sendToTarget();
                }
            }, 2000);
        }
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    /** <br> UI. */

    private void initWidget() {
        this.handler = new SafeHandler<>(this);

        this.container = (CoordinatorLayout) findViewById(R.id.activity_custom_api_container);

        this.statusBar = (StatusBarView) findViewById(R.id.activity_custom_api_statusBar);
        if (DisplayUtils.isNeedSetStatusBarMask()) {
            statusBar.setBackgroundResource(R.color.colorPrimary_light);
            statusBar.setMask(true);
        }

        SwipeBackCoordinatorLayout swipeBackView
                = (SwipeBackCoordinatorLayout) findViewById(R.id.activity_custom_api_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        ImageButton closeBtn = (ImageButton) findViewById(R.id.activity_custom_api_closeBtn);
        if (Mysplash.getInstance().isLightTheme()) {
            closeBtn.setImageResource(R.drawable.ic_close_light);
        } else {
            closeBtn.setImageResource(R.drawable.ic_close_dark);
        }
        closeBtn.setOnClickListener(this);

        this.key = (EditText) findViewById(R.id.activity_custom_api_key);
        DisplayUtils.setTypeface(this, key);
        if (!TextUtils.isEmpty(Mysplash.getInstance().getCustomApiKey())) {
            key.setText(Mysplash.getInstance().getCustomApiKey());
        }

        this.secret = (EditText) findViewById(R.id.activity_custom_api_secret);
        DisplayUtils.setTypeface(this, secret);
        if (!TextUtils.isEmpty(Mysplash.getInstance().getCustomApiSecret())) {
            secret.setText(Mysplash.getInstance().getCustomApiSecret());
        }

        TextView redirectUri = (TextView) findViewById(R.id.activity_custom_api_redirectUri);
        DisplayUtils.setTypeface(this, redirectUri);

        findViewById(R.id.activity_custom_api_cancelBtn).setOnClickListener(this);
        findViewById(R.id.activity_custom_api_enterBtn).setOnClickListener(this);
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_custom_api_closeBtn:
            case R.id.activity_custom_api_cancelBtn:
                finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
                break;

            case R.id.activity_custom_api_enterBtn:
                boolean changed = ApiManager.getInstance(this)
                        .writeCustomApi(
                                key.getText().toString(),
                                secret.getText().toString());
                ApiManager.getInstance(this).destroy();
                Mysplash.getInstance().setCustomApiKey(key.getText().toString());
                Mysplash.getInstance().setCustomApiSecret(secret.getText().toString());

                Intent intent = new Intent();
                setResult(changed ? RESULT_OK : RESULT_CANCELED, intent);
                finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
                break;
        }
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return true;
    }

    @Override
    public void onSwipeProcess(float percent) {
        statusBar.setAlpha(1 - percent);
        container.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
    }

    @Override
    public void onSwipeFinish(int dir) {
        finishActivity(dir);
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                backPressed = false;
                break;
        }
    }
}
