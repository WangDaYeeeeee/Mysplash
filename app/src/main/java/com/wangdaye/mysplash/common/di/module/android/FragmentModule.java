package com.wangdaye.mysplash.common.di.module.android;

import com.wangdaye.mysplash.common.di.module.NetworkServiceModule;
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

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract HomeFragment contributeHomeFragmentInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract CollectionFragment contributeCollectionFragmentInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract FollowingFeedFragment contributeFollowingFeedFragmentInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract MultiFilterFragment contributeMultiFilterFragmentInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract SelectedFragment contributeSelectedFragmentInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract ConfirmExitWithoutSaveDialog contributeConfirmExitWithoutSaveDialogInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract DeleteCollectionPhotoDialog contributeDeleteCollectionPhotoDialogInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract DownloadRepeatDialog contributeDownloadRepeatDialogInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract DownloadTypeDialog contributeDownloadTypeDialogInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract PathDialog contributePathDialogInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract ProfileDialog contributeProfileDialogInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract RequestBrowsableDataDialog contributeRequestBrowsableDataDialogInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract SelectCollectionDialog contributeSelectCollectionDialogInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract TimePickerDialog contributeTimePickerDialogInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract TotalDialog contributeTotalDialogInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract UpdateCollectionDialog contributeUpdateCollectionDialogInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract WallpaperWhereDialog contributeWallpaperWhereDialogInjector();

    @ContributesAndroidInjector(modules = NetworkServiceModule.class)
    abstract RetryDialog contributeRetryDialogInjector();
}
