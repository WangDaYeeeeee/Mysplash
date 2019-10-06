package com.wangdaye.common.di.component;

import com.wangdaye.common.di.module.NetworkServiceModule;
import com.wangdaye.common.ui.dialog.DeleteCollectionPhotoDialog;
import com.wangdaye.common.ui.dialog.ProfileDialog;
import com.wangdaye.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.common.utils.manager.UserNotificationManager;

import dagger.Component;

@Component(modules = NetworkServiceModule.class)
public interface NetworkServiceComponent {

    void inject(UserNotificationManager manager);
    void inject(AuthManager manager);

    void inject(DeleteCollectionPhotoDialog dialog);
    void inject(SelectCollectionDialog dialog);
    void inject(ProfileDialog dialog);
}
