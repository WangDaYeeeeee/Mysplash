package com.wangdaye.common.presenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.base.unsplash.LikePhotoResult;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.PhotoEvent;
import com.wangdaye.common.network.ComponentCollection;
import com.wangdaye.common.network.observer.BaseObserver;
import com.wangdaye.common.network.service.PhotoService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.reactivex.disposables.CompositeDisposable;

public class LikePhotoPresenter {

    private static class Inner{
        private static LikePhotoPresenter instance = new LikePhotoPresenter();
    }

    public static LikePhotoPresenter getInstance() {
        return Inner.instance;
    }

    private final List<Photo> photoList;
    private final PhotoService photoService;
    private final ReadWriteLock lock;

    private LikePhotoPresenter() {
        photoList = new ArrayList<>();
        photoService = new PhotoService(
                ComponentCollection.getInstance().getHttpClient(),
                ComponentCollection.getInstance().getGsonConverterFactory(),
                ComponentCollection.getInstance().getRxJava2CallAdapterFactory(),
                new CompositeDisposable()
        );
        lock = new ReentrantReadWriteLock();
    }

    private int indexPhoto(Photo photo) {
        for (int i = 0; i < photoList.size(); i ++) {
            if (photoList.get(i).id.equals(photo.id)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isInProgress(Photo photo) {
        lock.readLock().lock();
        boolean result = indexPhoto(photo) >= 0;
        lock.readLock().unlock();
        return result;
    }

    public void like(Photo photo) {
        lock.writeLock().lock();
        if (indexPhoto(photo) < 0) {
            photoList.add(photo);
            photoService.likePhoto(photo.id, new BaseObserver<LikePhotoResult>() {
                @Override
                public void onSucceed(LikePhotoResult likePhotoResult) {
                    handleResult(likePhotoResult.photo, likePhotoResult.user);
                }

                @Override
                public void onFailed() {
                    handleResult(photo, null);
                }
            });
        }
        lock.writeLock().unlock();

        MessageBus.getInstance().post(PhotoEvent.likeOrCancel(photo.id, photo.liked_by_user));
    }

    public void unlike(Photo photo) {
        lock.writeLock().lock();
        if (indexPhoto(photo) < 0) {
            photoList.add(photo);
            photoService.cancelLikePhoto(photo.id, new BaseObserver<LikePhotoResult>() {
                @Override
                public void onSucceed(LikePhotoResult likePhotoResult) {
                    handleResult(likePhotoResult.photo, likePhotoResult.user);
                }

                @Override
                public void onFailed() {
                    handleResult(photo, null);
                }
            });
        }
        lock.writeLock().unlock();

        MessageBus.getInstance().post(PhotoEvent.likeOrCancel(photo.id, photo.liked_by_user));
    }

    private void handleResult(@NonNull Photo photo, @Nullable User user) {
        lock.writeLock().lock();
        int index = indexPhoto(photo);
        if (index >= 0) {
            photoList.remove(index);
        }
        lock.writeLock().unlock();

        MessageBus.getInstance().post(PhotoEvent.likeOrCancel(photo.id, photo.liked_by_user));
        if (user != null) {
            MessageBus.getInstance().post(user);
        }
    }
}
