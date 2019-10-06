package com.wangdaye.db.entitiy;

import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.base.MuzeiWallpaperSource;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.util.List;

/**
 * Wallpaper source.
 *
 * The SQLite database table entity class for wallpaper source.
 *
 * */

@Entity
public class WallpaperSource {
    
    @Id
    public long collectionId;
    
    public String title;
    public boolean curated;
    public String coverUrl;

    public WallpaperSource(MuzeiWallpaperSource source) {
        this.collectionId = source.collectionId;
        this.title = source.title;
        this.curated = source.curated;
        this.coverUrl = source.coverUrl;
    }
    
    @Generated(hash = 1834667783)
    public WallpaperSource(long collectionId, String title, boolean curated,
            String coverUrl) {
        this.collectionId = collectionId;
        this.title = title;
        this.curated = curated;
        this.coverUrl = coverUrl;
    }

    @Generated(hash = 2085607522)
    public WallpaperSource() {
    }

    public MuzeiWallpaperSource toMuzeiWallpaperSource() {
        return new MuzeiWallpaperSource(collectionId, title, curated, coverUrl);
    }

    // insert.

    public static void insertWallpaperSource(SQLiteDatabase database,
                                            @NonNull WallpaperSource source) {
        deleteWallpaperSource(database, source.collectionId);
        new DaoMaster(database)
                .newSession()
                .getWallpaperSourceDao()
                .insert(source);
    }

    public static void insertWallpaperSource(SQLiteDatabase database,
                                             @NonNull List<WallpaperSource> list) {
        clearWallpaperSource(database);
        new DaoMaster(database)
                .newSession()
                .getWallpaperSourceDao()
                .insertInTx(list);
    }

    // delete.

    public static void deleteWallpaperSource(SQLiteDatabase database, long collectionId) {
        new DaoMaster(database)
                .newSession()
                .getWallpaperSourceDao()
                .deleteByKey(collectionId);
    }

    public static void clearWallpaperSource(SQLiteDatabase database) {
        new DaoMaster(database)
                .newSession()
                .getWallpaperSourceDao()
                .deleteAll();
    }

    // update.

    public static void updateWallpaperSource(SQLiteDatabase database,
                                            @NonNull WallpaperSource source) {
        new DaoMaster(database)
                .newSession()
                .getWallpaperSourceDao()
                .update(source);
    }

    // search.

    public static List<WallpaperSource> readWallpaperSourceList(SQLiteDatabase database) {
        List<WallpaperSource> list = new DaoMaster(database)
                .newSession()
                .getWallpaperSourceDao()
                .queryBuilder()
                .list();
        if (list.size() == 0) {
            list.add(new WallpaperSource(MuzeiWallpaperSource.unsplashSource()));
            list.add(new WallpaperSource(MuzeiWallpaperSource.mysplashSource()));
            insertWallpaperSource(database, list);
        }
        return list;
    }

    @Nullable
    public static WallpaperSource searchWallpaperSource(SQLiteDatabase database, long collectionId) {
        List<WallpaperSource> entityList = new DaoMaster(database)
                .newSession()
                .getWallpaperSourceDao()
                .queryBuilder()
                .where(WallpaperSourceDao.Properties.CollectionId.eq(collectionId))
                .list();
        if (entityList != null && entityList.size() > 0) {
            return entityList.get(0);
        } else {
            return null;
        }
    }
    
    public long getCollectionId() {
        return this.collectionId;
    }

    public void setCollectionId(long collectionId) {
        this.collectionId = collectionId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getCurated() {
        return this.curated;
    }

    public void setCurated(boolean curated) {
        this.curated = curated;
    }

    public String getCoverUrl() {
        return this.coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
