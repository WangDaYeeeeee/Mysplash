package com.wangdaye.mysplash.common.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.widget.EditText;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.CustomApiManager;
import com.wangdaye.mysplash.common.utils.widget.SafeHandler;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Custom api dialog.
 *
 * This activity can help user to save personal API key.
 *
 * */

public class CustomApiActivity extends MysplashActivity
        implements SwipeBackCoordinatorLayout.OnSwipeListener, SafeHandler.HandlerContainer {

    @BindView(R.id.activity_custom_api_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_custom_api_statusBar)
    StatusBarView statusBar;

    @BindView(R.id.activity_custom_api_key)
    EditText key;

    @BindView(R.id.activity_custom_api_secret)
    EditText secret;

    private SafeHandler<CustomApiActivity> handler;

    private boolean backPressed = false; // mark the first click action.

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
            ButterKnife.bind(this);
            initWidget();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void handleBackPressed() {
        // double click to exit.
        if (backPressed) {
            finishSelf(true);
        } else {
            backPressed = true;
            NotificationHelper.showSnackbar(getString(R.string.feedback_click_again_to_exit));

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.obtainMessage(1).sendToTarget();
                }
            }, 2000);
        }
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    public void finishSelf(boolean backPressed) {
        finish();
        if (backPressed) {
            overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
        } else {
            overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
        }
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    private void initWidget() {
        this.handler = new SafeHandler<>(this);

        SwipeBackCoordinatorLayout swipeBackView = findViewById(R.id.activity_custom_api_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        if (!TextUtils.isEmpty(CustomApiManager.getInstance(this).getCustomApiKey())) {
            key.setText(CustomApiManager.getInstance(this).getCustomApiKey());
        }

        if (!TextUtils.isEmpty(CustomApiManager.getInstance(this).getCustomApiSecret())) {
            secret.setText(CustomApiManager.getInstance(this).getCustomApiSecret());
        }
    }

    // interface.

    // on click listener.

    @OnClick({
            R.id.activity_custom_api_closeBtn,
            R.id.activity_custom_api_cancelBtn}) void cancel() {
        finishSelf(true);
    }

    @OnClick(R.id.activity_custom_api_enterBtn) void enter() {
        boolean changed = CustomApiManager.getInstance(this)
                .setCustomApi(
                        this,
                        key.getText().toString(),
                        secret.getText().toString());

        Intent intent = new Intent();
        setResult(changed ? RESULT_OK : RESULT_CANCELED, intent);
        finishSelf(true);
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
        finishSelf(false);
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
