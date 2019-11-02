package com.wangdaye.common.presenter.event;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wangdaye.base.DownloadTask;
import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.bus.event.DownloadEvent;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DownloadEventResponsePresenter extends BaseEventResponsePresenter {

    private class PhotoItem {
        Photo photo;
        int index;

        PhotoItem(Photo photo, int index) {
            this.photo = photo;
            this.index = index;
        }
    }

    @Inject
    public DownloadEventResponsePresenter(CompositeDisposable disposable) {
        super(disposable);
    }
    
    public void updatePhoto(@NonNull MutableLiveData<ListResource<Photo>> current,
                            DownloadEvent event, boolean duplicate) {
        if (current.getValue() == null
                || event.type == DownloadTask.COLLECTION_TYPE) {
            return;
        }

        Observable.create((ObservableOnSubscribe<PhotoItem>) emitter -> {
            List<Photo> list = current.getValue().dataList;
            try {
                for (int i = 0; i < list.size(); i ++) {
                    if (list.get(i).id.equals(event.title)) {
                        PhotoItem item = new PhotoItem((Photo) list.get(i).clone(), i);
                        item.photo.downloading = event.result == DownloadTask.RESULT_DOWNLOADING;
                        emitter.onNext(item);
                        if (!duplicate) {
                            emitter.onComplete();
                            return;
                        }
                    }
                }
            } catch (Exception ignored) {
                // do nothing.
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.from(getExecutor()))
                .observeOn(Schedulers.trampoline())
                .subscribe(new SimpleDisposableObserver<PhotoItem>() {
                    @Override
                    public void onNext(PhotoItem item) {
                        current.postValue(ListResource.changeItem(
                                current.getValue(), item.photo, item.index)
                        );
                    }
                });
    }
}
