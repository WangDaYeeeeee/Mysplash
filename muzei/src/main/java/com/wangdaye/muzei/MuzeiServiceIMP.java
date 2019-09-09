package com.wangdaye.muzei;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;

import com.wangdaye.base.MuzeiWallpaperSource;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.component.service.MuzeiService;
import com.wangdaye.muzei.base.MuzeiOptionManager;
import com.wangdaye.muzei.base.RoutingHelper;

import java.util.List;

public class MuzeiServiceIMP implements MuzeiService {

    @Override
    public void startMuzeiSettingsActivity(Activity a) {
        RoutingHelper.startMuzeiSettingsActivity(a);
    }

    @Override
    public void startMuzeiCollectionSourceConfigActivity(Activity a) {
        RoutingHelper.startMuzeiCollectionSourceConfigActivity(a);
    }

    @Override
    public boolean isMuzeiInstalled(Context context) {
        return MuzeiOptionManager.isInstalledMuzei(context);
    }

    @MuzeiSourceRule
    @Override
    public String getSource(Context context) {
        return MuzeiOptionManager.getInstance(context).getSource();
    }

    @Override
    public void setAsMuzeiSource(Context context, Collection collection) {
        ComponentFactory.getDatabaseService()
                .writeMuzeiWallpaperSource(new MuzeiWallpaperSource(collection));

        MuzeiOptionManager.updateCollectionSource(
                context, ComponentFactory.getDatabaseService().readMuzeiWallpaperSourceList());
    }

    @Override
    public void removeFromMuzeiSource(Context context, Collection collection) {
        ComponentFactory.getDatabaseService()
                .deleteMuzeiWallpaperSource(collection.id);

        List<MuzeiWallpaperSource> sourceList = ComponentFactory.getDatabaseService()
                .readMuzeiWallpaperSourceList();
        if (sourceList.size() == 0) {
            sourceList.add(MuzeiWallpaperSource.unsplashSource());
            sourceList.add(MuzeiWallpaperSource.mysplashSource());
        }
        MuzeiOptionManager.updateCollectionSource(context, sourceList);
    }

    @Nullable
    @Override
    public MuzeiWallpaperSource getMuzeiWallpaperSource(Context context, Collection collection) {
        return ComponentFactory.getDatabaseService().readMuzeiWallpaperSource(collection.id);
    }
}
