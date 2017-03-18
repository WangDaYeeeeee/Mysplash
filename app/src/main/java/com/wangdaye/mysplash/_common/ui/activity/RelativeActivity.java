package com.wangdaye.mysplash._common.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.CollectionCardAdapter;
import com.wangdaye.mysplash._common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash._common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;

import java.util.ArrayList;

/**
 * Relative activity.
 * */

public class RelativeActivity extends MysplashActivity
        implements View.OnClickListener, SwipeBackCoordinatorLayout.OnSwipeListener,
        SelectCollectionDialog.OnCollectionsChangedListener, PhotoAdapter.OnDownloadPhotoListener {
    // widget
    private CoordinatorLayout container;
    private StatusBarView statusBar;

    private RelativeLayout topBar;
    private RecyclerView photoRecyclerView;

    private PhotoAdapter adapter;
    private OnScrollListener scrollListener;

    // data
    private Photo downloadTarget;
    public static final String KEY_RELATIVE_ACTIVITY_PHOTO = "relative_activity_photo";

    /** <br> life cycle. */

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
            initWidget();
        }
    }

    @Override
    public void onDestroy() {
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
        int firstVisibleItem = ((LinearLayoutManager) photoRecyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();
        if (firstVisibleItem > 5) {
            photoRecyclerView.scrollToPosition(5);
        }
        photoRecyclerView.smoothScrollBy(0, (int) scrollListener.topBarTranslationY);
    }

    @Override
    protected boolean isFullScreen() {
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
        if (ViewCompat.canScrollVertically(photoRecyclerView, -1)
                && BackToTopUtils.isSetBackToTop(false)) {
            backToTop();
        } else {
            finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
        }
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    /** <br> UI. */

    private void initWidget() {
        Photo photo = getIntent().getParcelableExtra(KEY_RELATIVE_ACTIVITY_PHOTO);

        this.container = (CoordinatorLayout) findViewById(R.id.activity_relative_container);

        this.statusBar = (StatusBarView) findViewById(R.id.activity_relative_statusBar);
        if (DisplayUtils.isNeedSetStatusBarMask()) {
            statusBar.setBackgroundResource(R.color.colorPrimary_light);
            statusBar.setMask(true);
        }

        SwipeBackCoordinatorLayout swipeBackView
                = (SwipeBackCoordinatorLayout) findViewById(R.id.activity_relative_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        this.topBar = (RelativeLayout) findViewById(R.id.activity_relative_topBar);

        ImageButton closeBtn = (ImageButton) findViewById(R.id.activity_relative_closeBtn);
        closeBtn.setOnClickListener(this);
        if (Mysplash.getInstance().isLightTheme()) {
            closeBtn.setImageResource(R.drawable.ic_close_light);
        } else {
            closeBtn.setImageResource(R.drawable.ic_close_dark);
        }

        TextView collectionTitle = (TextView) findViewById(R.id.activity_relative_collectionTitle);
        DisplayUtils.setTypeface(this, collectionTitle);
        collectionTitle.setText(
                (photo.related_collections == null ? 0 : photo.related_collections.results.size())
                        + " " + getString(R.string.relative_collection));

        RecyclerView collectionRecyclerView = (RecyclerView) findViewById(R.id.activity_relative_collectionRecyclerView);
        collectionRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        collectionRecyclerView.setAdapter(
                new CollectionCardAdapter(
                        this,
                        photo.related_collections == null ? new ArrayList<Collection>() : photo.related_collections.results));

        TextView photoTitle = (TextView) findViewById(R.id.activity_relative_photoTitle);
        DisplayUtils.setTypeface(this, photoTitle);
        photoTitle.setText(
                (photo.related_photos == null ? 0 : photo.related_photos.results.size())
                        + " " + getString(R.string.relative_photo));

        this.adapter = new PhotoAdapter(
                this,
                photo.related_photos == null ? new ArrayList<Photo>() : photo.related_photos.results,
                this, this);
        this.scrollListener = new OnScrollListener();

        this.photoRecyclerView = (RecyclerView) findViewById(R.id.activity_relative_photoRecyclerView);
        photoRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        photoRecyclerView.setAdapter(adapter);
        photoRecyclerView.addOnScrollListener(scrollListener);

        adapter.setRecyclerView(photoRecyclerView);
    }

    /** <br> permission. */

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission(int permissionCode, int type) {
        switch (permissionCode) {
            case Mysplash.WRITE_EXTERNAL_STORAGE:
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(
                            new String[] {
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            type);
                } else {
                    DownloadHelper.getInstance(this)
                            .addMission(this, downloadTarget, DownloadHelper.DOWNLOAD_TYPE);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult);
        for (int i = 0; i < permission.length; i ++) {
            switch (permission[i]) {
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    if (grantResult[i] == PackageManager.PERMISSION_GRANTED) {
                        DownloadHelper.getInstance(this)
                                .addMission(this, downloadTarget, DownloadHelper.DOWNLOAD_TYPE);
                    } else {
                        NotificationHelper.showSnackbar(
                                getString(R.string.feedback_need_permission),
                                Snackbar.LENGTH_SHORT);
                    }
                    break;
            }
        }
    }

    /** <br> interface. */

    // on click swipeListener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_relative_closeBtn:
                finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
                break;
        }
    }

    // on scroll swipeListener.

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

    // on swipe swipeListener.

    @Override
    public boolean canSwipeBack(int dir) {
        return SwipeBackCoordinatorLayout.canSwipeBackForThisView(photoRecyclerView, dir);
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

    // on download swipeListener.

    @Override
    public void onDownload(Photo photo) {
        downloadTarget = photo;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            DownloadHelper.getInstance(this)
                    .addMission(this, downloadTarget, DownloadHelper.DOWNLOAD_TYPE);
        } else {
            requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, DownloadHelper.DOWNLOAD_TYPE);
        }
    }

    // on collections changed swipeListener.

    @Override
    public void onAddCollection(Collection c) {
        // do nothing.
    }

    @Override
    public void onUpdateCollection(Collection c, User u, Photo p) {
        adapter.updatePhoto(p, false);
    }
}