package com.wangdaye.mysplash.common.di.module;

import com.wangdaye.mysplash.common.ui.dialog.ConfirmExitWithoutSaveDialog;
import com.wangdaye.mysplash.common.ui.dialog.DeleteCollectionPhotoDialog;
import com.wangdaye.mysplash.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.mysplash.common.ui.dialog.DownloadTypeDialog;
import com.wangdaye.mysplash.common.ui.dialog.PathDialog;
import com.wangdaye.mysplash.common.ui.dialog.ProfileDialog;
import com.wangdaye.mysplash.common.ui.dialog.RequestBrowsableDataDialog;
import com.wangdaye.mysplash.common.ui.dialog.RetryDialog;
import com.wangdaye.mysplash.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash.common.ui.dialog.TimePickerDialog;
import com.wangdaye.mysplash.common.ui.dialog.TotalDialog;
import com.wangdaye.mysplash.common.ui.dialog.UpdateCollectionDialog;
import com.wangdaye.mysplash.common.ui.dialog.WallpaperWhereDialog;
import com.wangdaye.mysplash.main.collection.ui.CollectionFragment;
import com.wangdaye.mysplash.main.following.ui.FollowingFeedFragment;
import com.wangdaye.mysplash.main.home.ui.HomeFragment;
import com.wangdaye.mysplash.main.multiFilter.ui.MultiFilterFragment;
import com.wangdaye.mysplash.main.selected.ui.SelectedFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentModule {

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract HomeFragment contributeHomeFragmentInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract CollectionFragment contributeCollectionFragmentInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract FollowingFeedFragment contributeFollowingFeedFragmentInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract MultiFilterFragment contributeMultiFilterFragmentInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract SelectedFragment contributeSelectedFragmentInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract ConfirmExitWithoutSaveDialog contributeConfirmExitWithoutSaveDialogInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract DeleteCollectionPhotoDialog contributeDeleteCollectionPhotoDialogInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract DownloadRepeatDialog contributeDownloadRepeatDialogInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract DownloadTypeDialog contributeDownloadTypeDialogInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract PathDialog contributePathDialogInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract ProfileDialog contributeProfileDialogInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract RequestBrowsableDataDialog contributeRequestBrowsableDataDialogInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract SelectCollectionDialog contributeSelectCollectionDialogInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract TimePickerDialog contributeTimePickerDialogInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract TotalDialog contributeTotalDialogInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract UpdateCollectionDialog contributeUpdateCollectionDialogInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract WallpaperWhereDialog contributeWallpaperWhereDialogInjector();

    @ContributesAndroidInjector(modules = {
            RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
    abstract RetryDialog contributeRetryDialogInjector();
}
