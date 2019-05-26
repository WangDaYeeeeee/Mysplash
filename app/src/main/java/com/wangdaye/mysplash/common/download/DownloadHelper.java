package com.wangdaye.mysplash.common.download;

import android.app.DownloadManager;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.mysplash.common.bus.event.DownloadEvent;
import com.wangdaye.mysplash.common.di.component.DaggerNetworkServiceComponent;
import com.wangdaye.mysplash.common.download.imp.AndroidDownloaderService;
import com.wangdaye.mysplash.common.download.imp.AbstractDownloaderService;
import com.wangdaye.mysplash.common.download.imp.FileDownloaderService;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.db.DownloadMissionEntity;
import com.wangdaye.mysplash.common.network.service.PhotoService;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.common.db.DatabaseHelper;
import com.wangdaye.mysplash.common.bus.MessageBus;
import com.wangdaye.mysplash.common.utils.manager.SettingsOptionManager;

import java.util.List;

import javax.inject.Inject;

/**
 * Download helper.
 *
 * A helper class that makes operations of {@link DownloadManager} easier.
 *
 * */

public class DownloadHelper {

    private static DownloadHelper instance;

    public static DownloadHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DownloadHelper.class) {
                if (instance == null) {
                    instance = new DownloadHelper(context);
                }
            }
        }
        return instance;
    }

    private AbstractDownloaderService downloaderService;
    @Inject PhotoService photoService;

    private DownloadHelper(Context context) {
        bindDownloader(context, SettingsOptionManager.getInstance(context).getDownloader());
        DaggerNetworkServiceComponent.create().inject(this);
    }

    private void bindDownloader(Context context, String downloader) {
        if ("mysplash".equals(downloader)) {
            this.downloaderService = FileDownloaderService.getInstance(context);
        } else {
            this.downloaderService = AndroidDownloaderService.getInstance(context);
        }
    }

    public boolean switchDownloader(Context context, String downloader) {
        if (DatabaseHelper.getInstance(context)
                .readDownloadEntityCount(DownloadMissionEntity.RESULT_DOWNLOADING) > 0) {
            return false;
        }
        bindDownloader(context, downloader);
        return true;
    }

    public void addMission(Context c, Photo p, @DownloadMissionEntity.DownloadTypeRule int type) {
        if (FileUtils.createDownloadPath(c)) {
            long missionId = downloaderService.addMission(
                    c, new DownloadMissionEntity(c, p, type), true
            );
            photoService.downloadPhoto(p.id);

            MessageBus.getInstance().post(
                    new DownloadEvent(
                            missionId,
                            p.id,
                            type,
                            DownloadMissionEntity.RESULT_DOWNLOADING
                    )
            );
        }
    }

    public void addMission(Context c, Collection collection) {
        if (FileUtils.createDownloadPath(c)) {
            long missionId = downloaderService.addMission(
                    c, new DownloadMissionEntity(collection), true
            );

            MessageBus.getInstance().post(
                    new DownloadEvent(
                            missionId,
                            String.valueOf(collection.id),
                            DownloadMissionEntity.COLLECTION_TYPE,
                            DownloadMissionEntity.RESULT_DOWNLOADING
                    )
            );
        }
    }

    @Nullable
    public DownloadMission restartMission(Context c, long missionId) {
        DownloadMissionEntity entity = DatabaseHelper.getInstance(c).readDownloadEntity(missionId);
        if (entity != null) {
            DownloadMission mission = new DownloadMission(entity);
            mission.entity.missionId = downloaderService.restartMission(c, entity);
            mission.entity.result = DownloadMissionEntity.RESULT_DOWNLOADING;
            mission.process = 0;

            if (mission.entity.missionId != -1) {
                MessageBus.getInstance().post(
                        new DownloadEvent(
                                mission.entity.missionId,
                                mission.entity.title,
                                mission.entity.downloadType,
                                DownloadMissionEntity.RESULT_DOWNLOADING
                        )
                );
                return mission;
            }
        }
        return null;
    }

    public void completeMission(Context c, long missionId) {
        DownloadMissionEntity entity = DatabaseHelper.getInstance(c).readDownloadEntity(missionId);
        if (entity != null) {
            downloaderService.completeMission(c, entity);

            MessageBus.getInstance().post(
                    new DownloadEvent(
                            entity.missionId,
                            entity.title,
                            entity.downloadType,
                            entity.result
                    )
            );
        }
    }

    public void removeMission(Context c, @NonNull DownloadMissionEntity entity) {
        downloaderService.removeMission(c, entity, true);

        if (entity.result == DownloadMissionEntity.RESULT_DOWNLOADING) {
            MessageBus.getInstance().post(
                    new DownloadEvent(
                            entity.missionId,
                            entity.title,
                            entity.downloadType,
                            DownloadMissionEntity.RESULT_FAILED
                    )
            );
        }
    }

    public void clearMission(Context c, @Nullable List<DownloadMissionEntity> entityList) {
        if (entityList != null) {
            downloaderService.clearMission(c, entityList);

            for (DownloadMissionEntity e : entityList) {
                if (e.result == DownloadMissionEntity.RESULT_DOWNLOADING) {
                    MessageBus.getInstance().post(
                            new DownloadEvent(
                                    e.missionId,
                                    e.title,
                                    e.downloadType,
                                    DownloadMissionEntity.RESULT_FAILED
                            )
                    );
                }
            }
        }
    }

    public List<DownloadMissionEntity> readDownloadEntityList(Context c,
                                                              @DownloadMissionEntity.DownloadResultRule int result) {
        return downloaderService.readDownloadEntityList(c, result);
    }

    public int readDownloadingEntityCount(Context c, String title) {
        return downloaderService.readDownloadingEntityCount(c, title);
    }

    public void updateMissionResult(Context c,
                                    @NonNull DownloadMissionEntity entity,
                                    @DownloadMissionEntity.DownloadResultRule int result) {
        downloaderService.updateMissionResult(c, entity, result);
    }

    public DownloadMission getDownloadMission(Context context, @NonNull DownloadMissionEntity entity) {
        return new DownloadMission(
                entity,
                downloaderService.getMissionProcess(context, entity)
        );
    }

    public void addOnDownloadListener(@NonNull AbstractDownloaderService.OnDownloadListener l) {
        downloaderService.addOnDownloadListener(l);
    }

    public void addOnDownloadListener(@NonNull List<AbstractDownloaderService.OnDownloadListener> list) {
        downloaderService.addOnDownloadListener(list);
    }

    public void removeOnDownloadListener(@NonNull AbstractDownloaderService.OnDownloadListener l) {
        downloaderService.removeOnDownloadListener(l);
    }

    public void removeOnDownloadListener(@NonNull List<AbstractDownloaderService.OnDownloadListener> list) {
        downloaderService.removeOnDownloadListener(list);
    }

    @Nullable
    public DownloadMissionEntity readDownloadMissionEntity(Context context, String title) {
        return downloaderService.readDownloadMissionEntity(context, title);
    }
}

