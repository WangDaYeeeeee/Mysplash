package com.wangdaye.mysplash.common.di.module;

import com.wangdaye.mysplash.about.ui.AboutActivity;
import com.wangdaye.mysplash.collection.ui.CollectionActivity;
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

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract MainActivity contributeMainActivityInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract CollectionActivity contributeCollectionActivityInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract MeActivity contributeMeActivityInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract MyFollowActivity contributeMyFollowActivityInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract PhotoActivity3 contributePhotoActivity3Injector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract SearchActivity contributeSearchActivityInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract UserActivity contributeUserActivityInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract PreviewActivity contributePreviewActivityInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract LoginActivity contributeLoginActivityInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract UpdateMeActivity contributeUpdateMeActivityInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract DownloadManageActivity contributeDownloadManageActivityInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract SettingsActivity contributeSettingsActivityInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract AboutActivity contributeAboutActivityInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract IntroduceActivity contributeIntroduceActivityInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract CustomApiActivity contributeCustomApiActivityInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract SetWallpaperActivity contributeSetWallpaperActivityInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract MuzeiSettingsActivity contributeMuzeiSettingsActivityInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract MuzeiCollectionSourceConfigActivity contributeMuzeiCollectionSourceConfigActivityInjector();
}
