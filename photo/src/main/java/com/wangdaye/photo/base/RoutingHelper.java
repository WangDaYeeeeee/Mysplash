package com.wangdaye.photo.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.alibaba.android.arouter.launcher.ARouter;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.ui.transition.sharedElement.Recolor;
import com.wangdaye.common.ui.transition.sharedElement.RoundCornerTransition;
import com.wangdaye.photo.R;
import com.wangdaye.photo.activity.PhotoActivity;
import com.wangdaye.photo.activity.PreviewActivity;

import java.util.ArrayList;

public class RoutingHelper {

    public static void startPhotoActivity(Activity a, View image, View background,
                                          ArrayList<Photo> photoList, int currentIndex, int headIndex) {
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
                    Pair.create(image, a.getString(R.string.transition_photo_image)),
                    Pair.create(background, a.getString(R.string.transition_photo_background))
            );

            Recolor.addExtraProperties(background, b);
            RoundCornerTransition.addExtraProperties(background, b);
            MysplashApplication.getInstance().setSharedElementTransitionExtraProperties(b);
        }

        ARouter.getInstance()
                .build(PhotoActivity.PHOTO_ACTIVITY)
                .withParcelableArrayList(PhotoActivity.KEY_PHOTO_ACTIVITY_PHOTO_LIST, photoList)
                .withInt(PhotoActivity.KEY_PHOTO_ACTIVITY_PHOTO_CURRENT_INDEX, currentIndex)
                .withInt(PhotoActivity.KEY_PHOTO_ACTIVITY_PHOTO_HEAD_INDEX, headIndex)
                .withString(PhotoActivity.KEY_PHOTO_ACTIVITY_ID, photoList.get(currentIndex - headIndex).id)
                .withBundle(a.getString(R.string.transition_photo_image), b)
                .withBundle(a.getString(R.string.transition_photo_background), b)
                .withOptionsCompat(optionsCompat)
                .navigation(a);
    }

    public static void startPhotoActivity(Activity a, String photoId) {
        ARouter.getInstance()
                .build(PhotoActivity.PHOTO_ACTIVITY)
                .withString(PhotoActivity.KEY_PHOTO_ACTIVITY_ID, photoId)
                .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a);
    }

    public static void startPreviewActivity(Activity a, Photo photo, boolean showIcon) {
        ARouter.getInstance()
                .build(PreviewActivity.PREVIEW_ACTIVITY)
                .withParcelable(PreviewActivity.KEY_PREVIEW_ACTIVITY_PREVIEW, photo)
                .withBoolean(PreviewActivity.KEY_PREVIEW_ACTIVITY_SHOW_ICON, showIcon)
                .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a);
    }

    public static void startPreviewActivity(Activity a, User user, boolean showIcon) {
        ARouter.getInstance()
                .build(PreviewActivity.PREVIEW_ACTIVITY)
                .withParcelable(PreviewActivity.KEY_PREVIEW_ACTIVITY_PREVIEW, user)
                .withBoolean(PreviewActivity.KEY_PREVIEW_ACTIVITY_SHOW_ICON, showIcon)
                .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a);
    }
}
