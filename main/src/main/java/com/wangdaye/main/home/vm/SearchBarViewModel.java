package com.wangdaye.main.home.vm;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wangdaye.base.resource.Resource;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.utils.manager.AuthManager;

import javax.inject.Inject;

public class SearchBarViewModel extends ViewModel
        implements AuthManager.OnAuthDataChangedListener {

    private MutableLiveData<Resource<User>> userResource;

    @Inject
    public SearchBarViewModel() {
        userResource = null;
    }

    public void init(Resource<User> resource) {
        if (userResource == null) {
            userResource = new MutableLiveData<>();
            userResource.setValue(resource);
            AuthManager.getInstance().addOnWriteDataListener(this);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        AuthManager.getInstance().removeOnWriteDataListener(this);
    }

    public MutableLiveData<Resource<User>> getUserResource() {
        return userResource;
    }

    // interface.

    @Override
    public void onUpdateAccessToken() {
        // do nothing.
    }

    @Override
    public void onUpdateUser() {
        userResource.setValue(Resource.success(AuthManager.getInstance().getUser()));
    }

    @Override
    public void onUpdateFailed() {
        userResource.setValue(Resource.error(null));
    }

    @Override
    public void onLogout() {
        userResource.setValue(Resource.error(null));
    }
}
