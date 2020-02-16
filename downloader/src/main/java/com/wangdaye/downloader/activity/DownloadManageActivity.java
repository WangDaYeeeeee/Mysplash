package com.wangdaye.downloader.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.wangdaye.base.i.Downloadable;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.base.activity.ReadWriteActivity;
import com.wangdaye.base.DownloadTask;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.component.service.DownloaderService;
import com.wangdaye.downloader.DownloaderServiceIMP;
import com.wangdaye.downloader.R;
import com.wangdaye.common.utils.helper.RoutingHelper;
import com.wangdaye.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.utils.FileUtils;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.common.utils.helper.RecyclerViewHelper;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.downloader.R2;
import com.wangdaye.downloader.ui.DownloadRecyclerView;
import com.wangdaye.downloader.ui.adapter.DownloadAdapter;
import com.wangdaye.downloader.ui.PathDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nekocode.rxlifecycle.LifecycleEvent;
import cn.nekocode.rxlifecycle.RxLifecycle;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Download manage activity.
 *
 * This activity is used to show and manage download tasks.
 *
 * */

@Route(path = DownloadManageActivity.DOWNLOAD_MANAGE_ACTIVITY)
public class DownloadManageActivity extends ReadWriteActivity
        implements Toolbar.OnMenuItemClickListener, SwipeBackCoordinatorLayout.OnSwipeListener,
        DownloadAdapter.ItemEventCallback, DownloaderService.OnDownloadListener {

    @BindView(R2.id.activity_download_manage_container) CoordinatorLayout container;
    @BindView(R2.id.activity_download_manage_shadow) View shadow;
    @BindView(R2.id.activity_download_manage_swipeBackView) SwipeBackCoordinatorLayout swipeBackView;
    @BindView(R2.id.activity_download_manage_recyclerView) DownloadRecyclerView recyclerView;

    private DownloadAdapter adapter;
    private List<DownloadTask> taskList;
    private ReadWriteLock readWriteLock;
    private long updateProgressTime = -1;

    private boolean readListCompleted = false;
    private boolean destroyed = false;

    public static final String DOWNLOAD_MANAGE_ACTIVITY = "/downloader/DownloadManageActivity";
    public static final String ACTION_DOWNLOAD_MANAGE_ACTIVITY = "com.wangdaye.mysplash.DownloadManager";
    // we can get a boolean object from intent by using this string as a key.
    // If is true, that means this activity was opened by click downloading notification.
    public static final String KEY_DOWNLOAD_MANAGE_ACTIVITY_FROM_NOTIFICATION = "from_notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_mange);
        ButterKnife.bind(this);
        initData();
        initWidget();

        Observable.create(emitter -> runInWriteLock(() -> {
            DownloaderServiceIMP.getInstance().addOnDownloadListener(this);

            taskList.addAll(
                    DownloaderServiceIMP.getInstance()
                            .readDownloadTaskList(this, DownloadTask.RESULT_FAILED));
            taskList.addAll(
                    DownloaderServiceIMP.getInstance()
                            .readDownloadTaskList(this, DownloadTask.RESULT_DOWNLOADING));
            taskList.addAll(
                    DownloaderServiceIMP.getInstance()
                            .readDownloadTaskList(this, DownloadTask.RESULT_SUCCEED));

            emitter.onComplete();
        })).compose(RxLifecycle.bind(this).disposeObservableWhen(LifecycleEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    if (!destroyed) {
                        readListCompleted = true;
                        adapter.update(taskList);
                    }
                }).subscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyed = true;
        DownloaderServiceIMP.getInstance().removeOnDownloadListener(this);
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
        taskList = new ArrayList<>();
        readWriteLock = new ReentrantReadWriteLock();
        readListCompleted = false;
        destroyed = false;
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
        DisplayUtils.inflateToolbarMenu(
                toolbar, R.menu.activity_download_manage_toolbar, this);
        toolbar.setNavigationOnClickListener(v -> {
            if (MysplashApplication.getInstance().getActivityCount() == 1) {
                ComponentFactory.getMainModule().startMainActivity(this);
            }
            finishSelf(true);
        });

        adapter = new DownloadAdapter(this, new ArrayList<>()).setItemEventCallback(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(
                new GridLayoutManager(
                        this,
                        RecyclerViewHelper.getGirdColumnCount(this)
                )
        );
    }

    // control.

    private void runInWriteLock(Runnable r) {
        readWriteLock.writeLock().lock();
        r.run();
        readWriteLock.writeLock().unlock();
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
        DownloadTask newTask = DownloaderServiceIMP.getInstance().restartTask(this, task.taskId);
        if (newTask == null) {
            return;
        }

        runInWriteLock(() -> {
            for (int i = 0; i < taskList.size(); i ++) {
                if (taskList.get(i).title.equals(task.title)) {
                    taskList.remove(i);
                    break;
                }
            }
            for (int i = 0; i < taskList.size(); i ++) {
                if (taskList.get(i).result != DownloadTask.RESULT_FAILED) {
                    taskList.add(i, newTask);
                    break;
                }
            }
        });
        adapter.update(taskList);
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
            if (!readListCompleted) {
                return true;
            }
            runInWriteLock(() -> {
                DownloaderServiceIMP.getInstance().clearTask(this);
                taskList.clear();
            });
            adapter.update(taskList);
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
        runInWriteLock(() -> {
            DownloaderServiceIMP.getInstance().removeTask(this, task);
            for (int i = 0; i < taskList.size(); i ++) {
                if (taskList.get(i).title.equals(task.title)) {
                    taskList.remove(i);
                    break;
                }
            }
        });
        adapter.removeItem(task);
    }

    @Override
    public void onCheck(DownloadTask task, int adapterPosition) {
        checkDownloadResult(task);
    }

    @Override
    public void onRetry(DownloadTask task, int adapterPosition) {
        if (!readListCompleted) {
            return;
        }
        // If there is another task that is downloading the same thing, we cannot restart
        // this task.
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

    // on download listener.

    @Override
    public void onProcess(String title, int type, float process) {
        if (!readListCompleted) {
            return;
        }
        runInWriteLock(() -> {
            for (DownloadTask t : taskList) {
                if (t.result == DownloadTask.RESULT_SUCCEED) {
                    break;
                }
                if (t.title.equals(title)) {
                    t.result = DownloadTask.RESULT_DOWNLOADING;
                    t.process = process;
                    break;
                }
            }
        });

        if (System.currentTimeMillis() - updateProgressTime > 300) {
            updateProgressTime = System.currentTimeMillis();
            adapter.update(taskList);
        }
    }

    @Override
    public void onComplete(String title, int type, int result) {
        runInWriteLock(() -> {
            DownloadTask target = null;
            for (int i = 0; i < taskList.size(); i ++) {
                if (taskList.get(i).title.equals(title)) {
                    target = taskList.remove(i);
                    break;
                }
            }

            if (target != null) {
                target.result = result;
                target.process = 100;
                if (taskList.get(taskList.size() - 1).result != DownloadTask.RESULT_SUCCEED) {
                    taskList.add(target);
                } else {
                    for (int i = 0; i < taskList.size(); i ++) {
                        if (taskList.get(i).result == DownloadTask.RESULT_SUCCEED) {
                            taskList.add(i, target);
                            break;
                        }
                    }
                }
            }
        });
        adapter.update(taskList);
    }
}
