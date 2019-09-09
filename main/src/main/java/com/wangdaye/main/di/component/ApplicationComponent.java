package com.wangdaye.main.di.component;

import com.wangdaye.common.di.module.NetworkServiceModule;
import com.wangdaye.main.MainActivity;
import com.wangdaye.main.collection.ui.CollectionFragment;
import com.wangdaye.main.di.module.ViewModelModule;
import com.wangdaye.main.following.ui.FollowingFeedFragment;
import com.wangdaye.main.home.ui.HomeFragment;
import com.wangdaye.main.multiFilter.ui.MultiFilterFragment;
import com.wangdaye.main.selected.ui.SelectedFragment;

import dagger.Component;

@Component(modules = {NetworkServiceModule.class, ViewModelModule.class})
public interface ApplicationComponent {

    void inject(MainActivity activity);

    void inject(HomeFragment fragment);
    void inject(CollectionFragment fragment);
    void inject(FollowingFeedFragment fragment);
    void inject(MultiFilterFragment fragment);
    void inject(SelectedFragment fragment);
}
