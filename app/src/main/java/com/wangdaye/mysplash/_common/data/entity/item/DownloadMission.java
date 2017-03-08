package com.wangdaye.mysplash._common.data.entity.item;

import com.wangdaye.mysplash._common.data.entity.table.DownloadMissionEntity;

/**
 * Download mission.
 * */

public class DownloadMission {
    // data
    public DownloadMissionEntity entity;
    public float process;

    /** <br> life cycle. */

    public DownloadMission(DownloadMissionEntity entity) {
        this.entity = entity;
        this.process = 0;
    }

    public DownloadMission(DownloadMissionEntity entity, float process) {
        this.entity = entity;
        this.process = process;
    }
}
