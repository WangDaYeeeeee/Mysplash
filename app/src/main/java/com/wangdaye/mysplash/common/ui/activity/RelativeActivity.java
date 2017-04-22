package com.wangdaye.mysplash.common.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.ReadWriteActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.ui.adapter.CollectionAdapter;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Relative activity.
 *
 * This activity is used to show related photos and collections about a photo.
 *
 * */

public class RelativeActivity extends ReadWriteActivity
        implements SwipeBackCoordinatorLayout.OnSwipeListener,
        SelectCollectionDialog.OnCollectionsChangedListener, PhotoAdapter.OnDownloadPhotoListener {

    @BindView(R.id.activity_relative_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_relative_statusBar)
    StatusBarView statusBar;

    @BindView(R.id.activity_relative_topBar)
    RelativeLayout topBar;

    @BindView(R.id.activity_relative_photoRecyclerView)
    RecyclerView photoRecyclerView;

    private PhotoAdapter adapter;
    private OnScrollListener scrollListener;

    private Photo downloadTarget;

    public static final String KEY_RELATIVE_ACTIVITY_PHOTO = "relative_activity_photo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relative);
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
    public void onDestroy() {
        super.onDestroy();
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
        if (ViewCompat.canScrollVertically(photoRecyclerView, -1)
                && BackToTopUtils.isSetBackToTop(false)) {
            // need scroll to top.
            backToTop();
        } else {
            finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
        }
    }

    @Override
    protected void backToTop() {
        int firstVisibleItem = ((LinearLayoutManager) photoRecyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();
        if (firstVisibleItem > 5) {
            photoRecyclerView.scrollToPosition(5);
        }
        photoRecyclerView.smoothScrollBy(0, (int) scrollListener.topBarTranslationY);
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

    private void initWidget() {
        Photo photo = getIntent().getParcelableExtra(KEY_RELATIVE_ACTIVITY_PHOTO);

        SwipeBackCoordinatorLayout swipeBackView = ButterKnife.findById(
                this, R.id.activity_relative_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        ImageButton closeBtn = ButterKnife.findById(this, R.id.activity_relative_closeBtn);
        ThemeManager.setImageResource(closeBtn, R.drawable.ic_close_light, R.drawable.ic_close_dark);

        TextView collectionTitle = ButterKnife.findById(this, R.id.activity_relative_collectionTitle);
        DisplayUtils.setTypeface(this, collectionTitle);
        collectionTitle.setText(
                (photo.related_collections == null ? 0 : photo.related_collections.results.size())
                        + " " + getString(R.string.relative_collection));

        RecyclerView collectionRecyclerView = ButterKnife.findById(
                this, R.id.activity_relative_collectionRecyclerView);
        collectionRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        collectionRecyclerView.setAdapter(
                new CollectionAdapter(
                        this,
                        photo.related_collections == null ?
                                new ArrayList<Collection>() : photo.related_collections.results,
                        true));

        TextView photoTitle = ButterKnife.findById(this, R.id.activity_relative_photoTitle);
        DisplayUtils.setTypeface(this, photoTitle);
        photoTitle.setText(
                (photo.related_photos == null ? 0 : photo.related_photos.results.size())
                        + " " + getString(R.string.relative_photo));

        this.adapter = new PhotoAdapter(
                this,
                photo.related_photos == null ? new ArrayList<Photo>() : photo.related_photos.results,
                this, this);
        this.scrollListener = new OnScrollListener();

        photoRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        photoRecyclerView.setAdapter(adapter);
        photoRecyclerView.addOnScrollListener(scrollListener);

        adapter.setRecyclerView(photoRecyclerView);
    }

    // permission.

    @Override
    protected void requestReadWritePermissionSucceed(int requestCode) {
        DownloadHelper.getInstance(this)
                .addMission(this, downloadTarget, DownloadHelper.DOWNLOAD_TYPE);
    }

    // interface.

    // on click listener.

    @OnClick(R.id.activity_relative_closeBtn) void close() {
        finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
    }

    // on scroll listener.

    private class OnScrollListener extends RecyclerView.OnScrollListener {
        // data
        float topBarTranslationY;
        float maxTranslationY;

        OnScrollListener() {
            this.topBarTranslationY = 0;
            this.maxTranslationY = new DisplayUtils(RelativeActivity.this).dpToPx(360) * (-1);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy){
            topBarTranslationY -= dy;
            topBar.setTranslationY(Math.max(maxTranslationY, topBarTranslationY));
        }
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return SwipeBackCoordinatorLayout.canSwipeBack(photoRecyclerView, dir);
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

    // on download listener.

    @Override
    public void onDownload(Photo photo) {
        downloadTarget = photo;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            DownloadHelper.getInstance(this)
                    .addMission(this, downloadTarget, DownloadHelper.DOWNLOAD_TYPE);
        } else {
            requestReadWritePermission();
        }
    }

    // on collections changed listener.

    @Override
    public void onAddCollection(Collection c) {
        // do nothing.
    }

    @Override
    public void onUpdateCollection(Collection c, User u, Photo p) {
        adapter.updatePhoto(p, false, true);
    }
}