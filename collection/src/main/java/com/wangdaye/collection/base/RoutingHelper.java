package com.wangdaye.collection.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.alibaba.android.arouter.launcher.ARouter;
import com.wangdaye.collection.CollectionActivity;
import com.wangdaye.collection.R;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.common.ui.transition.sharedElement.Recolor;
import com.wangdaye.common.ui.transition.sharedElement.RoundCornerTransition;

public class RoutingHelper {
    public static void startCollectionActivity(Activity a,
                                               View avatar, View background, Collection c) {
        Bundle b = new Bundle();
        ActivityOptionsCompat optionsCompat;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(
                    background,
                    (int) background.getX(), (int) background.getY(),
                    background.getMeasuredWidth(), background.getMeasuredHeight()
            );
        } else {
            Recolor.addExtraProperties(background, b);
            RoundCornerTransition.addExtraProperties(background, b);

            optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    a,
                    Pair.create(avatar, a.getString(R.string.transition_collection_avatar)),
                    Pair.create(background, a.getString(R.string.transition_collection_background))
            );
        }

        ARouter.getInstance()
                .build(CollectionActivity.COLLECTION_ACTIVITY)
                .withParcelable(CollectionActivity.KEY_COLLECTION_ACTIVITY_COLLECTION, c)
                .withBundle(a.getString(R.string.transition_collection_background), b)
                .withOptionsCompat(optionsCompat)
                .navigation(a);
    }

    public static void startCollectionActivity(Activity a, Collection c) {
        ARouter.getInstance()
                .build(CollectionActivity.COLLECTION_ACTIVITY)
                .withParcelable(CollectionActivity.KEY_COLLECTION_ACTIVITY_COLLECTION, c)
                .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a);
    }

    public static void startCollectionActivity(Activity a, String collectionId) {
        ARouter.getInstance()
                .build(CollectionActivity.COLLECTION_ACTIVITY)
                .withString(CollectionActivity.KEY_COLLECTION_ACTIVITY_ID, collectionId)
                .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a);
    }
}
