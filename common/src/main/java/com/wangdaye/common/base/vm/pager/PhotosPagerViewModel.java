package com.wangdaye.common.base.vm.pager;

import androidx.annotation.Nullable;

import com.wangdaye.base.DownloadTask;
import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.DownloadEvent;
import com.wangdaye.common.bus.event.PhotoEvent;

import io.reactivex.disposables.Disposable;

public abstract class PhotosPagerViewModel extends PagerViewModel<Photo> {

    private Disposable photoEventDisposable;
    private Disposable downloadEventDisposable;

    public PhotosPagerViewModel() {
        this.photoEventDisposable = MessageBus.getInstance()
                .toObservable(PhotoEvent.class)
                .subscribe(photoEvent -> {
                    switch (photoEvent.event) {
                        case LIKE_OR_CANCEL:
                            onUpdatePhoto(photoEvent.photoId, photoEvent.like);
                            break;

                        case COMPLETE:
                            if (photoEvent.photo != null) {
                                onUpdatePhoto(photoEvent.photo);
                            }
                            break;

                        case COLLECT_OR_REMOVE:
                            if (photoEvent.photo == null
                                    || photoEvent.collection == null
                                    || photoEvent.photo.current_user_collections == null) {
                                return;
                            }
                            for (Collection c : photoEvent.photo.current_user_collections) {
                                if (c.id == photoEvent.collection.id) {
                                    onAddPhotoToCollection(photoEvent.photo, photoEvent.collection);
                                    return;
                                }
                            }
                            onRemovePhotoFromCollection(photoEvent.photo, photoEvent.collection);
                            break;
                    }
                });
        this.downloadEventDisposable = MessageBus.getInstance()
                .toObservable(DownloadEvent.class)
                .subscribe(downloadEvent -> {
                    if (downloadEvent.type != DownloadTask.COLLECTION_TYPE) {
                        onDownloadEvent(downloadEvent.title);
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        photoEventDisposable.dispose();
        downloadEventDisposable.dispose();
    }

    protected void onUpdatePhoto(String photoId, boolean like) {
        asynchronousWriteDataList((writer, resource) -> {
            for (int i = 0; i < resource.dataList.size(); i ++) {
                if (resource.dataList.get(i).id.equals(photoId)) {
                    Photo photo = resource.dataList.get(i);
                    photo.liked_by_user = like;
                    photo.likes += like ? 1 : -1;
                    writer.postListResource(ListResource.changeItem(resource, photo, i));
                }
            }
        });
    }

    protected void onUpdatePhoto(Photo photo) {
        asynchronousWriteDataList((writer, resource) -> {
            for (int i = 0; i < resource.dataList.size(); i ++) {
                if (resource.dataList.get(i).id.equals(photo.id)) {
                    writer.postListResource(ListResource.changeItem(resource, photo, i));
                }
            }
        });
    }

    protected void onAddPhotoToCollection(Photo photo, @Nullable Collection collection) {
        onUpdatePhoto(photo);
    }

    protected void onRemovePhotoFromCollection(Photo photo, @Nullable Collection collection) {
        onUpdatePhoto(photo);
    }

    protected void onDownloadEvent(String photoId) {
        asynchronousWriteDataList((writer, resource) -> {
            for (int i = 0; i < resource.dataList.size(); i ++) {
                if (resource.dataList.get(i).id.equals(photoId)) {
                    writer.postListResource(ListResource.changeItem(resource, resource.dataList.get(i), i));
                }
            }
        });
    }
}
