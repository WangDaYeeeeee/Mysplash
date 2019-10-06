package com.wangdaye.photo;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.wangdaye.common.base.application.MultiModulesApplication;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.component.module.PhotoModule;
import com.wangdaye.photo.base.RoutingHelper;

import java.util.ArrayList;

public class PhotoApplication extends MultiModulesApplication {

    private class PhotoModuleIMP implements PhotoModule {

        @Override
        public void startPhotoActivity(Activity a,
                                       View image, View background,
                                       ArrayList<Photo> photoList, int currentIndex, int headIndex) {
            RoutingHelper.startPhotoActivity(a, image, background, photoList, currentIndex, headIndex);
        }

        @Override
        public void startPhotoActivity(Activity a, String photoId) {
            RoutingHelper.startPhotoActivity(a, photoId);
        }

        @Override
        public void startPreviewActivity(Activity a, Photo photo, boolean showIcon) {
            RoutingHelper.startPreviewActivity(a, photo, showIcon);
        }

        @Override
        public void startPreviewActivity(Activity a, User user, boolean showIcon) {
            RoutingHelper.startPreviewActivity(a, user, showIcon);
        }
    }

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setPhotoModule(new PhotoModuleIMP());
    }
}
