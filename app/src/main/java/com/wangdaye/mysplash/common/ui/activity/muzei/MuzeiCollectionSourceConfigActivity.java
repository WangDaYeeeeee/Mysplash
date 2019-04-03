package com.wangdaye.mysplash.common.ui.activity.muzei;

import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.db.WallpaperSource;
import com.wangdaye.mysplash.common.ui.adapter.WallpaperSourceAdapter;
import com.wangdaye.mysplash.common.ui.dialog.ConfirmExitWithoutSaveDialog;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.db.DatabaseHelper;
import com.wangdaye.mysplash.common.utils.manager.MuzeiOptionManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

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

public class MuzeiCollectionSourceConfigActivity extends MysplashActivity
        implements SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R.id.activity_muzei_collection_source_config_container) CoordinatorLayout container;
    @BindView(R.id.activity_muzei_collection_source_config_statusBar) StatusBarView statusBar;
    @BindView(R.id.activity_muzei_collection_source_config_scrollView) NestedScrollView scrollView;

    @OnClick(R.id.activity_muzei_collection_source_config_doneBtn)
    void submit() {
        MuzeiOptionManager.updateCollectionSource(this, adapter.itemList);
        finishSelf(true);
    }

    @OnClick(R.id.activity_muzei_collection_source_config_resetBtn)
    void reset() {
        List<WallpaperSource> list = new ArrayList<>();
        list.add(WallpaperSource.unsplashSource());
        list.add(WallpaperSource.mysplashSource());
        refreshSourceList(list);
    }

    private WallpaperSourceAdapter adapter;

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
            overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
        } else {
            overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
        }
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
        SwipeBackCoordinatorLayout swipeBackView = findViewById(
                R.id.activity_muzei_collection_source_config_swipeBackView);
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

    private void refreshSourceList(List<WallpaperSource> newList) {
        adapter.itemList = newList;
        adapter.notifyDataSetChanged();
    }

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
    public void onSwipeProcess(float percent) {
        statusBar.setAlpha(1 - percent);
        container.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
    }

    @Override
    public void onSwipeFinish(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        finishSelf(false);
    }
}
