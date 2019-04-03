package com.wangdaye.mysplash.common.di.module.android;

import com.wangdaye.mysplash.about.ui.AboutActivity;
import com.wangdaye.mysplash.collection.ui.CollectionActivity;
import com.wangdaye.mysplash.common.di.module.NetworkServiceModule;
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
import com.wangdaye.mysplash.me.ui.activity.MeActivity;
import com.wangdaye.mysplash.me.ui.activity.MyFollowActivity;
import com.wangdaye.mysplash.photo3.ui.PhotoActivity3;
import com.wangdaye.mysplash.search.ui.SearchActivity;
import com.wangdaye.mysplash.user.ui.UserActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract MainActivity contributeMainActivityInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract CollectionActivity contributeCollectionActivityInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract MeActivity contributeMeActivityInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract MyFollowActivity contributeMyFollowActivityInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract PhotoActivity3 contributePhotoActivity3Injector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract SearchActivity contributeSearchActivityInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract UserActivity contributeUserActivityInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract PreviewActivity contributePreviewActivityInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract LoginActivity contributeLoginActivityInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract UpdateMeActivity contributeUpdateMeActivityInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract DownloadManageActivity contributeDownloadManageActivityInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract SettingsActivity contributeSettingsActivityInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract AboutActivity contributeAboutActivityInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract IntroduceActivity contributeIntroduceActivityInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract CustomApiActivity contributeCustomApiActivityInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract SetWallpaperActivity contributeSetWallpaperActivityInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract MuzeiSettingsActivity contributeMuzeiSettingsActivityInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract MuzeiCollectionSourceConfigActivity contributeMuzeiCollectionSourceConfigActivityInjector();
}
