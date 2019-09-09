package com.wangdaye.collection.base;

import android.content.Context;

import com.wangdaye.collection.CollectionActivity;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.CollectionEvent;
import com.wangdaye.common.bus.event.PhotoEvent;
import com.wangdaye.common.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.common.ui.dialog.DeleteCollectionPhotoDialog;

import java.util.List;

public class PhotoItemEventHelper extends com.wangdaye.common.ui.adapter.photo.PhotoItemEventHelper {

    private CollectionActivity activity;

    public PhotoItemEventHelper(CollectionActivity activity,
                                List<Photo> photoList,
                                LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter) {
        super(activity, photoList, likeOrDislikePhotoPresenter);
        this.activity = activity;
    }

    @Override
    public void downloadPhoto(Context context, Photo photo) {
        activity.requestPermissionAndDownload(photo);
    }

    @Override
    public void onDeleteButtonClicked(Photo photo, int adapterPosition) {
        DeleteCollectionPhotoDialog dialog = new DeleteCollectionPhotoDialog();
        dialog.setDeleteInfo(activity.getCollection(), photo);
        dialog.setOnDeleteCollectionListener(result -> {
            MessageBus.getInstance().post(new PhotoEvent(
                    result.photo, result.collection, PhotoEvent.Event.REMOVE_FROM_COLLECTION));

            MessageBus.getInstance().post(new CollectionEvent(
                    result.collection, CollectionEvent.Event.UPDATE));

            MessageBus.getInstance().post(result.user);
        });
        dialog.show(activity.getSupportFragmentManager(), null);
    }
}
