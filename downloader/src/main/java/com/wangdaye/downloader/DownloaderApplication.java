package com.wangdaye.downloader;

import android.content.Context;

import com.wangdaye.common.base.application.MultiModulesApplication;
import com.wangdaye.component.ComponentFactory;

public class DownloaderApplication extends MultiModulesApplication {

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setDownloaderService(
                DownloaderServiceIMP.getInstance(
                        context,
                        ComponentFactory.getSettingsService().getDownloader()
                )
        );
    }
}
