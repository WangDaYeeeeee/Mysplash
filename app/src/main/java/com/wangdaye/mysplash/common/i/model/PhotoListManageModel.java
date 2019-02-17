package com.wangdaye.mysplash.common.i.model;

import androidx.annotation.Nullable;

import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;

import java.util.List;

/**
 * Photo list manage model.
 * */

public interface PhotoListManageModel {

    List<Photo> getPhotoList();

    @Nullable
    Photo getPhoto();

    void setCurrentIndex(int index);
    int getCurrentIndex();

    void setHeadIndex(int index);
    int getHeadIndex();
    int getTailIndex();
}
