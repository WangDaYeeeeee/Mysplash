package com.wangdaye.settings.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.textfield.TextInputEditText;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.common.utils.FullscreenInputWorkaround;
import com.wangdaye.common.utils.manager.CustomApiManager;
import com.wangdaye.settings.R;
import com.wangdaye.settings.R2;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.nekocode.rxlifecycle.LifecycleEvent;
import cn.nekocode.rxlifecycle.RxLifecycle;
import io.reactivex.Emitter;
import io.reactivex.Observable;

/**
 * Custom api dialog.
 *
 * This activity can help user to save personal API key.
 *
 * */

@Route(path = CustomApiActivity.CUSTOM_API_ACTIVITY)
public class CustomApiActivity extends MysplashActivity
        implements SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R2.id.activity_custom_api_swipeBackView) SwipeBackCoordinatorLayout swipeBackView;
    @BindView(R2.id.activity_custom_api_container) CoordinatorLayout container;

    @BindView(R2.id.activity_custom_api_key) TextInputEditText key;
    @BindView(R2.id.activity_custom_api_secret) TextInputEditText secret;

    @OnClick({
            R2.id.activity_custom_api_closeBtn,
            R2.id.activity_custom_api_cancelBtn
    }) void cancel() {
        finishSelf(true);
    }

    @OnClick(R2.id.activity_custom_api_enterBtn) void enter() {
        String keyText = "";
        String secretText = "";
        if (key.getText() != null) {
            keyText = key.getText().toString();
        }
        if (secret.getText() != null) {
            secretText = secret.getText().toString();
        }

        boolean changed = CustomApiManager.getInstance(this).setCustomApi(this, keyText, secretText);

        Intent intent = new Intent();
        setResult(changed ? RESULT_OK : RESULT_CANCELED, intent);
        finishSelf(true);
    }

    private FullscreenInputWorkaround workaround;

    private boolean backPressed = false; // mark the first click action.

    public static final String CUSTOM_API_ACTIVITY = "/settings/CustomApiActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_api);
        ButterKnife.bind(this);
        initWidget();
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
            NotificationHelper.showSnackbar(
                    this, getString(R.string.feedback_click_again_to_exit));

            Observable.create(Emitter::onComplete)
                    .compose(RxLifecycle.bind(this).disposeObservableWhen(LifecycleEvent.DESTROY))
                    .delay(2, TimeUnit.SECONDS)
                    .doOnComplete(() -> backPressed = false)
                    .subscribe();
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
            // overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
        } else {
            overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
        }
    }

    @Nullable
    @Override
    protected SwipeBackCoordinatorLayout provideSwipeBackView() {
        return swipeBackView;
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    private void initWidget() {
        this.workaround = FullscreenInputWorkaround.assistActivity(
                this, container, null);

        swipeBackView.setOnSwipeListener(this);

        if (!TextUtils.isEmpty(CustomApiManager.getInstance(this).getCustomApiKey())) {
            key.setText(CustomApiManager.getInstance(this).getCustomApiKey());
        }

        if (!TextUtils.isEmpty(CustomApiManager.getInstance(this).getCustomApiSecret())) {
            secret.setText(CustomApiManager.getInstance(this).getCustomApiSecret());
        }
    }

    // interface.

    // on swipe listener.

    @Override
    public boolean canSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        return true;
    }

    @Override
    public void onSwipeProcess(float percent) {
        container.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
    }

    @Override
    public void onSwipeFinish(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        finishSelf(false);
    }
}
