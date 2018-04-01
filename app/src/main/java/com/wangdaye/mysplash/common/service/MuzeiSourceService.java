package com.wangdaye.mysplash.common.service;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import com.google.android.apps.muzei.api.UserCommand;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.table.WallpaperSource;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.service.CollectionService;
import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.common.utils.manager.MuzeiOptionManager;
import com.wangdaye.mysplash.photo2.view.activity.PhotoActivity2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Muzei source photoService.
 *
 * This photoService is used to provide source of wallpapers for Muzei.
 *
 * */

public class MuzeiSourceService extends RemoteMuzeiArtSource {

    private static final String SOURCE_NAME = "Mysplash";

    private static final long UNIT_UPDATE_INTERVAL = 60 * 60 * 1000;
    private static final long RETRY_INTERVAL = 15 * 60 * 1000;

    public MuzeiSourceService() {
        super(SOURCE_NAME);
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {
        MuzeiOptionManager manager = MuzeiOptionManager.getInstance(this);
        if (manager.isUpdateOnlyInWifi() && !isWifi()) {
            return;
        }
        if (System.currentTimeMillis() - manager.getLastUpdateTime()
                < manager.getUpdateInterval() * UNIT_UPDATE_INTERVAL
                && reason != UPDATE_REASON_USER_NEXT) {
            return;
        }

        Artwork art = getCurrentArtwork();
        String currentToken = art == null ? null : art.getToken();

        update(manager, getRandomCollectionId(manager), currentToken);
    }

    private int getRandomCollectionId(MuzeiOptionManager manager) {
        List<WallpaperSource> sourceList = manager.getSourceList();
        return (int) sourceList.get(new Random().nextInt(sourceList.size())).collectionId;
    }

    private void update(MuzeiOptionManager manager, int collectionId, String currentToken) {
        Collection collection = requestCollection(collectionId);

        if (collection != null) {
            if (collection.total_photos > 1) {
                List<Photo> photoList = requestCollectionsPhotos(collection);

                if (photoList != null && photoList.size() > 0) {
                    while (true) {
                        int i = new Random().nextInt(photoList.size());
                        if (TextUtils.isEmpty(currentToken)
                                || !photoList.get(i).id.equals(currentToken)) {
                            // This picture is different from the last one.
                            exportPhoto(photoList.get(i));
                            scheduleUpdate(System.currentTimeMillis() + UNIT_UPDATE_INTERVAL);
                            MuzeiOptionManager.writeUpdateTime(this, manager);
                            return;
                        }
                    }
                }
            } else if (collection.total_photos == 1) {
                List<Photo> photoList = PhotoService.getService()
                        .requestCollectionPhotos(collectionId, 1, 1);
                if (photoList != null && photoList.size() > 0) {
                    exportPhoto(photoList.get(0));
                    scheduleUpdate(System.currentTimeMillis() + UNIT_UPDATE_INTERVAL);
                    MuzeiOptionManager.writeUpdateTime(this, manager);
                    return;
                }
            } else if (collectionId != MuzeiOptionManager.DEFAULT_COLLECTION_ID) {
                update(manager, MuzeiOptionManager.DEFAULT_COLLECTION_ID, currentToken);
            }
        }
        scheduleUpdate(System.currentTimeMillis() + RETRY_INTERVAL);
    }

    private Collection requestCollection(int collectionId) {
        if (collectionId < 1000) {
            return CollectionService.getService()
                    .requestACuratedCollections(String.valueOf(collectionId));
        } else {
            return CollectionService.getService()
                    .requestACollections(String.valueOf(collectionId));
        }
    }

    private List<Photo> requestCollectionsPhotos(Collection collection) {
        if (collection.curated) {
            return PhotoService.getService()
                    .requestCurateCollectionPhotos(
                            collection.id,
                            1 + new Random().nextInt(collection.total_photos / 2), // page.
                            2 /* per_page. */);
        } else {
            return PhotoService.getService()
                    .requestCollectionPhotos(
                            collection.id,
                            1 + new Random().nextInt(collection.total_photos / 2), // page.
                            2 /* per_page. */);
        }
    }

    private void exportPhoto(@Nullable Photo photo) {
        if (photo != null) {
            Intent intent = new Intent(this, PhotoActivity2.class);
            intent.putExtra(PhotoActivity2.KEY_PHOTO_ACTIVITY_2_ID, photo.id);

            publishArtwork(
                    new Artwork.Builder()
                            .title(getString(R.string.by) + " " + photo.user.name)
                            .byline(getString(R.string.on) + " " + photo.created_at.split("T")[0])
                            .imageUri(Uri.parse(photo.getWallpaperSizeUrl(this)))
                            .token(photo.id)
                            .viewIntent(intent)
                            .build());

            List<UserCommand> commands = new ArrayList<>();
            commands.add(new UserCommand(BUILTIN_COMMAND_ID_NEXT_ARTWORK));
            setUserCommands(commands);
        }
    }

    private boolean isWifi() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }
}
