package com.wangdaye.component.service;

import androidx.annotation.Nullable;

import com.wangdaye.base.DownloadTask;
import com.wangdaye.base.MuzeiWallpaperSource;

import java.util.List;

public interface DatabaseService {

    // download task.

    void writeDownloadTask(DownloadTask task);

    void deleteDownloadTask(long missionId);

    void clearDownloadTask();

    void updateDownloadTask(DownloadTask task);

    List<DownloadTask> readDownloadTaskList();

    List<DownloadTask> readDownloadTaskList(@DownloadTask.DownloadResultRule int result);

    int readDownloadTaskCount(@DownloadTask.DownloadResultRule int result);

    @Nullable
    DownloadTask readDownloadTask(long missionId);

    @Nullable
    DownloadTask readDownloadingTask(String title);

    int readDownloadingTaskCount(String title);

    // muzie wallpaper source.

    void writeMuzeiWallpaperSource(MuzeiWallpaperSource source);

    void writeMuzeiWallpaperSource(List<MuzeiWallpaperSource> list);

    void deleteMuzeiWallpaperSource(long collectionId);

    void clearMuzeiWallpaperSource();

    void updateMuzeiWallpaperSource(MuzeiWallpaperSource source);

    List<MuzeiWallpaperSource> readMuzeiWallpaperSourceList();

    @Nullable
    MuzeiWallpaperSource readMuzeiWallpaperSource(long collectionId);
}
