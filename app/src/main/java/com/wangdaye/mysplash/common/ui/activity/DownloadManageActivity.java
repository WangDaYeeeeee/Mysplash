package com.wangdaye.mysplash.common.ui.activity;

import android.os.Build;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.ReadWriteActivity;
import com.wangdaye.mysplash.common.db.DatabaseHelper;
import com.wangdaye.mysplash.common.download.DownloadMission;
import com.wangdaye.mysplash.common.download.imp.DownloaderService;
import com.wangdaye.mysplash.common.ui.dialog.PathDialog;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.download.DownloadHelper;
import com.wangdaye.mysplash.common.db.DownloadMissionEntity;
import com.wangdaye.mysplash.common.ui.adapter.DownloadAdapter;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

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
        implements Toolbar.OnMenuItemClickListener, SwipeBackCoordinatorLayout.OnSwipeListener,
        DownloadAdapter.ItemEventCallback {

    @BindView(R.id.activity_download_manage_container) CoordinatorLayout container;
    @BindView(R.id.activity_download_manage_shadow) View shadow;
    @BindView(R.id.activity_download_manage_statusBar) StatusBarView statusBar;
    @BindView(R.id.activity_download_manage_recyclerView) RecyclerView recyclerView;

    private DownloadAdapter adapter;
    // if we need to restart a mission, we need save it by this object and request permission.
    private DownloadMissionEntity readyToDownloadEntity;

    private List<OnDownloadListener> listenerList;

    public static final String ACTION_DOWNLOAD_MANAGER = "com.wangdaye.mysplash.DownloadManager";

    // we can get a boolean object from intent by using this string as a key.
    // If is true, that means this activity was opened by click downloading notification.
    public static final String EXTRA_NOTIFICATION = "notification";

    private class OnDownloadListener extends DownloaderService.OnDownloadListener {

        OnDownloadListener(DownloadMission mission) {
            super(mission.entity.missionId, mission.entity.getNotificationTitle(), mission.entity.result);
        }

        @Override
        public void onProcess(float process) {
            int index = locateMission();
            if (index == -1) {
                // cannot find the mission's position.
                return;
            }
            DownloadMission mission = adapter.itemList.get(index);
            float oldProcess = mission.process;
            mission.process = process;
            if (mission.entity.result != DownloaderService.RESULT_DOWNLOADING) {
                DownloadHelper.getInstance(DownloadManageActivity.this)
                        .updateMissionResult(
                                DownloadManageActivity.this,
                                mission.entity,
                                DownloaderService.RESULT_DOWNLOADING);
                drawRecyclerItemProcess(index, mission);
            } else if (mission.process != oldProcess) {
                drawRecyclerItemProcess(index, mission);
            }
        }

        @Override
        public void onComplete(int result) {
            listenerList.remove(this);

            int index = locateMission();
            if (index == -1) {
                // cannot find the mission's position.
                return;
            }
            DownloadMission mission = adapter.itemList.get(index);
            int oldResult = mission.entity.result;
            mission.entity.result = result;
            switch (result) {
                case DownloaderService.RESULT_SUCCEED:
                    if (oldResult != DownloaderService.RESULT_SUCCEED) {
                        DownloadHelper.getInstance(DownloadManageActivity.this)
                                .updateMissionResult(
                                        DownloadManageActivity.this,
                                        mission.entity,
                                        DownloaderService.RESULT_SUCCEED);
                        drawRecyclerItemSucceed(index, mission);
                    }
                    break;

                case DownloaderService.RESULT_FAILED:
                    if (oldResult != DownloaderService.RESULT_FAILED) {
                        DownloadHelper.getInstance(DownloadManageActivity.this)
                                .updateMissionResult(
                                        DownloadManageActivity.this,
                                        mission.entity,
                                        DownloaderService.RESULT_FAILED);
                        drawRecyclerItemFailed(index, mission);
                    }
                    break;

                case DownloaderService.RESULT_DOWNLOADING:
                    break;
            }
        }

        private int locateMission() {
            for (int i = 0; i < adapter.getItemCount(); i ++) {
                if (adapter.itemList.get(i).entity.missionId == missionId) {
                    return i;
                }
            }
            return -1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_mange);
        ButterKnife.bind(this);
        initData();
        initWidget();

        listenerList = new ArrayList<>();
        for (int i = 0; i < adapter.getItemCount(); i ++) {
            if (adapter.itemList.get(i).entity.result == DownloaderService.RESULT_DOWNLOADING) {
                OnDownloadListener listener = new OnDownloadListener(adapter.itemList.get(i));
                listenerList.add(listener);
                DownloadHelper.getInstance(this).addOnDownloadListener(listener);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerList != null) {
            for (int i = listenerList.size() - 1; i >= 0; i --) {
                DownloadHelper.getInstance(this).removeOnDownloadListener(listenerList.get(i));
                listenerList.remove(i);
            }
        }
    }

    @Override
    public void handleBackPressed() {
        finishSelf(true);
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
        List<DownloadMission> missionList = new ArrayList<>();
        List<DownloadMissionEntity> entityList;
        entityList = DatabaseHelper.getInstance(this).readDownloadEntityList(DownloaderService.RESULT_FAILED);
        for (int i = 0; i < entityList.size(); i ++) {
            missionList.add(
                    new DownloadMission(
                            entityList.get(i)));
        }
        entityList = DatabaseHelper.getInstance(this).readDownloadEntityList(DownloaderService.RESULT_DOWNLOADING);
        for (int i = 0; i < entityList.size(); i ++) {
            missionList.add(DownloadHelper.getInstance(this).getDownloadMission(this, entityList.get(i)));
        }
        entityList = DatabaseHelper.getInstance(this).readDownloadEntityList(DownloaderService.RESULT_SUCCEED);
        for (int i = 0; i < entityList.size(); i ++) {
            missionList.add(
                    new DownloadMission(
                            entityList.get(i)));
        }
        this.adapter = new DownloadAdapter(missionList);
        adapter.setItemEventCallback(this);
    }

    private void initWidget() {
        SwipeBackCoordinatorLayout swipeBackView = findViewById(R.id.activity_download_manage_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        Toolbar toolbar = findViewById(R.id.activity_download_manage_toolbar);
        if (Mysplash.getInstance().getActivityCount() == 1) {
            ThemeManager.setNavigationIcon(
                    toolbar,
                    R.drawable.ic_toolbar_home_light, R.drawable.ic_toolbar_home_dark);
        } else {
            ThemeManager.setNavigationIcon(
                    toolbar,
                    R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        }
        toolbar.inflateMenu(R.menu.activity_download_manage_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (Mysplash.getInstance().getActivityCount() == 1) {
                IntentHelper.startMainActivity(DownloadManageActivity.this);
            }
            finishSelf(true);
        });
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
     * @param mission     A {@link DownloadMission} object which saved information of downloading task. */
    private void drawRecyclerItemProcess(int position, DownloadMission mission) {
        adapter.itemList.set(position, mission);

        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstPosition = layoutManager.findFirstVisibleItemPosition();
            int lastPosition = layoutManager.findLastVisibleItemPosition();
            if (firstPosition <= position && position <= lastPosition) {
                // we doesn't need to refresh a item view that is not displayed.
                adapter.updateItemView(position);
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
            if (adapter.itemList.get(i).entity.result != DownloaderService.RESULT_SUCCEED) {
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
        if (mission == null) {
            return;
        }

        OnDownloadListener listener = new OnDownloadListener(mission);
        listenerList.add(listener);
        DownloadHelper.getInstance(this).addOnDownloadListener(listener);

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
                if (adapter.itemList.get(i).entity.result != DownloaderService.RESULT_FAILED) {
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
    protected void requestReadWritePermissionSucceed(Downloadable downloadable, int requestCode) {
        restartMission();
    }

    // interface.

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_path:
                PathDialog dialog = new PathDialog();
                dialog.show(getSupportFragmentManager(), null);
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

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return SwipeBackCoordinatorLayout.canSwipeBack(recyclerView, dir);
    }

    @Override
    public void onSwipeProcess(float percent) {
        statusBar.setAlpha(1 - percent);
        shadow.setAlpha(SwipeBackCoordinatorLayout.getBackgroundAlpha(percent));
    }

    @Override
    public void onSwipeFinish(int dir) {
        finishSelf(false);
    }

    // item event callback.

    @Override
    public void onDelete(DownloadMissionEntity entity, int adapterPosition) {
        DownloadHelper.getInstance(this).removeMission(this, entity);
        adapter.itemList.remove(adapterPosition);
        adapter.notifyItemRemoved(adapterPosition);
    }

    @Override
    public void onRetry(DownloadMissionEntity entity, int adapterPosition) {
        readyToDownloadEntity = entity;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            restartMission();
        } else {
            requestReadWritePermission(null);
        }
    }
}
