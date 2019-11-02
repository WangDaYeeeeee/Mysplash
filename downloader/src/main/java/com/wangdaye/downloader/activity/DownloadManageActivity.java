package com.wangdaye.downloader.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.wangdaye.base.i.Downloadable;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.base.activity.ReadWriteActivity;
import com.wangdaye.base.DownloadTask;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.downloader.DownloaderServiceIMP;
import com.wangdaye.downloader.R;
import com.wangdaye.common.utils.helper.RoutingHelper;
import com.wangdaye.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.ui.widget.windowInsets.StatusBarView;
import com.wangdaye.common.utils.FileUtils;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.common.utils.helper.RecyclerViewHelper;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.downloader.R2;
import com.wangdaye.downloader.ui.DownloadAdapter;
import com.wangdaye.downloader.ui.DownloadItemDecoration;
import com.wangdaye.downloader.ui.PathDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nekocode.rxlifecycle.LifecycleEvent;
import cn.nekocode.rxlifecycle.RxLifecycle;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Download manage activity.
 *
 * This activity is used to show and manage download missions.
 *
 * */

@Route(path = DownloadManageActivity.DOWNLOAD_MANAGE_ACTIVITY)
public class DownloadManageActivity extends ReadWriteActivity
        implements Toolbar.OnMenuItemClickListener, SwipeBackCoordinatorLayout.OnSwipeListener,
        DownloadAdapter.ItemEventCallback {

    @BindView(R2.id.activity_download_manage_container) CoordinatorLayout container;
    @BindView(R2.id.activity_download_manage_shadow) View shadow;
    @BindView(R2.id.activity_download_manage_swipeBackView) SwipeBackCoordinatorLayout swipeBackView;
    @BindView(R2.id.activity_download_manage_statusBar) StatusBarView statusBar;
    @BindView(R2.id.activity_download_manage_recyclerView) RecyclerView recyclerView;

    private DownloadAdapter adapter;
    private List<DownloadTask> missionList;
    private List<OnDownloadListener> listenerList;

    private boolean readListCompleted = false;
    private boolean destroyed = false;

    public static final String DOWNLOAD_MANAGE_ACTIVITY = "/downloader/DownloadManageActivity";
    public static final String ACTION_DOWNLOAD_MANAGE_ACTIVITY = "com.wangdaye.mysplash.DownloadManager";
    // we can get a boolean object from intent by using this string as a key.
    // If is true, that means this activity was opened by click downloading notification.
    public static final String KEY_DOWNLOAD_MANAGE_ACTIVITY_FROM_NOTIFICATION = "from_notification";

    public final Object synchronizedLocker = new Object();

    private class OnDownloadListener extends com.wangdaye.downloader.base.OnDownloadListener {

        OnDownloadListener(DownloadTask task) {
            super(task.taskId, task.getNotificationTitle(), task.result);
        }

        @Override
        public void onProcess(float process) {
            if (destroyed) {
                return;
            }

            findProgressingHolderAndUpdateIt(index -> {
                if (index < 0 || index >= missionList.size()) {
                    return;
                }

                synchronized (synchronizedLocker) {
                    DownloadTask task = missionList.get(index);
                    float oldProcess = task.process;
                    task.process = process;
                    if (task.result != DownloadTask.RESULT_DOWNLOADING) {
                        DownloaderServiceIMP.getInstance().updateTaskResult(
                                DownloadManageActivity.this,
                                task,
                                DownloadTask.RESULT_DOWNLOADING
                        );
                        drawRecyclerItemProcess(index, task);
                    } else if (task.process != oldProcess) {
                        drawRecyclerItemProcess(index, task);
                    }
                }
            }, taskId);
        }

        @Override
        public void onComplete(int result) {
            if (destroyed) {
                return;
            }

            listenerList.remove(this);

            findProgressingHolderAndUpdateIt(index -> {
                if (index < 0 || index >= missionList.size()) {
                    return;
                }

                synchronized (synchronizedLocker) {
                    DownloadTask task = missionList.get(index);
                    int oldResult = task.result;
                    task.result = result;
                    switch (result) {
                        case DownloadTask.RESULT_SUCCEED:
                            if (oldResult != DownloadTask.RESULT_SUCCEED) {
                                DownloaderServiceIMP.getInstance().updateTaskResult(
                                        DownloadManageActivity.this,
                                        task,
                                        DownloadTask.RESULT_SUCCEED
                                );
                                drawRecyclerItemSucceed(index, task);
                            }
                            break;

                        case DownloadTask.RESULT_FAILED:
                            if (oldResult != DownloadTask.RESULT_FAILED) {
                                DownloaderServiceIMP.getInstance().updateTaskResult(
                                        DownloadManageActivity.this,
                                        task,
                                        DownloadTask.RESULT_FAILED
                                );
                                drawRecyclerItemFailed(index, task);
                            }
                            break;

                        case DownloadTask.RESULT_DOWNLOADING:
                            break;
                    }
                }
            }, taskId);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_mange);
        ButterKnife.bind(this);
        initData();
        initWidget();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyed = true;
        if (listenerList != null) {
            DownloaderServiceIMP.getInstance().removeOnDownloadListener(listenerList);
            listenerList.clear();
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
        missionList = new ArrayList<>();
        readListCompleted = false;
        destroyed = false;

        // read failed tasks.
        missionList.addAll(
                DownloaderServiceIMP.getInstance()
                        .readDownloadTaskList(this, DownloadTask.RESULT_FAILED)
        );

        // read downloading tasks.
        List<DownloadTask> taskList = DownloaderServiceIMP.getInstance()
                .readDownloadTaskList(this, DownloadTask.RESULT_DOWNLOADING);
        listenerList = new ArrayList<>();
        DownloadTask task;
        for (int i = 0; i < taskList.size(); i ++) {
            task = DownloaderServiceIMP.getInstance().readTaskProcess(this, taskList.get(i));
            missionList.add(task);
            listenerList.add(new OnDownloadListener(task));
        }

        adapter = new DownloadAdapter(missionList).setItemEventCallback(this);

        DownloaderServiceIMP.getInstance().addOnDownloadListener(listenerList);

        // read completed tasks.
        Observable.create((ObservableOnSubscribe<List<DownloadTask>>) emitter -> {
            List<DownloadTask> failedList = new ArrayList<>(
                    DownloaderServiceIMP.getInstance()
                            .readDownloadTaskList(this, DownloadTask.RESULT_SUCCEED)
            );

            emitter.onNext(failedList);
            emitter.onComplete();
        }).compose(RxLifecycle.bind(this).disposeObservableWhen(LifecycleEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<DownloadTask>>() {
                    @Override
                    public void onNext(List<DownloadTask> list) {
                        synchronized (synchronizedLocker) {
                            int size = missionList.size();
                            missionList.addAll(list);
                            adapter.notifyItemRangeInserted(
                                    size,
                                    missionList.size() - size
                            );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        readListCompleted = true;
                    }
                });
    }

    private void initWidget() {
        swipeBackView.setOnSwipeListener(this);

        Toolbar toolbar = findViewById(R.id.activity_download_manage_toolbar);
        if (MysplashApplication.getInstance().getActivityCount() == 1) {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_home_light, R.drawable.ic_toolbar_home_dark);
        } else {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        }
        toolbar.inflateMenu(R.menu.activity_download_manage_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (MysplashApplication.getInstance().getActivityCount() == 1) {
                ComponentFactory.getMainModule().startMainActivity(this);
            }
            finishSelf(true);
        });
        toolbar.setOnMenuItemClickListener(this);

        recyclerView.setLayoutManager(
                new GridLayoutManager(
                        this,
                        RecyclerViewHelper.getGirdColumnCount(this)
                )
        );
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DownloadItemDecoration(this));
    }

    // control.

    private void findProgressingHolderAndUpdateIt(Consumer<Integer> consumer, long missionId) {
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            synchronized (synchronizedLocker) {
                for (int i = 0; i < missionList.size(); i ++) {
                    if (missionList.get(i).taskId == missionId) {
                        emitter.onNext(i);
                        return;
                    } else if (missionList.get(i).result == DownloadTask.RESULT_SUCCEED) {
                        return;
                    }
                }
            }
        }).compose(RxLifecycle.bind(this).disposeObservableWhen(LifecycleEvent.DESTROY))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(consumer)
                .subscribe();
    }

    /**
     * Make item view show downloading progress and percent.
     *
     * @param position    Adapter position for item.
     * @param mission     A {@link DownloadTask} object which saved information of downloading task. */
    private void drawRecyclerItemProcess(int position, DownloadTask mission) {
        missionList.set(position, mission);
        adapter.notifyItemChanged(position, 1);
    }

    /**
     * Make the item view to show the information that means "Download successful".
     *
     * @param position Adapter position for item.
     * @param mission  A {@link DownloadTask} object which saved information of downloading task.
     * */
    private void drawRecyclerItemSucceed(int position, DownloadTask mission) {
        missionList.remove(position);
        adapter.notifyItemRemoved(position);

        for (int i = missionList.size() - 1; i >= 0; i --) {
            if (missionList.get(i).result != DownloadTask.RESULT_SUCCEED) {
                missionList.add(i + 1, mission);
                adapter.notifyItemInserted(i + 1);
                return;
            }
        }
        missionList.add(0, mission);
        adapter.notifyItemInserted(0);
    }

    /**
     * Make the item view to show the information that means "Download failed".
     *
     * @param position Adapter position for item.
     * @param mission  A {@link DownloadTask} object which saved information of downloading task.
     * */
    private void drawRecyclerItemFailed(int position, DownloadTask mission) {
        // remove the old item and add a new item on the first position of list.

        missionList.remove(position);
        adapter.notifyItemRemoved(position);

        missionList.add(0, mission);
        adapter.notifyItemInserted(0);
    }

    private void restartTaskWithPermissionsCheck(DownloadTask task) {
        requestReadWritePermission(task, new RequestPermissionCallback() {
            @Override
            public void onGranted(Downloadable downloadable) {
                restartTask(task);
            }

            @Override
            public void onDenied(Downloadable downloadable) {
                NotificationHelper.showSnackbar(
                        DownloadManageActivity.this, getString(R.string.feedback_need_permission));
            }
        });
    }

    private void restartTask(DownloadTask task) {
        DownloadTask mission = DownloaderServiceIMP.getInstance().restartTask(this, task.taskId);
        if (mission == null) {
            return;
        }

        OnDownloadListener listener = new OnDownloadListener(mission);
        listenerList.add(listener);
        DownloaderServiceIMP.getInstance().addOnDownloadListener(listener);

        synchronized (synchronizedLocker) {
            // remove old item.
            int index = -1;
            for (int i = 0; i < missionList.size(); i ++) {
                if (missionList.get(i).taskId == task.taskId) {
                    index = i;
                    break;
                }
            }
            if (index < 0) {
                return;
            }
            missionList.remove(index);
            adapter.notifyItemRemoved(index);

            // add new item.
            index = 0;
            for (int i = 0; i < missionList.size(); i ++) {
                if (missionList.get(i).result != DownloadTask.RESULT_FAILED) {
                    index = i;
                    break;
                }
            }
            missionList.add(index, mission);
            adapter.notifyItemInserted(index);
        }
    }

    private void checkDownloadResult(DownloadTask entity) {
        if (entity.downloadType == DownloadTask.COLLECTION_TYPE) {
            if (FileUtils.isCollectionExists(this, entity.title)) {
                RoutingHelper.startCheckCollectionActivity(this, entity.title);
                return;
            }
        } else {
            if (FileUtils.isPhotoExists(this, entity.title)) {
                RoutingHelper.startCheckPhotoActivity(this, entity.title);
                return;
            }
        }
        NotificationHelper.showSnackbar(this, getString(R.string.feedback_file_does_not_exist));
    }

    // interface.

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_path) {
            PathDialog dialog = new PathDialog();
            dialog.show(getSupportFragmentManager(), null);
        } else if (i == R.id.action_cancel_all) {
            if (readListCompleted) {
                DownloaderServiceIMP.getInstance().clearTask(this, new ArrayList<>(missionList));
                missionList.clear();
                adapter.notifyDataSetChanged();
            }
        }
        return true;
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        return SwipeBackCoordinatorLayout.canSwipeBack(recyclerView, dir);
    }

    @Override
    public void onSwipeProcess(float percent) {
        statusBar.setAlpha(1 - percent);
        shadow.setAlpha(SwipeBackCoordinatorLayout.getBackgroundAlpha(percent));
    }

    @Override
    public void onSwipeFinish(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        finishSelf(false);
    }

    // item event callback.

    @Override
    public void onPhotoItemClicked(String photoId) {
        ComponentFactory.getPhotoModule().startPhotoActivity(this, photoId);
    }

    @Override
    public void onCollectionItemClicked(String collectionId) {
        ComponentFactory.getCollectionModule().startCollectionActivity(this, collectionId);
    }

    @Override
    public void onDelete(DownloadTask task, int adapterPosition) {
        if (readListCompleted && adapterPosition >= 0) {
            synchronized (synchronizedLocker) {
                DownloaderServiceIMP.getInstance().removeTask(this, task);
                missionList.remove(adapterPosition);
                adapter.notifyItemRemoved(adapterPosition);
            }
        }
    }

    @Override
    public void onCheck(DownloadTask task, int adapterPosition) {
        checkDownloadResult(task);
    }

    @Override
    public void onRetry(DownloadTask task, int adapterPosition) {
        // If there is another mission that is downloading the same thing, we cannot restart
        // this mission.
        if (task.result != DownloadTask.RESULT_DOWNLOADING
                && DownloaderServiceIMP.getInstance().isDownloading(this, task.title)) {
            NotificationHelper.showSnackbar(this, getString(R.string.feedback_download_repeat));
        } else if (FileUtils.isPhotoExists(this, task.title)
                || FileUtils.isCollectionExists(this, task.title)) {
            DownloadRepeatDialog dialog = new DownloadRepeatDialog();
            dialog.setDownloadKey(task);
            dialog.setOnCheckOrDownloadListener(new DownloadRepeatDialog.OnCheckOrDownloadListener() {
                @Override
                public void onCheck(Object obj) {
                    if (task.result == DownloadTask.RESULT_SUCCEED) {
                        checkDownloadResult(task);
                    }
                }

                @Override
                public void onDownload(Object obj) {
                    restartTaskWithPermissionsCheck(task);
                }
            });
            dialog.show(getSupportFragmentManager(), null);
        } else {
            restartTaskWithPermissionsCheck(task);
        }
    }
}
