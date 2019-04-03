package com.wangdaye.mysplash.common.di.component;

import com.wangdaye.mysplash.common.di.module.NetworkServiceModule;
import com.wangdaye.mysplash.common.download.DownloadHelper;
import com.wangdaye.mysplash.common.muzei.MysplashMuzeiWorker;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.manager.UserNotificationManager;

import dagger.Component;

@Component(modules = NetworkServiceModule.class)
public interface NetworkServiceComponent {

    void inject(UserNotificationManager manager);

    void inject(DownloadHelper helper);

    void inject(AuthManager manager);

    void inject(MysplashMuzeiWorker worker);
}
