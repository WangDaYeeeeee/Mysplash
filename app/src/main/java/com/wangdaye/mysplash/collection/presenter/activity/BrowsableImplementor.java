package com.wangdaye.mysplash.collection.presenter.activity;

import android.net.Uri;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.entity.Collection;
import com.wangdaye.mysplash._common.data.service.CollectionService;
import com.wangdaye.mysplash._common.i.model.BrowsableModel;
import com.wangdaye.mysplash._common.i.presenter.BrowsablePresenter;
import com.wangdaye.mysplash._common.i.view.BrowsableView;
import com.wangdaye.mysplash.collection.view.activity.CollectionActivity;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Browsable implementor.
 * */

public class BrowsableImplementor
        implements BrowsablePresenter,
        CollectionService.OnRequestSingleCollectionListener {
    // model & view.
    private BrowsableModel model;
    private BrowsableView view;

    /** <br> life cycle. */

    public BrowsableImplementor(BrowsableModel model, BrowsableView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public Uri getIntentUri() {
        return model.getIntentUri();
    }

    @Override
    public boolean isBrowsable() {
        return model.isBrowsable();
    }

    @Override
    public void requestBrowsableData() {
        view.showRequestDialog();
        requestCollection();
    }

    @Override
    public void drawBrowsableView() {
        view.drawBrowsableView();
    }

    @Override
    public void visitParentView() {
        view.visitParentView();
    }

    @Override
    public void cancelRequest() {
        ((CollectionService) model.getService()).cancel();
    }

    /** <br> utils. */

    private void requestCollection() {
        String[] keys = model.getBrowsableDataKey().split(",");
        if (keys[1].equals("curated")) {
            ((CollectionService) model.getService()).requestACuratedCollections(keys[2], this);
        } else {
            ((CollectionService) model.getService()).requestACollections(keys[1], this);
        }
    }

    /** <br> listener. */

    @Override
    public void onRequestSingleCollectionSuccess(Call<Collection> call, Response<Collection> response) {
        if (response.isSuccessful() && response.body() != null) {
            Mysplash.getInstance()
                    .getTopActivity()
                    .getIntent()
                    .putExtra(CollectionActivity.KEY_COLLECTION_ACTIVITY_COLLECTION, response.body());
            view.dismissRequestDialog();
            view.drawBrowsableView();
        } else {
            requestCollection();
        }
    }

    @Override
    public void onRequestSingleCollectionFailed(Call<Collection> call, Throwable t) {
        requestCollection();
    }
}
