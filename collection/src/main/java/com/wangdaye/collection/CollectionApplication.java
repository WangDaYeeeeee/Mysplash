package com.wangdaye.collection;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.wangdaye.collection.base.RoutingHelper;
import com.wangdaye.common.base.application.MultiModulesApplication;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.component.module.CollectionModule;

public class CollectionApplication extends MultiModulesApplication {

    private class CollectionModuleIMP implements CollectionModule {

        @Override
        public void startCollectionActivity(Activity a,
                                            View avatar, View background, Collection c) {
            RoutingHelper.startCollectionActivity(a, avatar, background, c);
        }

        @Override
        public void startCollectionActivity(Activity a, Collection c) {
            RoutingHelper.startCollectionActivity(a, c);
        }

        @Override
        public void startCollectionActivity(Activity a, String collectionId) {
            RoutingHelper.startCollectionActivity(a, collectionId);
        }
    }

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setCollectionModule(new CollectionModuleIMP());
    }
}
