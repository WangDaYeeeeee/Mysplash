package com.wangdaye.mysplash.common.di.component;

import com.wangdaye.mysplash.common.di.module.NetworkModule;
import com.wangdaye.mysplash.common.download.DownloadHelper;
import com.wangdaye.mysplash.common.network.service.PhotoService;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.manager.UserNotificationManager;

import dagger.Component;

@Component(modules = NetworkModule.class)
public interface ServiceComponent {

    void inject(UserNotificationManager manager);

    void inject(DownloadHelper helper);

    void inject(AuthManager manager);

    PhotoService getPhotoService();
}
