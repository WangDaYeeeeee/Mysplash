package com.wangdaye.mysplash.muzei;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Sunng87 on 17-5-12.
 */

public final class MysplashMuzeiArtSource extends RemoteMuzeiArtSource {

    private static final String SOURCE_NAME = "MysplashArtSource";

    // 30 minutes
    private static final long FAILED_RETRY_INTERVAL = 30 * 60 * 1000;

    // 24 hours
    private static final long UPDATE_INTERVAL = 24 * 60 * 60 * 1000;

    private PhotoService photoService = PhotoService.getService();

    /**
     * Remember to call this constructor from an empty constructor!
     *
     */
    public MysplashMuzeiArtSource() {
        super(SOURCE_NAME);
    }

    private void exportPhoto(Photo photo) {
        if (photo != null) {
            Log.d(SOURCE_NAME, "Photo:" + photo.id);

            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra(PhotoActivity.KEY_PHOTO_ACTIVITY_ID, photo.id);

            Artwork art = new Artwork.Builder()
                    .title(photo.user.name)
                    .byline(photo.created_at)
                    .imageUri(Uri.parse(photo.getRegularUrl()))
                    .token(photo.id)
                    .viewIntent(intent)
                    .build();

            publishArtwork(art);
            setUserCommands(BUILTIN_COMMAND_ID_NEXT_ARTWORK);

            // schedule next update
            scheduleUpdate(System.currentTimeMillis() + UPDATE_INTERVAL);
        }
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {
        Artwork art = getCurrentArtwork();
        final String currentToken = art == null ? null : art.getToken();

        photoService.requestCuratePhotos(1, 20, null, new PhotoService.OnRequestPhotosListener(){

            @Override
            public void onRequestPhotosSuccess(Call<List<Photo>> call, Response<List<Photo>> response) {
                if (response.isSuccessful()) {
                    Photo photo ;
                    while(true) {
                        List<Photo> results = response.body();
                        int idx = new Random().nextInt(results.size());
                        photo = results.get(idx);
                        if (!photo.id.equals(currentToken)) {
                            break;
                        }
                    }
                    exportPhoto(photo);
                } else {
                    MysplashMuzeiArtSource.this.scheduleUpdate(System.currentTimeMillis() + FAILED_RETRY_INTERVAL);
                }
            }

            @Override
            public void onRequestPhotosFailed(Call<List<Photo>> call, Throwable t) {
                MysplashMuzeiArtSource.this.scheduleUpdate(System.currentTimeMillis() + FAILED_RETRY_INTERVAL);
            }
        });
    }
}
