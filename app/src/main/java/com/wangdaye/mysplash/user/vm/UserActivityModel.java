package com.wangdaye.mysplash.user.vm;

import com.wangdaye.mysplash.common.basic.model.Resource;
import com.wangdaye.mysplash.common.basic.vm.BrowsableViewModel;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.bus.MessageBus;
import com.wangdaye.mysplash.user.repository.UserActivityRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * User browsable view model.
 * */
public class UserActivityModel extends BrowsableViewModel<User>
        implements Consumer<User> {

    private UserActivityRepository repository;
    private Disposable disposable;

    private String username;

    @Inject
    public UserActivityModel(UserActivityRepository repository) {
        super();
        this.repository = repository;
        this.disposable = MessageBus.getInstance()
                .toObservable(User.class)
                .subscribe(this);
        this.username = null;
    }

    public void init(@NonNull Resource<User> defaultUser, String username) {
        boolean init = super.init(defaultUser);

        if (this.username == null) {
            this.username = username;
        }

        if (init && getResource().getValue() != null
                && (getResource().getValue().data == null
                || !getResource().getValue().data.complete)) {
            requestUser();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancel();
        disposable.dispose();
    }

    public void requestUser() {
        repository.getUser(getResource(), username);
    }

    public void followOrCancelFollowUser(boolean setToFollow) {
        repository.followOrCancelFollowUser(getResource(), username, setToFollow);
    }

    public String getUsername() {
        return username;
    }

    // interface.

    @Override
    public void accept(User user) {
        if (user.username.equals(username)) {
            getResource().setValue(Resource.success(user));
        }
    }
}
