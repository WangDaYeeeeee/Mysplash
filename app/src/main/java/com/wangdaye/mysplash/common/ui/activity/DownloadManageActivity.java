package com.wangdaye.mysplash.common.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.ReadWriteActivity;
import com.wangdaye.mysplash.common.data.entity.item.DownloadMission;
import com.wangdaye.mysplash.common.ui.dialog.PathDialog;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash.common.data.entity.table.DownloadMissionEntity;
import com.wangdaye.mysplash.common.ui.adapter.DownloadAdapter;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.utils.manager.ThreadManager;
import com.wangdaye.mysplash.common.utils.widget.SafeHandler;
import com.wangdaye.mysplash.common._basic.FlagRunnable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Download manage activity.
 *
 * This activity is used to show and manage download missions.
 *
 * */

public class DownloadManageActivity extends ReadWriteActivity
        implements View.OnClickListener, Toolbar.OnMenuItemClickListener,
        DownloadAdapter.OnRetryListener, SwipeBackCoordinatorLayout.OnSwipeListener,
        SafeHandler.HandlerContainer {

    @BindView(R.id.activity_download_manage_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_download_manage_statusBar)
    StatusBarView statusBar;

    @BindView(R.id.activity_download_manage_recyclerView)
    RecyclerView recyclerView;

    private SafeHandler<DownloadManageActivity> handler;

    private DownloadAdapter adapter;
    // if we need to restart a mission, we need save it by this object and request permission.
    private DownloadMissionEntity readyToDownloadEntity;

    public static final String ACTION_DOWNLOAD_MANAGER = "com.wangdaye.mysplash.DownloadManager";

    // we can get a boolean object from intent by using this string as a key.
    // If is true, that means this activity was opened by click downloading notification.
    public static final String EXTRA_NOTIFICATION = "notification";

    private final int CHECK_AND_UPDATE = 1;

    /**
     * This Runnable class is used to poll download progress.
     * */
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
                            // only if the state of mission has changed or the progress changed,
                            // then we should send a message to update the item view.
                            handler.obtainMessage(CHECK_AND_UPDATE, mission).sendToTarget();
                        }
                        SystemClock.sleep(50);
                    }
                }
                SystemClock.sleep(50);
            }
        }
    };

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
            ButterKnife.bind(this);
            initData();
            initWidget();

            checkRunnable.setRunning(true);
            ThreadManager.getInstance().execute(checkRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkRunnable.setRunning(false);
        handler.removeCallbacksAndMessages(null);
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
        finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
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
        this.adapter = new DownloadAdapter(this, this);
    }

    private void initWidget() {
        this.handler = new SafeHandler<>(this);

        SwipeBackCoordinatorLayout swipeBackView = ButterKnife.findById(
                this, R.id.activity_download_manage_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.activity_download_manage_toolbar);
        if (Mysplash.getInstance().getActivityCount() == 1) {
            ThemeManager.setNavigationIcon(
                    toolbar,
                    R.drawable.ic_toolbar_home_light, R.drawable.ic_toolbar_home_dark);
        } else {
            ThemeManager.setNavigationIcon(
                    toolbar,
                    R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        }
        ThemeManager.inflateMenu(toolbar,
                R.menu.activity_download_manage_toolbar_light,
                R.menu.activity_download_manage_toolbar_dark);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnMenuItemClickListener(this);

        recyclerView.setLayoutManager(
                new GridLayoutManager(this, DisplayUtils.getGirdColumnCount(this)));
        recyclerView.setAdapter(adapter);
    }

    // control.

    /**
     * Make item view show downloading progress and percent.
     *
     * @param position    Adapter position for item.
     * @param mission     A {@link DownloadMission} object which saved information of downloading task.
     * @param switchState If set true, that means the item view will switch from another state to
     *                    the downloading state.
     * */
    private void drawRecyclerItemProcess(int position, DownloadMission mission, boolean switchState) {
        adapter.itemList.set(position, mission);

        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        int lastPosition = layoutManager.findLastVisibleItemPosition();
        if (firstPosition <= position && position <= lastPosition) {
            // we doesn't need to refresh a item view that is not displayed.
            DownloadAdapter.ViewHolder holder
                    = (DownloadAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                holder.drawProcessStatus(mission, switchState);
            }
        }
    }

    /**
     * Make the item view to show the information that means "Download successful".
     *
     * @param position Adapter position for item.
     * @param mission  A {@link DownloadMission} object which saved information of downloading task.
     * */
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

    /**
     * Make the item view to show the information that means "Download failed".
     *
     * @param position Adapter position for item.
     * @param mission  A {@link DownloadMission} object which saved information of downloading task.
     * */
    private void drawRecyclerItemFailed(int position, DownloadMission mission) {
        // remove the old item and add a new item on the first position of list.

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

        // remove the old item.
        for (int i = 0; i < adapter.itemList.size(); i ++) {
            if (adapter.itemList.get(i).entity.missionId == oldId) {
                adapter.itemList.remove(i);
                adapter.notifyItemRemoved(i);
                break;
            }
        }
        // add the new item.
        if (adapter.itemList.size() > 0) {
            // if the list's size > 0, we need find the last failed mission item and add the new item after it.
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

    // permission.

    @Override
    protected void requestReadWritePermissionSucceed(int requestCode) {
        restartMission();
    }

    // interface.

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                if (Mysplash.getInstance().getActivityCount() == 1) {
                    IntentHelper.startMainActivity(this);
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

    // on retry listener.

    @Override
    public void onRetry(DownloadMissionEntity entity) {
        readyToDownloadEntity = entity;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            restartMission();
        } else {
            requestReadWritePermission();
        }
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return SwipeBackCoordinatorLayout.canSwipeBack(recyclerView, dir);
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
                    // try to find the mission's position.
                    int index = -1;
                    for (int i = 0; i < adapter.getItemCount(); i ++) {
                        if (adapter.itemList.get(i).entity.missionId == newMission.entity.missionId) {
                            index = i;
                            break;
                        }
                    }
                    if (index == -1) {
                        // cannot find the mission's position.
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
}
