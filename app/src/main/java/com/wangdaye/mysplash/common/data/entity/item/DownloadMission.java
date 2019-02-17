package com.wangdaye.mysplash.common.data.entity.item;

import androidx.annotation.FloatRange;

import com.wangdaye.mysplash.common.data.entity.table.DownloadMissionEntity;

/**
 * Download mission.
 *
 * The item model for {@link com.wangdaye.mysplash.common.ui.adapter.DownloadAdapter}.
 *
 * */

public class DownloadMission {

    public DownloadMissionEntity entity;
    @ProcessRangeRule
    public float process;

    @FloatRange(from = 0.0, to = 100.0)
    public @interface ProcessRangeRule {}

    public DownloadMission(DownloadMissionEntity entity) {
        this.entity = entity;
        this.process = 0;
    }

    public DownloadMission(DownloadMissionEntity entity,
                           @ProcessRangeRule float process) {
        this.entity = entity;
        this.process = Math.max(0, Math.min(process, 100));
    }
}
