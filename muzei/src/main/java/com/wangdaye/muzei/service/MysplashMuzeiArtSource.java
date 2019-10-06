package com.wangdaye.muzei.service;

import android.net.Uri;
import androidx.annotation.NonNull;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiArtSource;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import com.google.android.apps.muzei.api.UserCommand;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.muzei.di.DaggerNetworkServiceComponent;
import com.wangdaye.muzei.base.MuzeiOptionManager;
import com.wangdaye.muzei.base.MuzeiUpdateHelper;
import com.wangdaye.common.network.service.PhotoService;
import com.wangdaye.common.utils.helper.RoutingHelper;
import com.wangdaye.muzei.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Muzei source photoService.
 *
 * This photoService is used to provide source of wallpapers for Muzei.
 *
 * */

public class MysplashMuzeiArtSource extends RemoteMuzeiArtSource
        implements MuzeiUpdateHelper.OnUpdateCallback {

    @Inject PhotoService service;

    private static final String SOURCE_NAME = "Mysplash";

    private static final long HOUR = 60 * 60 * 1000;
    private static final long FIFTEEN_MINUTES = 15 * 60 * 1000;

    public MysplashMuzeiArtSource() {
        super(SOURCE_NAME);
        DaggerNetworkServiceComponent.create().inject(this);
    }

    @Override
    protected void onTryUpdate(int reason) {
        if (MuzeiOptionManager.getInstance(this).isUpdateOnlyInWifi()
                && !MuzeiUpdateHelper.isWifi(this)) {
            return;
        }
        MuzeiUpdateHelper.update(this, service, this);
    }

    private void publishPhoto(@NonNull Photo photo) {
        publishArtwork(
                new Artwork.Builder()
                        .title(getString(R.string.by) + " " + photo.user.name)
                        .byline(getString(R.string.on) + " " + photo.created_at.split("T")[0])
                        .imageUri(Uri.parse(photo.getDownloadUrl()))
                        .token(photo.id)
                        .viewIntent(
                                RoutingHelper.getWebActivityIntent(
                                        "https://unsplash.com/photos/" + photo.id)
                        ).build()
        );

        List<UserCommand> commands = new ArrayList<>();
        commands.add(new UserCommand(MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK));
        setUserCommands(commands);
    }

    // interface.

    @Override
    public void onUpdateSucceed(@NonNull List<Photo> photoList) {
        Artwork art = getCurrentArtwork();
        String lastPhotoId = art == null ? null : art.getToken();
        for (Photo photo : photoList) {
            if (!photo.id.equals(lastPhotoId)) {
                publishPhoto(photo);
                scheduleUpdate(System.currentTimeMillis()
                        + MuzeiOptionManager.getInstance(this).getUpdateInterval() * HOUR);
                return;
            }
        }
        onUpdateFailed();
    }

    @Override
    public void onUpdateFailed() {
        scheduleUpdate(System.currentTimeMillis() + FIFTEEN_MINUTES);
    }
}