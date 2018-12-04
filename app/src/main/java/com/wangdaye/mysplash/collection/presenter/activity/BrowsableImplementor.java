package com.wangdaye.mysplash.collection.presenter.activity;

import android.net.Uri;

import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.service.network.CollectionService;
import com.wangdaye.mysplash.common.i.model.BrowsableModel;
import com.wangdaye.mysplash.common.i.presenter.BrowsablePresenter;
import com.wangdaye.mysplash.common.i.view.BrowsableView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Browsable implementor.
 * */

public class BrowsableImplementor
        implements BrowsablePresenter,
        CollectionService.OnRequestSingleCollectionListener {

    private BrowsableModel model;
    private BrowsableView view;

    public BrowsableImplementor(BrowsableModel model, BrowsableView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public Uri getIntentUri() {
        return model.getIntentUri();
    }

    @Override
    public boolean isBrowsable() {
        return model.isBrowsable();
    }

    @Override
    public void visitPreviousPage() {
        view.visitPreviousPage();
    }

    @Override
    public void requestBrowsableData() {
        view.showRequestDialog();
        requestCollection();
    }

    @Override
    public void cancelRequest() {
        ((CollectionService) model.getService()).cancel();
    }

    private void requestCollection() {
        List<String> keyList = model.getBrowsableDataKey();
        if (keyList.get(1).equals("curated")) {
            ((CollectionService) model.getService()).requestACuratedCollections(keyList.get(2), this);
        } else {
            ((CollectionService) model.getService()).requestACollections(keyList.get(1), this);
        }
    }

    // interface.

    // on request single collection listener.

    @Override
    public void onRequestSingleCollectionSuccess(Call<Collection> call, Response<Collection> response) {
        if (response.isSuccessful() && response.body() != null) {
            view.dismissRequestDialog();
            view.drawBrowsableView(response.body());
        } else {
            requestCollection();
        }
    }

    @Override
    public void onRequestSingleCollectionFailed(Call<Collection> call, Throwable t) {
        requestCollection();
    }
}
