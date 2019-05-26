package com.wangdaye.mysplash.common.bus.event;

import com.wangdaye.mysplash.common.db.DownloadMissionEntity;

public class DownloadEvent {

    public long id;
    public String title;
    @DownloadMissionEntity.DownloadTypeRule public int type;
    @DownloadMissionEntity.DownloadResultRule public int result;

    public DownloadEvent(long id, String title,
                         @DownloadMissionEntity.DownloadTypeRule int type,
                         @DownloadMissionEntity.DownloadResultRule int result) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.result = result;
    }
}
