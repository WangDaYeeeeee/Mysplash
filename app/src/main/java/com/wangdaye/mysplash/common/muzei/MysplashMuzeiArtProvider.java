package com.wangdaye.mysplash.common.muzei;

import com.google.android.apps.muzei.api.provider.MuzeiArtProvider;

public class MysplashMuzeiArtProvider extends MuzeiArtProvider {

    @Override
    protected void onLoadRequested(boolean initial) {
        MysplashMuzeiWorker.enqueue();
    }
}