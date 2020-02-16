package com.wangdaye.common.base.vm.pager;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.bus.MessageBus;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public abstract class UsersPagerViewModel extends PagerViewModel<User>
        implements Consumer<User> {

    private Disposable disposable;

    public UsersPagerViewModel() {
        this.disposable = MessageBus.getInstance()
                .toObservable(User.class)
                .subscribe(this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }

    @Override
    public void accept(User user) {
        onUpdateUser(user);
    }

    protected void onUpdateUser(User User) {
        asynchronousWriteDataList((writer, resource) -> {
            for (int i = 0; i < resource.dataList.size(); i ++) {
                if (resource.dataList.get(i).username.equals(User.username)) {
                    writer.postListResource(ListResource.changeItem(resource, User, i));
                }
            }
        });
    }
}
