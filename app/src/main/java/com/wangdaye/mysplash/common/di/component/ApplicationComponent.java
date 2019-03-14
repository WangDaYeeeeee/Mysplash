package com.wangdaye.mysplash.common.di.component;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.di.module.RxJavaModule;
import com.wangdaye.mysplash.common.di.module.android.ViewModelFactoryModule;
import com.wangdaye.mysplash.common.di.module.android.ActivityModule;
import com.wangdaye.mysplash.common.di.module.android.FragmentModule;
import com.wangdaye.mysplash.common.di.module.NetworkModule;
import com.wangdaye.mysplash.common.download.DownloadHelper;
import com.wangdaye.mysplash.common.muzei.MysplashMuzeiWorker;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.manager.UserNotificationManager;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;

@Component(modules = {
        AndroidInjectionModule.class, AndroidSupportInjectionModule.class,
        ActivityModule.class, FragmentModule.class, ViewModelFactoryModule.class,
        RxJavaModule.class, NetworkModule.class})
public interface ApplicationComponent {

    void inject(Mysplash application);

    void inject(UserNotificationManager manager);

    void inject(DownloadHelper helper);

    void inject(AuthManager manager);

    void inject(MysplashMuzeiWorker worker);
}
