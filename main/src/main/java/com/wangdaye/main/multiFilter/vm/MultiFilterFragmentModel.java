package com.wangdaye.main.multiFilter.vm;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MultiFilterFragmentModel extends ViewModel {

    private MutableLiveData<String> searchQuery;
    private MutableLiveData<String> searchUser;
    private MutableLiveData<String> searchOrientation;
    private MutableLiveData<Boolean> searchFeatured;

    @Inject
    public MultiFilterFragmentModel() {
        searchQuery = null;
        searchUser = null;
        searchOrientation = null;
        searchFeatured = null;
    }

    public void init(String defaultQuery, String defaultUser,
                     String defaultOrientation, boolean defaultFeatured) {
        if (searchQuery == null) {
            searchQuery = new MutableLiveData<>();
            searchQuery.setValue(defaultQuery);
        }
        if (searchUser == null) {
            searchUser = new MutableLiveData<>();
            searchUser.setValue(defaultUser);
        }
        if (searchOrientation == null) {
            searchOrientation = new MutableLiveData<>();
            searchOrientation.setValue(defaultOrientation);
        }
        if (searchFeatured == null) {
            searchFeatured = new MutableLiveData<>();
            searchFeatured.setValue(defaultFeatured);
        }
    }

    public MutableLiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public MutableLiveData<String> getSearchUser() {
        return searchUser;
    }

    public void setSearchUser(String user) {
        searchUser.setValue(user);
    }

    public MutableLiveData<String> getSearchOrientation() {
        return searchOrientation;
    }

    public void setSearchOrientation(String orientation) {
        searchOrientation.setValue(orientation);
    }

    public MutableLiveData<Boolean> getSearchFeatured() {
        return searchFeatured;
    }

    public void setSearchFeatured(boolean featured) {
        searchFeatured.setValue(featured);
    }
}
