package com.wangdaye.mysplash._common.ui.activity;

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

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.DownloadMission;
import com.wangdaye.mysplash._common.ui.dialog.PathDialog;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.data.entity.DownloadMissionEntity;
import com.wangdaye.mysplash._common.ui.adapter.DownloadAdapter;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash._common.utils.widget.SafeHandler;
import com.wangdaye.mysplash.main.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Download manage activity.
 * */

public class DownloadManageActivity extends MysplashActivity
        implements View.OnClickListener, Toolbar.OnMenuItemClickListener,
        SwipeBackCoordinatorLayout.OnSwipeListener, SafeHandler.HandlerContainer {
    // widget
    private SafeHandler<DownloadManageActivity> handler;
    private Timer timer;

    private CoordinatorLayout container;
    private StatusBarView statusBar;
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
        } else {
            this.adapter = new DownloadAdapter(this);
            recyclerView.setAdapter(adapter);
        }

        this.handler = new SafeHandler<>(this);
        this.timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.obtainMessage(1).sendToTarget();
            }
        }, 500, 500);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.activity_slide_out_bottom);
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
        handler.removeCallbacksAndMessages(null);
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

    /** <br> UI. */

    // init.

    private void initWidget() {
        this.container = (CoordinatorLayout) findViewById(R.id.activity_download_manage_container);

        SwipeBackCoordinatorLayout swipeBackView
                = (SwipeBackCoordinatorLayout) findViewById(R.id.activity_download_manage_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        this.statusBar = (StatusBarView) findViewById(R.id.activity_download_manage_statusBar);
        if (DisplayUtils.isNeedSetStatusBarMask()) {
            statusBar.setBackgroundResource(R.color.colorPrimary_light);
            statusBar.setMask(true);
        }

        boolean openByNotification = getIntent().getBooleanExtra(EXTRA_NOTIFICATION, false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_download_manage_toolbar);
        if (Mysplash.getInstance().isLightTheme()) {
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

        this.recyclerView = (RecyclerView) findViewById(R.id.activity_download_manage_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    // interface.

    private void makeRecyclerItemDrawProcess(int position, DownloadMission mission, Cursor cursor) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        int lastPosition = layoutManager.findLastVisibleItemPosition();
        if (firstPosition <= position && position <= lastPosition) {
            DownloadAdapter.ViewHolder holder
                    = (DownloadAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
            if (cursor != null) {
                holder.drawProcessStatus(mission.entity, cursor);
            } else {
                holder.drawProcessStatus(mission.entity);
            }
        }
    }

    private void makeRecyclerItemDrawSuccess(int position) {
        adapter.itemList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    private void makeRecyclerItemDrawFailed(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        int lastPosition = layoutManager.findLastVisibleItemPosition();
        if (firstPosition <= position && position <= lastPosition) {
            DownloadAdapter.ViewHolder holder
                    = (DownloadAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
            holder.drawFailedStatus();
        }
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
                finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
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
                List<DownloadMissionEntity> entityList = new ArrayList<>();
                for (int i = 0; i < adapter.getRealItemCount(); i ++) {
                    entityList.add(adapter.itemList.get(i).entity);
                }
                DownloadHelper.getInstance(this).clearMission(this, entityList);
                adapter.itemList.clear();
                adapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return SwipeBackCoordinatorLayout.canSwipeBackForThisView(recyclerView, dir);
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
                Cursor cursor;
                long missionId;
                for (int i = 0; i < adapter.getRealItemCount(); i ++) {
                    missionId = adapter.itemList.get(i).entity.missionId;
                    cursor = DownloadHelper.getInstance(this).getMissionCursor(missionId);
                    if (cursor != null) {
                        if (DownloadHelper.isMissionFailed(cursor)) {
                            if (adapter.itemList.get(i).process != -1) {
                                adapter.itemList.get(i).process = -1;
                                makeRecyclerItemDrawFailed(i);
                            }
                        } else if (DownloadHelper.isMissionSuccess(cursor)) {
                            if (adapter.itemList.get(i).process != 100) {
                                adapter.itemList.get(i).process = 100;
                                makeRecyclerItemDrawSuccess(i);
                                i --;
                            }
                        } else {
                            float percent = DownloadHelper.getMissionProcess(cursor);
                            if (adapter.itemList.get(i).process != percent) {
                                adapter.itemList.get(i).process = percent;
                                makeRecyclerItemDrawProcess(i, adapter.itemList.get(i), cursor);
                            }
                        }
                    } else {
                        if (adapter.itemList.get(i).process != 0) {
                            adapter.itemList.get(i).process = 0;
                            makeRecyclerItemDrawProcess(i, adapter.itemList.get(i), null);
                        }
                    }
                }
                break;
        }
    }
}
