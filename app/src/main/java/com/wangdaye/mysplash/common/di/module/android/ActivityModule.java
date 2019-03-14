package com.wangdaye.mysplash.common.di.module.android;

import com.wangdaye.mysplash.about.ui.AboutActivity;
import com.wangdaye.mysplash.collection.ui.CollectionActivity;
import com.wangdaye.mysplash.common.di.module.NetworkModule;
import com.wangdaye.mysplash.common.ui.activity.CustomApiActivity;
import com.wangdaye.mysplash.common.ui.activity.DownloadManageActivity;
import com.wangdaye.mysplash.common.ui.activity.IntroduceActivity;
import com.wangdaye.mysplash.common.ui.activity.LoginActivity;
import com.wangdaye.mysplash.common.ui.activity.PreviewActivity;
import com.wangdaye.mysplash.common.ui.activity.SetWallpaperActivity;
import com.wangdaye.mysplash.common.ui.activity.SettingsActivity;
import com.wangdaye.mysplash.common.ui.activity.UpdateMeActivity;
import com.wangdaye.mysplash.common.ui.activity.muzei.MuzeiCollectionSourceConfigActivity;
import com.wangdaye.mysplash.common.ui.activity.muzei.MuzeiSettingsActivity;
import com.wangdaye.mysplash.main.MainActivity;
import com.wangdaye.mysplash.me.ui.MeActivity;
import com.wangdaye.mysplash.me.ui.MyFollowActivity;
import com.wangdaye.mysplash.photo3.ui.PhotoActivity3;
import com.wangdaye.mysplash.search.ui.SearchActivity;
import com.wangdaye.mysplash.user.ui.UserActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract MainActivity contributeMainActivityInjector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract CollectionActivity contributeCollectionActivityInjector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract MeActivity contributeMeActivityInjector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract MyFollowActivity contributeMyFollowActivityInjector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract PhotoActivity3 contributePhotoActivity3Injector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract SearchActivity contributeSearchActivityInjector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract UserActivity contributeUserActivityInjector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract PreviewActivity contributePreviewActivityInjector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract LoginActivity contributeLoginActivityInjector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract UpdateMeActivity contributeUpdateMeActivityInjector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract DownloadManageActivity contributeDownloadManageActivityInjector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract SettingsActivity contributeSettingsActivityInjector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract AboutActivity contributeAboutActivityInjector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract IntroduceActivity contributeIntroduceActivityInjector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract CustomApiActivity contributeCustomApiActivityInjector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract SetWallpaperActivity contributeSetWallpaperActivityInjector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract MuzeiSettingsActivity contributeMuzeiSettingsActivityInjector();

    @ContributesAndroidInjector(modules = NetworkModule.class)
    abstract MuzeiCollectionSourceConfigActivity contributeMuzeiCollectionSourceConfigActivityInjector();
}
