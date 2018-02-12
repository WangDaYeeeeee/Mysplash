package com.wangdaye.mysplash.photo2.presenter;

import android.net.Uri;

import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.common.i.model.BrowsableModel;
import com.wangdaye.mysplash.common.i.presenter.BrowsablePresenter;
import com.wangdaye.mysplash.common.i.view.BrowsableView;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Browsable implementor.
 * */

public class BrowsableImplementor
        implements BrowsablePresenter,
        PhotoService.OnRequestSinglePhotoListener {

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
    public void requestBrowsableData() {
        view.showRequestDialog();
        ((PhotoService) model.getService())
                .requestAPhoto(model.getBrowsableDataKey().get(0), this);
    }

    @Override
    public void visitPreviousPage() {
        view.visitPreviousPage();
    }

    @Override
    public void cancelRequest() {
        ((PhotoService) model.getService()).cancel();
    }

    // interface.

    @Override
    public void onRequestSinglePhotoSuccess(Call<Photo> call, Response<Photo> response) {
        if (response.isSuccessful() && response.body() != null) {
            Photo photo = response.body();
            photo.complete = true;
            view.dismissRequestDialog();
            view.drawBrowsableView(photo);
        } else {
            ((PhotoService) model.getService())
                    .requestAPhoto(model.getBrowsableDataKey().get(0), this);
        }
    }

    @Override
    public void onRequestSinglePhotoFailed(Call<Photo> call, Throwable t) {
        ((PhotoService) model.getService())
                .requestAPhoto(model.getBrowsableDataKey().get(0), this);
    }
}
