package com.wangdaye.mysplash._common.data.entity.database;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Download mission entity.
 * */

@Entity
public class DownloadMissionEntity {
    @Id
    public long missionId;

    public String photoId;
    public String photoUri;

    public String downloadUrl;
    public int downloadType;

    public DownloadMissionEntity(Photo p, int type) {
        this.photoId = p.id;
        this.photoUri = p.urls.regular;
        if (Mysplash.getInstance()
                .getDownloadScale()
                .equals("compact")) {
            this.downloadUrl = p.urls.full;
        } else {
            this.downloadUrl = p.urls.raw;
        }
        this.downloadType = type;
    }

    @Generated(hash = 984514955)
    public DownloadMissionEntity(long missionId, String photoId, String photoUri,
            String downloadUrl, int downloadType) {
        this.missionId = missionId;
        this.photoId = photoId;
        this.photoUri = photoUri;
        this.downloadUrl = downloadUrl;
        this.downloadType = downloadType;
    }

    @Generated(hash = 1239001066)
    public DownloadMissionEntity() {
    }

    public long getMissionId() {
        return this.missionId;
    }

    public void setMissionId(long missionId) {
        this.missionId = missionId;
    }

    public String getPhotoId() {
        return this.photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getPhotoUri() {
        return this.photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getDownloadType() {
        return this.downloadType;
    }

    public void setDownloadType(int downloadType) {
        this.downloadType = downloadType;
    }
}
