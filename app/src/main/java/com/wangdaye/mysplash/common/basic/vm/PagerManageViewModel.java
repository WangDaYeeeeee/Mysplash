package com.wangdaye.mysplash.common.basic.vm;

import javax.inject.Inject;

import androidx.annotation.CallSuper;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Pager manage view model.
 * */
public class PagerManageViewModel extends ViewModel {

    private MutableLiveData<Integer> pagerPosition;

    @Inject
    public PagerManageViewModel() {
        this.pagerPosition = null;
    }

    @CallSuper
    public void init(int defaultPosition) {
        if (pagerPosition == null) {
            pagerPosition = new MutableLiveData<>();
            pagerPosition.setValue(defaultPosition);
        }
    }

    public MutableLiveData<Integer> getPagerPosition() {
        return pagerPosition;
    }

    public void setPagerPosition(int position) {
        pagerPosition.setValue(position);
    }
}
