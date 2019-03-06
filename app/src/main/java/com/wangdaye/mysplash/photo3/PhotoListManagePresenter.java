package com.wangdaye.mysplash.photo3;

import com.wangdaye.mysplash.common.network.json.Photo;

import java.util.List;

import androidx.annotation.Nullable;

public class PhotoListManagePresenter {

    private List<Photo> photoList;
    private int currentIndex;
    private int headIndex;

    public PhotoListManagePresenter(List<Photo> photoList, int currentIndex, int headIndex) {
        this.photoList = photoList;
        this.currentIndex = currentIndex;
        this.headIndex = headIndex;
    }

    public void setCurrentIndex(int index) {
        this.currentIndex = index;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setHeadIndex(int index) {
        this.headIndex = index;
    }

    public int getHeadIndex() {
        return headIndex;
    }

    public int getTailIndex() {
        return headIndex + photoList.size() - 1;
    }

    public int getSize() {
        return photoList.size();
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

    @Nullable
    public Photo getPhoto() {
        if (photoList == null || photoList.size() == 0
                || currentIndex < 0 || headIndex < 0) {
            return null;
        } else {
            return photoList.get(currentIndex - headIndex);
        }
    }
}
