package com.wangdaye.common.presenter.event;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Photo;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PhotoEventResponsePresenter extends BaseEventResponsePresenter {

    @Inject
    public PhotoEventResponsePresenter(CompositeDisposable disposable) {
        super(disposable);
    }
    
    public void updatePhoto(@NonNull MutableLiveData<ListResource<Photo>> current,
                            Photo photo, boolean duplicate) {
        if (current.getValue() == null) {
            return;
        }
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            List<Photo> list = current.getValue().dataList;
            for (int i = 0; i < list.size(); i ++) {
                if (list.get(i).id.equals(photo.id)) {
                    Photo original = list.get(i);
                    photo.loadPhotoSuccess = original.loadPhotoSuccess;
                    photo.hasFadedIn = original.hasFadedIn;

                    emitter.onNext(i);
                    if (!duplicate) {
                        emitter.onComplete();
                        return;
                    }
                }
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.from(getExecutor()))
                .observeOn(Schedulers.trampoline())
                .subscribe(new SimpleDisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        current.postValue(ListResource.changeItem(current.getValue(), photo, integer));
                    }
                });
    }

    public void removePhoto(@NonNull MutableLiveData<ListResource<Photo>> current,
                                   Photo photo, boolean duplicate) {
        if (current.getValue() == null) {
            return;
        }

        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            List<Photo> list = current.getValue().dataList;
            for (int i = list.size() - 1; i >= 0; i --) {
                if (list.get(i).id.equals(photo.id)) {
                    emitter.onNext(i);
                    if (!duplicate) {
                        emitter.onComplete();
                        return;
                    }
                }
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.trampoline())
                .subscribe(new SimpleDisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        current.postValue(ListResource.removeItem(current.getValue(), integer));
                    }
                });
    }
}
