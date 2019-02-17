package com.wangdaye.mysplash.common.i.presenter;

import androidx.annotation.Nullable;

import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;

import java.util.List;

/**
 * Photo list manage presenter.
 * */

public interface PhotoListManagePresenter {

    List<Photo> getPhotoList();

    @Nullable
    Photo getPhoto();

    void setCurrentIndex(int index);
    int getCurrentIndex();

    void setHeadIndex(int index);
    int getHeadIndex();
    int getTailIndex();
}
