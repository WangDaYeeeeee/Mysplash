package com.wangdaye.photo.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.alibaba.android.arouter.launcher.ARouter;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.ui.transition.sharedElement.Recolor;
import com.wangdaye.common.ui.transition.sharedElement.RoundCornerTransition;
import com.wangdaye.photo.R;
import com.wangdaye.photo.activity.PhotoActivity;

public class RoutingHelper {

    public static void startPhotoActivity(Activity a, View image, View background, Photo photo, int currentIndex) {
        Bundle b = new Bundle();
        ActivityOptionsCompat optionsCompat;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(
                    background,
                    (int) background.getX(), (int) background.getY(),
                    background.getMeasuredWidth(), background.getMeasuredHeight()
            );
        } else {
            optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    a,
                    new Pair<>(image, a.getString(R.string.transition_photo_image) + "_" + photo.id),
                    new Pair<>(background, a.getString(R.string.transition_photo_image) + "_" + photo.id)
            );
        }
        ARouter.getInstance()
                .build(PhotoActivity.PHOTO_ACTIVITY)
                .withParcelable(PhotoActivity.KEY_PHOTO_ACTIVITY_PHOTO, photo)
                .withInt(PhotoActivity.KEY_PHOTO_ACTIVITY_CURRENT_INDEX, currentIndex)
                .withString(PhotoActivity.KEY_PHOTO_ACTIVITY_ID, photo.id)
                .withBundle(a.getString(R.string.transition_photo_image), b)
                .withOptionsCompat(optionsCompat)
                .navigation(a);
    }

    public static void startPhotoActivity(Activity a, String photoId) {
        ARouter.getInstance()
                .build(PhotoActivity.PHOTO_ACTIVITY)
                .withString(PhotoActivity.KEY_PHOTO_ACTIVITY_ID, photoId)
                // .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a);
    }
}
