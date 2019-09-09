package com.wangdaye.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.alibaba.android.arouter.launcher.ARouter;
import com.wangdaye.common.base.application.MultiModulesApplication;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.ui.transition.sharedElement.Recolor;
import com.wangdaye.common.ui.transition.sharedElement.RoundCornerTransition;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.component.module.SearchModule;

public class SearchApplication extends MultiModulesApplication {

    private class SearchModuleIMP implements SearchModule {

        @Override
        public void startSearchActivity(Activity a, View background, @Nullable String query) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Bundle b = new Bundle();
                Recolor.addExtraProperties(background, b);
                RoundCornerTransition.addExtraProperties(background, b);
                MysplashApplication.getInstance().setSharedElementTransitionExtraProperties(b);

                ARouter.getInstance()
                        .build(SearchActivity.SEARCH_ACTIVITY)
                        .withString(SearchActivity.KEY_SEARCH_ACTIVITY_QUERY, query)
                        .withBoolean(SearchActivity.KEY_EXECUTE_TRANSITION, true)
                        .withBundle(a.getString(R.string.transition_search_background), b)
                        .withOptionsCompat(
                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        a,
                                        Pair.create(background, a.getString(R.string.transition_search_background))
                                )
                        ).navigation(a);
            } else {
                startSearchActivity(a, query);
            }
        }

        @Override
        public void startSearchActivity(Activity a, @Nullable String query) {
            ARouter.getInstance()
                    .build(SearchActivity.SEARCH_ACTIVITY)
                    .withString(SearchActivity.KEY_SEARCH_ACTIVITY_QUERY, query)
                    .withTransition(R.anim.activity_slide_in, R.anim.none)
                    .navigation(a);
        }

        @Override
        public Intent getSearchActivityIntentForShortcut() {
            return new Intent(SearchActivity.ACTION_SEARCH_ACTIVITY);
        }
    }

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setSearchModule(new SearchModuleIMP());
    }
}
