package com.wangdaye.muzei.provider;

import android.content.Context;
import android.net.Uri;

import com.google.android.apps.muzei.api.provider.Artwork;
import com.google.android.apps.muzei.api.provider.ProviderContract;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.muzei.di.DaggerNetworkServiceComponent;
import com.wangdaye.muzei.base.MuzeiOptionManager;
import com.wangdaye.muzei.base.MuzeiUpdateHelper;
import com.wangdaye.common.network.service.PhotoService;
import com.wangdaye.muzei.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MysplashMuzeiWorker extends Worker
        implements MuzeiUpdateHelper.OnUpdateCallback {

    @Inject PhotoService service;

    public MysplashMuzeiWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        DaggerNetworkServiceComponent.create().inject(this);
    }

    static void enqueue() {
        WorkManager.getInstance().enqueue(
                new OneTimeWorkRequest.Builder(MysplashMuzeiWorker.class).setConstraints(
                        new Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build()
                ).build()
        );
    }

    @NonNull
    @Override
    public Result doWork() {
        if (MuzeiUpdateHelper.update(getApplicationContext(), service, this)) {
            return Result.success();
        } else {
            return Result.failure();
        }
    }

    @Override
    public void onUpdateSucceed(@NonNull List<Photo> photoList) {
        boolean screenSizeImage = MuzeiOptionManager.getInstance(getApplicationContext())
                .isScreenSizeImage();
        Context context = getApplicationContext();

        List<Artwork> artworkList = new ArrayList<>(photoList.size());
        for (Photo p : photoList) {
            artworkList.add(new Artwork.Builder()
                    .title(getApplicationContext().getString(R.string.by) + " " + p.user.name)
                    .byline(getApplicationContext().getString(R.string.on) + " " + p.created_at.split("T")[0])
                    .persistentUri(Uri.parse(
                            screenSizeImage
                                    ? p.getRegularSizeUrl(MuzeiUpdateHelper.getScreenSize(context))
                                    : p.getDownloadUrl()
                    )).token(p.id)
                    .webUri(Uri.parse(p.links.html))
                    .build());
        }
        if (MuzeiOptionManager.getInstance(getApplicationContext()).getCacheMode().equals("keep")) {
            ProviderContract.getProviderClient(getApplicationContext(), MysplashMuzeiArtProvider.class)
                    .addArtwork(artworkList);
        } else {
            ProviderContract.getProviderClient(getApplicationContext(), MysplashMuzeiArtProvider.class)
                    .setArtwork(artworkList);
        }
    }

    @Override
    public void onUpdateFailed() {
        // do nothing.
    }
}