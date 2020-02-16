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

public interface DownloaderService {

    interface OnDownloadListener {

        void onProcess(String title,
                       @DownloadTask.DownloadTypeRule int type,
                       float process);
        void onComplete(String title,
                        @DownloadTask.DownloadTypeRule int type,
                        @DownloadTask.DownloadResultRule int result);
    }

    void addOnDownloadListener(@NonNull OnDownloadListener l);
    void removeOnDownloadListener(@NonNull OnDownloadListener l);

    String DOWNLOADER_MYSPLASH = "mysplash";
    String DOWNLOADER_SYSTEM = "system";
    @StringDef({DOWNLOADER_MYSPLASH, DOWNLOADER_SYSTEM})
    @interface DownloaderRule {}

    boolean switchDownloader(Context context, @DownloaderRule String downloader);

    void addTask(Context context, Photo photo, @DownloadTask.DownloadTypeRule int type, String downloadScale);

    void addTask(Context context, Collection collection);

    void removeTask(Context context, @NonNull DownloadTask task);

    void clearTask(Context context);

    boolean isDownloading(Context c, String title);

    @Nullable
    DownloadTask readDownloadTask(Context context, String title);

    void startDownloadManageActivity(Activity activity);

    void startDownloadManageActivityFromNotification(Context context);

    Intent getDownloadManageActivityIntentForShortcut();

    Intent getDownloadManageActivityIntentForNotification();
}
