package com.wangdaye.common.presenter.event;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.User;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class UserEventResponsePresenter extends BaseEventResponsePresenter {

    @Inject
    public UserEventResponsePresenter(CompositeDisposable disposable) {
        super(disposable);
    }
    
    public void updateUser(@NonNull MutableLiveData<ListResource<User>> current,
                           User user, boolean duplicate) {
        if (current.getValue() == null) {
            return;
        }
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            List<User> list = current.getValue().dataList;
            for (int i = 0; i < list.size(); i ++) {
                if (list.get(i).username.equals(user.username)) {
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
                        current.postValue(ListResource.changeItem(current.getValue(), user, integer));
                    }
                });
    }
}
