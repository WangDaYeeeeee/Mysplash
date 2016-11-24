package com.wangdaye.mysplash._common.data.entity.item;

import com.wangdaye.mysplash._common.data.entity.database.DownloadMissionEntity;

/**
 * Download mission.
 * */

public class DownloadMission {
    // data
    public DownloadMissionEntity entity;
    public float process;

    public DownloadMission(DownloadMissionEntity entity) {
        this.entity = entity;
        this.process = 0;
    }
}
