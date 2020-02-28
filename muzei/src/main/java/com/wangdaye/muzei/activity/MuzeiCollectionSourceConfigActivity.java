package com.wangdaye.muzei.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.base.MuzeiWallpaperSource;
import com.wangdaye.muzei.R2;
import com.wangdaye.muzei.base.MuzeiOptionManager;
import com.wangdaye.muzei.R;
import com.wangdaye.muzei.ui.ConfirmExitWithoutSaveDialog;
import com.wangdaye.muzei.ui.WallpaperSourceAdapter;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.utils.manager.ThemeManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Muzei collection source config activity.
 *
 * This activity is used to config collection source for Muzei.
 *
 * */

@Route(path = MuzeiCollectionSourceConfigActivity.MUZEI_COLLECTION_SOURCE_CONFIG_ACTIVITY)
public class MuzeiCollectionSourceConfigActivity extends MysplashActivity
        implements SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R2.id.activity_muzei_collection_source_config_swipeBackView) SwipeBackCoordinatorLayout swipeBackView;
    @BindView(R2.id.activity_muzei_collection_source_config_container) CoordinatorLayout container;
    @BindView(R2.id.activity_muzei_collection_source_config_scrollView) NestedScrollView scrollView;

    @OnClick(R2.id.activity_muzei_collection_source_config_doneBtn)
    void submit() {
        MuzeiOptionManager.updateCollectionSource(
                this, MuzeiOptionManager.getInstance(this).getCollectionSourceList());
        finishSelf(true);
    }

    @OnClick(R2.id.activity_muzei_collection_source_config_resetBtn)
    void reset() {
        List<MuzeiWallpaperSource> list = new ArrayList<>();
        list.add(MuzeiWallpaperSource.unsplashSource());
        list.add(MuzeiWallpaperSource.mysplashSource());
        adapter.update(list);
    }

    private WallpaperSourceAdapter adapter;

    public static final String MUZEI_COLLECTION_SOURCE_CONFIG_ACTIVITY
            = "/muzei/MuzeiCollectionSourceConfigActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muzei_collection_source_config);
        ButterKnife.bind(this);
        initData();
        initWidget();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.update(MuzeiOptionManager.getInstance(this).getCollectionSourceList());
    }

    @Override
    public void handleBackPressed() {
        ConfirmExitWithoutSaveDialog dialog = new ConfirmExitWithoutSaveDialog();
        dialog.show(getSupportFragmentManager(), null);
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

    // init.

    private void initData() {
        this.adapter = new WallpaperSourceAdapter(this, new ArrayList<>());
    }

    private void initWidget() {
        swipeBackView.setOnSwipeListener(this);

        Toolbar toolbar = findViewById(R.id.activity_muzei_collection_source_config_toolbar);
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_close_light, R.drawable.ic_toolbar_close_dark
        );
        toolbar.setNavigationOnClickListener(v -> finishSelf(true));

        RecyclerView collectionList = findViewById(R.id.activity_muzei_collection_source_config_collectionList);
        collectionList.setLayoutManager(
                new LinearLayoutManager(
                        this, LinearLayoutManager.HORIZONTAL, false
                )
        );
        collectionList.setAdapter(adapter);
    }

    // control.

    public void saveConfiguration() {
        submit();
        finishSelf(true);
    }

    // interface.

    // on swipe listener.

    @Override
    public boolean canSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        return SwipeBackCoordinatorLayout.canSwipeBack(scrollView, dir);
    }

    @Override
    public void onSwipeProcess(@SwipeBackCoordinatorLayout.DirectionRule int dir, float percent) {
        container.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
    }

    @Override
    public void onSwipeFinish(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        finishSelf(false);
    }
}
