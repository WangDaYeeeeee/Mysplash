package com.wangdaye.base;

import com.wangdaye.base.unsplash.Collection;

public class MuzeiWallpaperSource {

    public long collectionId;
    public String title;
    public boolean curated;
    public String coverUrl;

    public MuzeiWallpaperSource(Collection collection) {
        this(
                collection.id,
                collection.title,
                collection.curated,
                collection.cover_photo != null ? collection.cover_photo.urls.regular : null
        );
    }

    public MuzeiWallpaperSource(long collectionId, String title, boolean curated, String coverUrl) {
        this.collectionId = collectionId;
        this.title = title;
        this.curated = curated;
        this.coverUrl = coverUrl;
    }

    public static MuzeiWallpaperSource mysplashSource() {
        return new MuzeiWallpaperSource(
                864380,
                "Mysplash Wallpapers",
                false,
                "https://images.unsplash.com/photo-1451847487946-99830706c22d"
                        + "?ixlib=rb-0.3.5"
                        + "&q=80"
                        + "&fm=jpg"
                        + "&crop=entropy"
                        + "&cs=tinysrgb"
                        + "&w=1080"
                        + "&fit=max"
                        + "&s=334b584fa099b256b9e755cd3b75fd45"
        );
    }

    public static MuzeiWallpaperSource unsplashSource() {
        return new MuzeiWallpaperSource(
                1065976,
                "Unsplash Wallpapers",
                false,
                "https://images.unsplash.com/photo-1544979407-1204ff29f490"
                        + "?ixlib=rb-0.3.5"
                        + "&q=80"
                        + "&fm=jpg"
                        + "&crop=entropy"
                        + "&cs=tinysrgb"
                        + "&w=1080"
                        + "&fit=max"
                        + "&s=334b584fa099b256b9e755cd3b75fd45"
        );
    }
}
