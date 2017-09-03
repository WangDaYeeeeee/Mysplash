package com.wangdaye.mysplash.common.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.table.WallpaperSource;
import com.wangdaye.mysplash.common.ui.adapter.WallpaperSourceAdapter;
import com.wangdaye.mysplash.common.ui.dialog.ConfirmExitWithoutSaveDialog;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.MuzeiOptionManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Muzei configuration activity.
 *
 * This activity is used to config Muzei live wallpaper.
 *
 * */

public class MuzeiConfigurationActivity extends MysplashActivity
        implements SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R.id.activity_muzei_configuration_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_muzei_configuration_statusBar)
    StatusBarView statusBar;

    @BindView(R.id.activity_muzei_configuration_scrollView)
    NestedScrollView scrollView;

    @BindView(R.id.activity_muzei_configuration_intervalEditText)
    EditText intervalEditText;

    @BindView(R.id.activity_muzei_configuration_wifiSwitch)
    Switch wifiSwitch;

    private WallpaperSourceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muzei_configuration);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            ButterKnife.bind(this);
            initData();
            initWidget();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<WallpaperSource> sourceList = DatabaseHelper.getInstance(this)
                .readWallpaperSourceList();
        if (sourceList.size() != adapter.itemList.size()) {
            refreshSourceList(sourceList);
        } else {
            for (int i = 0; i < sourceList.size(); i ++) {
                if (sourceList.get(i).collectionId != adapter.itemList.get(i).getCollectionId()) {
                    refreshSourceList(sourceList);
                    return;
                }
            }
        }
    }

    @Override
    protected void setTheme() {
        if (ThemeManager.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Common);
        }
    }

    @Override
    public void handleBackPressed() {
        if (getWindow().getAttributes().softInputMode
                == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(intervalEditText.getWindowToken(), 0);
        } else {
            ConfirmExitWithoutSaveDialog dialog = new ConfirmExitWithoutSaveDialog();
            dialog.show(getFragmentManager(), null);
        }
    }

    @Override
    protected void backToTop() {
        // do nothing.
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
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initData() {
        this.adapter = new WallpaperSourceAdapter(this, new ArrayList<WallpaperSource>());
    }

    private void initWidget() {
        MuzeiOptionManager manager = MuzeiOptionManager.getInstance(this);

        SwipeBackCoordinatorLayout swipeBackView = ButterKnife.findById(
                this, R.id.activity_muzei_configuration_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.activity_muzei_configuration_toolbar);
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_close_light, R.drawable.ic_toolbar_close_dark);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
            }
        });

        TextView intervalTitle = ButterKnife.findById(this, R.id.activity_muzei_configuration_intervalTitle);
        DisplayUtils.setTypeface(this, intervalTitle);

        intervalEditText.setText(String.valueOf(manager.getUpdateInterval()));
        DisplayUtils.setTypeface(this, intervalEditText);

        TextView wifiTitle = ButterKnife.findById(this, R.id.activity_muzei_configuration_wifiTitle);
        DisplayUtils.setTypeface(this, wifiTitle);

        wifiSwitch.setChecked(manager.isUpdateOnlyInWifi());

        TextView collectionTitle = ButterKnife.findById(this, R.id.activity_muzei_configuration_collectionTitle);
        DisplayUtils.setTypeface(this, collectionTitle);

        RecyclerView collectionList = ButterKnife.findById(this, R.id.activity_muzei_configuration_collectionList);
        collectionList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        collectionList.setAdapter(adapter);
    }

    // control.

    private void refreshSourceList(List<WallpaperSource> newList) {
        adapter.itemList = newList;
        adapter.notifyDataSetChanged();
    }

    public void saveConfiguration() {
        submit();
        finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
    }

    // interface.

    // on click listener.

    @OnClick(R.id.activity_muzei_configuration_wifiContainer)
    void switchWifiState() {
        wifiSwitch.setChecked(!wifiSwitch.isChecked());
    }

    @OnClick(R.id.activity_muzei_configuration_doneBtn)
    void submit() {
        String intervalText = intervalEditText.getText().toString();

        if (TextUtils.isEmpty(intervalText)) {
            intervalText = String.valueOf(MuzeiOptionManager.DEFAULT_INTERVAL);
        }

        int interval = Integer.parseInt(intervalText);
        if (interval < 1 || interval > 24) {
            NotificationHelper.showSnackbar(getString(R.string.feedback_interval_bounds));
            return;
        }

        MuzeiOptionManager.update(
                this, null,
                interval, wifiSwitch.isChecked(), adapter.itemList);
        finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return SwipeBackCoordinatorLayout.canSwipeBack(scrollView, dir);
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
}
