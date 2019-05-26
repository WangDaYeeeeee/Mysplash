package com.wangdaye.mysplash.me.vm;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.bus.MessageBus;
import com.wangdaye.mysplash.common.presenter.event.UserEventResponsePresenter;
import com.wangdaye.mysplash.me.repository.MyFollowUserViewRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MyFollowerViewModel extends PagerViewModel<User>
        implements Consumer<User> {

    private MyFollowUserViewRepository repository;
    private UserEventResponsePresenter presenter;
    private Disposable disposable;

    @Inject
    public MyFollowerViewModel(MyFollowUserViewRepository repository,
                               UserEventResponsePresenter presenter) {
        super();
        this.repository = repository;
        this.presenter = presenter;
        this.disposable = MessageBus.getInstance()
                .toObservable(User.class)
                .subscribe(this);
    }

    @Override
    public boolean init(@NonNull ListResource<User> resource) {
        if (super.init(resource)) {
            refresh();
            return true;
        }
        return false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        getRepository().cancel();
        presenter.clearResponse();
        disposable.dispose();
    }

    @Override
    public void refresh() {
        getRepository().getFollowers(getListResource(), true);
    }

    @Override
    public void load() {
        getRepository().getFollowers(getListResource(), false);
    }

    MyFollowUserViewRepository getRepository() {
        return repository;
    }

    // interface.

    @Override
    public void accept(User user) {
        presenter.updateUser(getListResource(), user, false);
    }
}
