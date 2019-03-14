package com.wangdaye.mysplash.common.di.module;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class RxJavaModule {

    @Provides
    public CompositeDisposable getCompositeDisposable() {
        return new CompositeDisposable();
    }
}
