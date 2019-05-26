package com.wangdaye.mysplash.common.presenter.event;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.bus.event.DownloadEvent;
import com.wangdaye.mysplash.common.db.DownloadMissionEntity;
import com.wangdaye.mysplash.common.network.json.Photo;

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
                || event.type == DownloadMissionEntity.COLLECTION_TYPE) {
            return;
        }

        Observable.create((ObservableOnSubscribe<PhotoItem>) emitter -> {
            List<Photo> list = current.getValue().dataList;
            for (int i = 0; i < list.size(); i ++) {
                if (list.get(i).id.equals(event.title)) {
                    emitter.onNext(
                            new PhotoItem(list.get(i), i)
                    );
                    if (!duplicate) {
                        emitter.onComplete();
                        return;
                    }
                }
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
