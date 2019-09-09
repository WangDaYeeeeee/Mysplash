package com.wangdaye.component.module;

import android.app.Activity;
import android.view.View;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;

import java.util.ArrayList;

public interface PhotoModule {

    void startPhotoActivity(Activity a, View image, View background,
                            ArrayList<Photo> photoList, int currentIndex, int headIndex);

    void startPhotoActivity(Activity a, String photoId);

    void startPreviewActivity(Activity a, Photo photo, boolean showIcon);

    void startPreviewActivity(Activity a, User user, boolean showIcon);
}
