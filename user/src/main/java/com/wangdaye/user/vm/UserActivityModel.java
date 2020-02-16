package com.wangdaye.user.vm;

import com.wangdaye.base.resource.Resource;
import com.wangdaye.common.base.vm.BrowsableViewModel;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.FollowEvent;
import com.wangdaye.user.repository.UserActivityRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * User browsable view model.
 * */
public class UserActivityModel extends BrowsableViewModel<User> {

    private UserActivityRepository repository;
    private Disposable userEventDisposable;
    private Disposable followEventDisposable;

    private String username;

    @Inject
    public UserActivityModel(UserActivityRepository repository) {
        super();
        this.repository = repository;
        this.userEventDisposable = MessageBus.getInstance()
                .toObservable(User.class)
                .subscribe(user -> {
                    if (user.username.equals(username)) {
                        getResource().setValue(Resource.success(user));
                    }
                });
        this.followEventDisposable = MessageBus.getInstance()
                .toObservable(FollowEvent.class)
                .subscribe(followEvent -> {
                    if (followEvent.target.username.equals(username) && getResource().getValue() != null) {
                        Resource.Status status = getResource().getValue().status;
                        switch (status) {
                            case SUCCESS:
                                getResource().setValue(Resource.success(followEvent.target));
                                break;

                            case ERROR:
                                getResource().setValue(Resource.error(followEvent.target));
                                break;

                            case LOADING:
                                getResource().setValue(Resource.loading(followEvent.target));
                                break;
                        }
                    }
                });
        this.username = null;
    }

    public void init(@NonNull Resource<User> defaultUser, String username) {
        boolean init = super.init(defaultUser);

        if (this.username == null) {
            this.username = username;
        }

        if (init && getResource().getValue() != null
                && (getResource().getValue().data == null
                || !getResource().getValue().data.isComplete())) {
            requestUser();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancel();
        userEventDisposable.dispose();
        followEventDisposable.dispose();
    }

    public void requestUser() {
        repository.getUser(this, username);
    }

    public void followOrCancelFollowUser(boolean setToFollow) {
        repository.followOrCancelFollowUser(this, username, setToFollow);
    }

    public String getUsername() {
        return username;
    }
}
