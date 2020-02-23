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
import com.wangdaye.common.base.widget.LockableList;
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
    private LockableList<DownloadTask> lockableTaskList;
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

        Observable.create(emitter -> lockableTaskList.write((list, setter) -> {
            DownloaderServiceIMP.getInstance().addOnDownloadListener(this);

            List<DownloadTask> taskList = new ArrayList<>();
            taskList.addAll(
                    DownloaderServiceIMP.getInstance()
                            .readDownloadTaskList(DownloadTask.RESULT_FAILED));
            taskList.addAll(
                    DownloaderServiceIMP.getInstance()
                            .readDownloadTaskList(DownloadTask.RESULT_DOWNLOADING));
            taskList.addAll(
                    DownloaderServiceIMP.getInstance()
                            .readDownloadTaskList(DownloadTask.RESULT_SUCCEED));
            setter.setList(taskList);

            emitter.onComplete();
        })).compose(RxLifecycle.bind(this).disposeObservableWhen(LifecycleEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    if (!destroyed) {
                        readListCompleted = true;
                        lockableTaskList.read(list -> adapter.update(list));
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
        lockableTaskList = new LockableList<>(new ArrayList<>());
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

        lockableTaskList.write((list, setter) -> {
            for (int i = 0; i < list.size(); i ++) {
                if (list.get(i).title.equals(task.title)) {
                    list.remove(i);
                    break;
                }
            }
            for (int i = 0; i < list.size(); i ++) {
                if (list.get(i).result != DownloadTask.RESULT_FAILED) {
                    list.add(i, newTask);
                    break;
                }
            }
            setter.setList(list);
        });
        lockableTaskList.read(list -> adapter.update(list));
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
            lockableTaskList.write((list, setter) -> {
                DownloaderServiceIMP.getInstance().clearTask(this);

                list.clear();
                setter.setList(list);
            });
            lockableTaskList.read(list -> adapter.update(list));
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
        lockableTaskList.write((list, setter) -> {
            DownloaderServiceIMP.getInstance().removeTask(this, task);

            for (int i = 0; i < list.size(); i ++) {
                if (list.get(i).title.equals(task.title)) {
                    list.remove(i);
                    break;
                }
            }
            setter.setList(list);
        });
        lockableTaskList.read(list -> adapter.update(list));
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
        lockableTaskList.write((list, setter) -> {
            for (DownloadTask t : list) {
                if (t.result == DownloadTask.RESULT_SUCCEED) {
                    break;
                }
                if (t.title.equals(title)) {
                    t.result = DownloadTask.RESULT_DOWNLOADING;
                    t.process = process;
                    break;
                }
            }
            setter.setList(list);
        });

        if (System.currentTimeMillis() - updateProgressTime > 300) {
            updateProgressTime = System.currentTimeMillis();
            lockableTaskList.read(list -> adapter.update(list));
        }
    }

    @Override
    public void onComplete(String title, int type, int result) {
        lockableTaskList.write((list, setter) -> {
            DownloadTask target = null;
            for (int i = 0; i < list.size(); i ++) {
                if (list.get(i).title.equals(title)) {
                    target = list.remove(i);
                    break;
                }
            }

            if (target != null) {
                target.result = result;
                target.process = 100;
                if (list.get(list.size() - 1).result != DownloadTask.RESULT_SUCCEED) {
                    list.add(target);
                } else {
                    for (int i = 0; i < list.size(); i ++) {
                        if (list.get(i).result == DownloadTask.RESULT_SUCCEED) {
                            list.add(i, target);
                            break;
                        }
                    }
                }
            }
        });
        lockableTaskList.read(list -> adapter.update(list));
    }
}
