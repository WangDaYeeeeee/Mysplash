package com.wangdaye.mysplash.common.data.entity.table;

import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;

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

    public WallpaperSource(Collection collection) {
        this.collectionId = collection.id;
        this.title = collection.title;
        this.curated = collection.curated;
        this.coverUrl = collection.cover_photo != null ? collection.cover_photo.urls.regular : null;
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

    public static WallpaperSource mysplashSource() {
        WallpaperSource source = new WallpaperSource();
        source.collectionId = 864380;
        source.title = "Mysplash Wallpapers";
        source.curated = false;
        source.coverUrl = "https://images.unsplash.com/photo-1451847487946-99830706c22d?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=334b584fa099b256b9e755cd3b75fd45";
        return source;
    }

    public static WallpaperSource unsplashSource() {
        WallpaperSource source = new WallpaperSource();
        source.collectionId = 1065976;
        source.title = "Unsplash Wallpaper";
        source.curated = false;
        source.coverUrl = "https://images.unsplash.com/photo-1544979407-1204ff29f490?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=334b584fa099b256b9e755cd3b75fd45";
        return source;
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
            list.add(unsplashSource());
            list.add(mysplashSource());
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
