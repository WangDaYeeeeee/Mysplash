package com.wangdaye.mysplash._common.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.tools.DownloadManager;
import com.wangdaye.mysplash._common.ui.adapter.DownloadAdapter;
import com.wangdaye.mysplash._common.ui.widget.StatusBarView;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackLayout;
import com.wangdaye.mysplash._common.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Download manage activity.
 * */

public class DownloadManageActivity extends MysplashActivity
        implements View.OnClickListener, Toolbar.OnMenuItemClickListener, SwipeBackLayout.OnSwipeListener,
        DownloadManager.OnDownloadListener, DownloadAdapter.OnDownloadResponseListener {
    // widget
    private RecyclerView recyclerView;

    // data
    private DownloadAdapter adapter;

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
            DownloadManager.getInstance().addOnDownloadListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadManager.getInstance().removeDownloadListener(this);
    }

    @Override
    protected void setTheme() {
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent);
        }
    }

    /** <br> UI. */

    private void initWidget() {
        SwipeBackLayout swipeBackLayout = (SwipeBackLayout) findViewById(R.id.activity_download_manage_swipeBackLayout);
        swipeBackLayout.setOnSwipeListener(this);

        StatusBarView statusBar = (StatusBarView) findViewById(R.id.activity_download_manage_statusBar);
        if (ThemeUtils.getInstance(this).isNeedSetStatusBarMask()) {
            statusBar.setMask(true);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_download_manage_toolbar);
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
            toolbar.inflateMenu(R.menu.activity_download_manage_toolbar_light);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
            toolbar.inflateMenu(R.menu.activity_download_manage_toolbar_dark);
        }
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnMenuItemClickListener(this);

        this.recyclerView = (RecyclerView) findViewById(R.id.activity_download_manage_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    /** <br> data. */

    private void initData() {
        List<DownloadManager.Mission> list = new ArrayList<>();
        list.addAll(DownloadManager.getInstance().getMissionList());
        this.adapter = new DownloadAdapter(this, list);
        adapter.setOnDownloadResponseListener(this);
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                finish();
                break;
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel_all:
                adapter.clearItem();
                DownloadManager.getInstance().cancelAll();
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
    public void onSwipeFinish() {
        finish();
    }

    // on download listener.

    @Override
    public void onDownloadComplete(int id) {
        adapter.removeItem(id);
    }

    @Override
    public void onDownloadFailed(int id, int code) {
        adapter.setItemFailed(id);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDownloadProgress(int id, int percent) {
        adapter.setItemProgress(id, percent);
        int position = adapter.getItemAdapterPosition(id);
        if (position != DownloadManager.FAILED_CODE) {
            DownloadAdapter.ViewHolder holder
                    = (DownloadAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
            DownloadManager.Mission mission = adapter.getItem(position);
            ((TextView) holder.itemView.findViewById(R.id.item_download_title))
                    .setText(mission.photo.id.toUpperCase() + " : " + mission.progress + "%");
        }
    }

    // on download response listener.

    @Override
    public void onCancelDownload(DownloadManager.Mission m) {
        DownloadManager.getInstance().cancel(m.photo.id);
    }

    @Override
    public void onRetryDownload(DownloadManager.Mission m) {
        DownloadManager.Mission newMission = DownloadManager.getInstance().retry(m.photo.id, this);
        adapter.insertItem(newMission, 0);
    }
}
