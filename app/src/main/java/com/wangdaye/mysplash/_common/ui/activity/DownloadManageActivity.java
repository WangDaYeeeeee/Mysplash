package com.wangdaye.mysplash._common.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.item.DownloadMission;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.dialog.PathDialog;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash._common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.data.entity.table.DownloadMissionEntity;
import com.wangdaye.mysplash._common.ui.adapter.DownloadAdapter;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash._common.utils.manager.ThreadManager;
import com.wangdaye.mysplash._common.utils.widget.SafeHandler;
import com.wangdaye.mysplash._common._basic.FlagRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Download manage activity.
 * */

public class DownloadManageActivity extends MysplashActivity
        implements View.OnClickListener, Toolbar.OnMenuItemClickListener,
        DownloadAdapter.OnRetryListener, SwipeBackCoordinatorLayout.OnSwipeListener,
        SafeHandler.HandlerContainer {
    // widget
    private SafeHandler<DownloadManageActivity> handler;

    private CoordinatorLayout container;
    private StatusBarView statusBar;
    private RecyclerView recyclerView;

    // data
    private DownloadAdapter adapter;
    private DownloadMissionEntity readyToDownloadEntity;
    public static final String EXTRA_NOTIFICATION = "notification";

    private final int CHECK_AND_UPDATE = 1;

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
            ThreadManager.getInstance().execute(checkRunnable);
        }
    }

    @Override
    public void handleBackPressed() {
        finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public View getSnackbarContainer() {
        return container;
    }

    /** <br> UI. */

    // init.

    private void initWidget() {
        this.handler = new SafeHandler<>(this);

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

    private void drawRecyclerItemProcess(int position, DownloadMission mission, boolean switchState) {
        adapter.itemList.set(position, mission);

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        int lastPosition = layoutManager.findLastVisibleItemPosition();
        if (firstPosition <= position && position <= lastPosition) {
            DownloadAdapter.ViewHolder holder
                    = (DownloadAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                holder.drawProcessStatus(mission, switchState);
            }
        }
    }

    private void drawRecyclerItemSucceed(int position, DownloadMission mission) {
        adapter.itemList.remove(position);
        adapter.notifyItemRemoved(position);

        for (int i = adapter.itemList.size() - 1; i >= 0; i --) {
            if (adapter.itemList.get(i).entity.result != DownloadHelper.RESULT_SUCCEED) {
                adapter.itemList.add(i + 1, mission);
                adapter.notifyItemInserted(i + 1);
                return;
            }
        }
        adapter.itemList.add(0, mission);
        adapter.notifyItemInserted(0);
    }

    private void drawRecyclerItemFailed(int position, DownloadMission mission) {
        adapter.itemList.remove(position);
        adapter.notifyItemRemoved(position);

        adapter.itemList.add(0, mission);
        adapter.notifyItemInserted(0);
    }

    private void restartMission() {
        if (readyToDownloadEntity == null) {
            return;
        }

        long oldId = readyToDownloadEntity.missionId;
        readyToDownloadEntity = null;

        DownloadMission mission = DownloadHelper.getInstance(this).restartMission(this, oldId);

        for (int i = 0; i < adapter.itemList.size(); i ++) {
            if (adapter.itemList.get(i).entity.missionId == oldId) {
                adapter.itemList.remove(i);
                adapter.notifyItemRemoved(i);
                break;
            }
        }
        if (adapter.itemList.size() > 0) {
            for (int i = 0; i < adapter.itemList.size(); i ++) {
                if (adapter.itemList.get(i).entity.result != DownloadHelper.RESULT_FAILED) {
                    adapter.itemList.add(i, mission);
                    adapter.notifyItemInserted(i);
                    return;
                }
            }
            adapter.itemList.add(adapter.itemList.size(), mission);
            adapter.notifyItemInserted(adapter.itemList.size() - 1);
        } else {
            adapter.itemList.add(0, mission);
            adapter.notifyItemInserted(0);
        }
    }

    /** <br> data. */

    private void initData() {
        this.adapter = new DownloadAdapter(this, this);
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
                    restartMission();
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
                        restartMission();
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
            case -1:
                if (getIntent().getBooleanExtra(EXTRA_NOTIFICATION, false)) {
                    IntentHelper.startMainActivity(this);
                }
                finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
                break;
        }
    }

    // on menu item click swipeListener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_path:
                PathDialog dialog = new PathDialog();
                dialog.show(getFragmentManager(), null);
                break;

            case R.id.action_cancel_all:
                List<DownloadMissionEntity> entityList = new ArrayList<>();
                for (int i = 0; i < adapter.getItemCount(); i ++) {
                    entityList.add(adapter.itemList.get(i).entity);
                }
                DownloadHelper.getInstance(this).clearMission(this, entityList);
                adapter.itemList.clear();
                adapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    // on retry swipeListener.

    @Override
    public void onRetry(DownloadMissionEntity entity) {
        readyToDownloadEntity = entity;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            restartMission();
        } else {
            requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, 0);
        }
    }

    // on swipe swipeListener.

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

    // handler.

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case CHECK_AND_UPDATE:
                if (message.obj != null && message.obj instanceof DownloadMission) {
                    DownloadMission newMission = (DownloadMission) message.obj;
                    int index = -1;
                    for (int i = 0; i < adapter.getItemCount(); i ++) {
                        if (adapter.itemList.get(i).entity.missionId == newMission.entity.missionId) {
                            index = i;
                            break;
                        }
                    }
                    if (index == -1) {
                        return;
                    }
                    DownloadMission oldMission = adapter.itemList.get(index);
                    switch (newMission.entity.result) {
                        case DownloadHelper.RESULT_DOWNLOADING:
                            if (oldMission.entity.result != DownloadHelper.RESULT_DOWNLOADING) {
                                DownloadHelper.getInstance(this)
                                        .updateMissionResult(
                                                this,
                                                newMission.entity.missionId,
                                                DownloadHelper.RESULT_DOWNLOADING);
                                drawRecyclerItemProcess(index, newMission, true);
                            } else if (oldMission.process != newMission.process) {
                                drawRecyclerItemProcess(index, newMission, false);
                            }
                            break;

                        case DownloadHelper.RESULT_SUCCEED:
                            if (oldMission.entity.result != DownloadHelper.RESULT_SUCCEED) {
                                DownloadHelper.getInstance(this)
                                        .updateMissionResult(
                                                this,
                                                newMission.entity.missionId,
                                                DownloadHelper.RESULT_SUCCEED);
                                drawRecyclerItemSucceed(index, newMission);
                            }
                            break;

                        case DownloadHelper.RESULT_FAILED:
                            if (oldMission.entity.result != DownloadHelper.RESULT_FAILED) {
                                DownloadHelper.getInstance(this)
                                        .updateMissionResult(
                                                this,
                                                newMission.entity.missionId,
                                                DownloadHelper.RESULT_FAILED);
                                drawRecyclerItemFailed(index, newMission);
                            }
                            break;
                    }
                }
                break;
        }
    }

    /** <br> thread. */

    private FlagRunnable checkRunnable = new FlagRunnable(true) {
        @Override
        public void run() {
            while (isRunning()) {
                for (int i = 0; isRunning() && i < adapter.itemList.size(); i ++) {
                    if (adapter.itemList.get(i).entity.result == DownloadHelper.RESULT_DOWNLOADING) {
                        DownloadMission mission = DownloadHelper.getInstance(DownloadManageActivity.this)
                                .getDownloadMission(
                                        DownloadManageActivity.this,
                                        adapter.itemList.get(i).entity.missionId);
                        if (mission != null
                                && (mission.entity.result == DownloadHelper.RESULT_DOWNLOADING
                                || mission.entity.result != adapter.itemList.get(i).entity.result)) {
                            handler.obtainMessage(CHECK_AND_UPDATE, mission).sendToTarget();
                        }
                        SystemClock.sleep(50);
                    }
                }
                SystemClock.sleep(50);
            }
        }
    };
}
