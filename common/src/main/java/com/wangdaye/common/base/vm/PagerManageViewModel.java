package com.wangdaye.common.base.vm;

import androidx.annotation.CallSuper;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

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
