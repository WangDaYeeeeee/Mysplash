package com.wangdaye.common.presenter.event;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Collection;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CollectionEventResponsePresenter extends BaseEventResponsePresenter {

    @Inject
    public CollectionEventResponsePresenter(CompositeDisposable disposable) {
        super(disposable);
    }

    public void createCollection(@NonNull MutableLiveData<ListResource<Collection>> current,
                                        Collection collection) {
        if (current.getValue() == null) {
            return;
        }
        current.setValue(ListResource.insertItem(current.getValue(), collection, 0));
    }

    public void updateCollection(@NonNull MutableLiveData<ListResource<Collection>> current,
                                 Collection collection) {
        if (current.getValue() == null) {
            return;
        }
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            List<Collection> list = current.getValue().dataList;
            for (int i = 0; i < list.size(); i ++) {
                if (list.get(i).id == collection.id) {
                    Collection original = list.get(i);
                    if (collection.cover_photo != null && original.cover_photo != null) {
                        collection.cover_photo.loadPhotoSuccess = original.cover_photo.loadPhotoSuccess;
                        collection.cover_photo.hasFadedIn = original.cover_photo.hasFadedIn;
                    }
                    emitter.onNext(i);
                    emitter.onComplete();
                    return;
                }
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.from(getExecutor()))
                .observeOn(Schedulers.trampoline())
                .subscribe(new SimpleDisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        current.postValue(ListResource.changeItem(current.getValue(), collection, integer));
                    }
                });
    }

    public void deleteCollection(@NonNull MutableLiveData<ListResource<Collection>> current,
                                 Collection collection) {
        if (current.getValue() == null) {
            return;
        }
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            List<Collection> list = current.getValue().dataList;
            for (int i = 0; i < list.size(); i ++) {
                if (list.get(i).id == collection.id) {
                    emitter.onNext(i);
                    emitter.onComplete();
                    return;
                }
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.from(getExecutor()))
                .observeOn(Schedulers.trampoline())
                .subscribe(new SimpleDisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        current.postValue(ListResource.removeItem(current.getValue(), integer));
                    }
                });
    }
}
