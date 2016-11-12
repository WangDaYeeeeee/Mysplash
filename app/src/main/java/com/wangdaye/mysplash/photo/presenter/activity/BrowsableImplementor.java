package com.wangdaye.mysplash.photo.presenter.activity;

import android.net.Uri;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.entity.Photo;
import com.wangdaye.mysplash._common.data.service.PhotoService;
import com.wangdaye.mysplash._common.i.model.BrowsableModel;
import com.wangdaye.mysplash._common.i.presenter.BrowsablePresenter;
import com.wangdaye.mysplash._common.i.view.BrowsableView;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Browsable implementor.
 * */

public class BrowsableImplementor
        implements BrowsablePresenter,
        PhotoService.OnRequestSinglePhotoListener {
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
        ((PhotoService) model.getService()).requestAPhoto(model.getBrowsableDataKey(), this);
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
        ((PhotoService) model.getService()).cancel();
    }

    /** <br> listener. */

    @Override
    public void onRequestSinglePhotoSuccess(Call<Photo> call, Response<Photo> response) {
        if (response.isSuccessful() && response.body() != null) {
            Mysplash.getInstance()
                    .getTopActivity()
                    .getIntent()
                    .putExtra(PhotoActivity.KEY_PHOTO_ACTIVITY_PHOTO, response.body());
            view.dismissRequestDialog();
            view.drawBrowsableView();
        } else {
            ((PhotoService) model.getService()).requestAPhoto(model.getBrowsableDataKey(), this);
        }
    }

    @Override
    public void onRequestSinglePhotoFailed(Call<Photo> call, Throwable t) {
        ((PhotoService) model.getService()).requestAPhoto(model.getBrowsableDataKey(), this);
    }
}
