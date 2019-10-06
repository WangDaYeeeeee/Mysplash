package com.wangdaye.component.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;

import com.wangdaye.base.DownloadTask;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.Photo;

import java.util.List;

public interface DownloaderService {

    String DOWNLOADER_MYSPLASH = "mysplash";
    String DOWNLOADER_SYSTEM = "system";
    @StringDef({DOWNLOADER_MYSPLASH, DOWNLOADER_SYSTEM})
    @interface DownloaderRule {}

    boolean switchDownloader(Context context, @DownloaderRule String downloader);

    void addTask(Context c, Photo p, @DownloadTask.DownloadTypeRule int type, String downloadScale);

    void addTask(Context c, Collection collection);

    void removeTask(Context c, @NonNull DownloadTask entity);

    void clearTask(Context c, @Nullable List<DownloadTask> entityList);

    boolean isDownloading(Context c, String title);

    @Nullable
    DownloadTask readDownloadTask(Context context, String title);

    void startDownloadManageActivity(Activity a);

    void startDownloadManageActivityFromNotification(Context context);

    Intent getDownloadManageActivityIntentForShortcut();

    Intent getDownloadManageActivityIntentForNotification();
}
