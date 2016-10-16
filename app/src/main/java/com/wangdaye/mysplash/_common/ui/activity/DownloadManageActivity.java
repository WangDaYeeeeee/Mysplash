package com.wangdaye.mysplash._common.ui.activity;

import android.app.DownloadManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.ui.dialog.PathDialog;
import com.wangdaye.mysplash._common.utils.DownloadHelper;
import com.wangdaye.mysplash._common.data.entity.DownloadMissionEntity;
import com.wangdaye.mysplash._common.ui.adapter.DownloadAdapter;
import com.wangdaye.mysplash._common.ui.widget.StatusBarView;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackLayout;
import com.wangdaye.mysplash._common.utils.SafeHandler;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash.main.view.activity.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Download manage activity.
 * */

public class DownloadManageActivity extends MysplashActivity
        implements View.OnClickListener, Toolbar.OnMenuItemClickListener, SwipeBackLayout.OnSwipeListener,
        DownloadHelper.OnDownloadListener, SafeHandler.HandlerContainer {
    // widget
    private SafeHandler<DownloadManageActivity> handler;
    private Timer timer;

    private CoordinatorLayout container;
    private RecyclerView recyclerView;

    // data
    private DownloadAdapter adapter;
    public static final String EXTRA_NOTIFICATION = "notification";

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_mange);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initData();
            initWidget();
            DownloadHelper.getInstance(this).setOnDownloadListener(this);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.obtainMessage(1).sendToTarget();
                }
            }, 200, 200);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.activity_slide_out_bottom);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        handler.removeCallbacksAndMessages(null);
        DownloadHelper.getInstance(this).setOnDownloadListener(null);
    }

    @Override
    protected void setTheme() {
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Common);
        }
    }

    /** <br> UI. */

    private void initWidget() {
        this.handler = new SafeHandler(this);
        this.timer = new Timer();

        SwipeBackLayout swipeBackLayout = (SwipeBackLayout) findViewById(R.id.activity_download_manage_swipeBackLayout);
        swipeBackLayout.setOnSwipeListener(this);

        StatusBarView statusBar = (StatusBarView) findViewById(R.id.activity_download_manage_statusBar);
        if (ThemeUtils.getInstance(this).isNeedSetStatusBarMask()) {
            statusBar.setBackgroundResource(R.color.colorPrimary_light);
            statusBar.setMask(true);
        }

        boolean openByNotification = getIntent().getBooleanExtra(EXTRA_NOTIFICATION, false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_download_manage_toolbar);
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            if (openByNotification) {
                toolbar.setNavigationIcon(R.drawable.ic_toolbar_home_light);
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
            }
            toolbar.inflateMenu(R.menu.activity_download_manage_toolbar_light);
        } else {
            if (openByNotification) {
                toolbar.setNavigationIcon(R.drawable.ic_toolbar_home_dark);
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
            }
            toolbar.inflateMenu(R.menu.activity_download_manage_toolbar_dark);
        }
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnMenuItemClickListener(this);

        this.container = (CoordinatorLayout) findViewById(R.id.activity_download_manage_container);

        this.recyclerView = (RecyclerView) findViewById(R.id.activity_download_manage_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    /** <br> data. */

    private void initData() {
        this.adapter = new DownloadAdapter(this);
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                if (getIntent().getBooleanExtra(EXTRA_NOTIFICATION, false)) {
                    startActivity(new Intent(this, MainActivity.class));
                }
                finish();
                break;
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_path:
                PathDialog dialog = new PathDialog();
                dialog.show(getFragmentManager(), null);
                break;

            case R.id.action_cancel_all:
                DownloadHelper.getInstance(this).clearMission(this);
                adapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return SwipeBackLayout.canSwipeBack(recyclerView, dir);
    }

    @Override
    public void onSwipeFinish(int dir) {
        finish();
        switch (dir) {
            case SwipeBackLayout.UP_DIR:
                overridePendingTransition(0, R.anim.activity_slide_out_top);
                break;

            case SwipeBackLayout.DOWN_DIR:
                overridePendingTransition(0, R.anim.activity_slide_out_bottom);
                break;
        }
    }

    // on download listener.

    @Override
    public void onProcess(long id, int position) {
        DownloadAdapter.ViewHolder holder
                = (DownloadAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);

        DownloadMissionEntity entity = adapter.getItem(position);

        Cursor cursor = ((DownloadManager) getSystemService(DOWNLOAD_SERVICE)).query(
                new DownloadManager.Query().setFilterById(entity.missionId));
        cursor.moveToFirst();

        holder.title.setText(
                DownloadHelper.getInstance(this).entityList.get(position).photoId.toUpperCase()
                        + " : " + adapter.getProcess(cursor) + "%");
    }

    @Override
    public void onSuccess(long id, int position) {
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onFailed(long id, int position) {
        adapter.notifyItemChanged(position);
    }

    // snackbar container.

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                DownloadHelper.getInstance(this).refreshEntityList();
                break;
        }
    }
}
