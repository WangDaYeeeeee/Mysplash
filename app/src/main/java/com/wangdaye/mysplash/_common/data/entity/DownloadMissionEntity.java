package com.wangdaye.mysplash._common.data.entity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Download mission entity.
 * */

@Entity
public class DownloadMissionEntity {
    @Id
    public long missionId;

    @Unique
    public String photoId;
    public String photoUri;

    public String downloadUrl;
    public int downloadType;
    public int downloadStatus;

    public DownloadMissionEntity(Context c, Photo p, int type) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        String scaleType = sharedPreferences.getString(
                Mysplash.getInstance().getString(R.string.key_download_scale),
                Mysplash.getInstance().getResources().getStringArray(R.array.download_type_values)[0]);

        this.photoId = p.id;
        this.photoUri = p.urls.regular;
        if (scaleType.equals(Mysplash.getInstance().getResources().getStringArray(R.array.download_type_values)[0])) {
            this.downloadUrl = p.urls.full;
        } else {
            this.downloadUrl = p.urls.raw;
        }
        this.downloadType = type;
        this.downloadStatus = DownloadManager.STATUS_PENDING;
    }

    @Generated(hash = 1058775346)
    public DownloadMissionEntity(long missionId, String photoId, String photoUri, String downloadUrl, int downloadType,
            int downloadStatus) {
        this.missionId = missionId;
        this.photoId = photoId;
        this.photoUri = photoUri;
        this.downloadUrl = downloadUrl;
        this.downloadType = downloadType;
        this.downloadStatus = downloadStatus;
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

    public int getDownloadStatus() {
        return this.downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }
}
