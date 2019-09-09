package com.wangdaye.common.bus.event;

import com.wangdaye.base.DownloadTask;

public class DownloadEvent {

    public long id;
    public String title;
    @DownloadTask.DownloadTypeRule public int type;
    @DownloadTask.DownloadResultRule public int result;

    public DownloadEvent(long id, String title,
                         @DownloadTask.DownloadTypeRule int type,
                         @DownloadTask.DownloadResultRule int result) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.result = result;
    }
}
