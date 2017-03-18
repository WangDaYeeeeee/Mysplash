package com.wangdaye.mysplash.photo.presenter;

import android.net.Uri;

import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.data.service.PhotoInfoService;
import com.wangdaye.mysplash._common.i.model.BrowsableModel;
import com.wangdaye.mysplash._common.i.presenter.BrowsablePresenter;
import com.wangdaye.mysplash._common.i.view.BrowsableView;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Browsable implementor.
 * */

public class BrowsableImplementor
        implements BrowsablePresenter,
        PhotoInfoService.OnRequestSinglePhotoListener {
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
        ((PhotoInfoService) model.getService()).requestAPhoto(model.getBrowsableDataKey(), this);
    }

    @Override
    public void visitParentView() {
        view.visitParentView();
    }

    @Override
    public void cancelRequest() {
        ((PhotoInfoService) model.getService()).cancel();
    }

    /** <br> swipeListener. */

    @Override
    public void onRequestSinglePhotoSuccess(Call<Photo> call, Response<Photo> response) {
        if (response.isSuccessful() && response.body() != null) {
            Photo photo = response.body();
            photo.complete = true;
            view.dismissRequestDialog();
            view.drawBrowsableView(photo);
        } else {
            ((PhotoInfoService) model.getService()).requestAPhoto(model.getBrowsableDataKey(), this);
        }
    }

    @Override
    public void onRequestSinglePhotoFailed(Call<Photo> call, Throwable t) {
        ((PhotoInfoService) model.getService()).requestAPhoto(model.getBrowsableDataKey(), this);
    }
}
