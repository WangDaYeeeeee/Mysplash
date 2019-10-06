package com.wangdaye.downloader.base;

import com.wangdaye.base.DownloadTask;

public abstract class OnDownloadListener {

    public long taskId;
    public String taskTitle;

    @DownloadTask.DownloadResultRule
    public int result;

    public OnDownloadListener(long taskId, String taskTitle,
                              @DownloadTask.DownloadResultRule int result) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.result = result;
    }

    public abstract void onProcess(float process);
    public abstract void onComplete(@DownloadTask.DownloadResultRule int result);
}
