package com.wangdaye.mysplash.collection.presenter.widget;

import android.content.Context;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.data.Collection;
import com.wangdaye.mysplash._common.data.data.Photo;
import com.wangdaye.mysplash._common.data.service.PhotoService;
import com.wangdaye.mysplash._common.i.model.PhotosModel;
import com.wangdaye.mysplash._common.i.presenter.PhotosPresenter;
import com.wangdaye.mysplash._common.i.view.PhotosView;
import com.wangdaye.mysplash.collection.model.PhotosObject;
import com.wangdaye.mysplash._common.ui.toast.MaterialToast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Photos implementor.
 * */

public class PhotosImplementor
        implements PhotosPresenter {
    // model & view.
    private PhotosModel model;
    private PhotosView view;

    // data
    private Collection requestKey;

    /** <br> life cycle. */

    public PhotosImplementor(Collection c, PhotosModel model, PhotosView view) {
        this.model = model;
        this.view = view;
        this.requestKey = c;
    }

    public Collection getRequestKey() {
        return requestKey;
    }

    /** <br> presenter. */

    @Override
    public void requestPhotos(Context c, int page, boolean refresh) {
        if (!model.isLoading()) {
            model.setLoading(true);
            switch (model.getPhotosType()) {
                case PhotosObject.PHOTOS_TYPE_NORMAL:
                    requestCollectionPhotos(c, requestKey, page, refresh);
                    break;

                case PhotosObject.PHOTOS_TYPE_CURATED:
                    requestCuratedCollectionPhotos(c, requestKey, page, refresh);
                    break;
            }
        }
    }

    @Override
    public void cancelRequest() {
        model.getService().cancel();
    }

    @Override
    public void refreshNew(Context c, boolean notify) {
        if (notify) {
            view.setRefreshing(true);
        }
        requestPhotos(c, model.getPhotosPage(), true);
    }

    @Override
    public void loadMore(Context c, boolean notify) {
        if (notify) {
            view.setLoading(true);
        }
        requestPhotos(c, model.getPhotosPage(), false);
    }

    @Override
    public void initRefresh(Context c) {
        model.getService().cancel();
        model.setLoading(false);
        refreshNew(c, false);
        view.initRefreshStart();
    }

    @Override
    public boolean waitingRefresh() {
        return !model.isLoading() && model.getAdapter().getRealItemCount() <= 0;
    }

    @Override
    public boolean canLoadMore() {
        return !model.isLoading() && !model.isOver();
    }

    @Override
    public void setOrder(String key) {
        model.setPhotosOrder(key);
    }

    /** <br> utils. */

    private void requestCollectionPhotos(Context context,
                                         Collection collection, int page, boolean refresh) {
        model.getService()
                .requestCollectionPhotos(
                        collection,
                        page,
                        Mysplash.DEFAULT_PER_PAGE,
                        new OnRequestPhotosListener(context, page, refresh));
    }

    private void requestCuratedCollectionPhotos(Context context,
                                                Collection collection, int page, boolean refresh) {
        model.getService()
                .requestCuratedCollectionPhotos(
                        collection,
                        page,
                        Mysplash.DEFAULT_PER_PAGE,
                        new OnRequestPhotosListener(context, page, refresh));
    }

    /** <br> interface. */

    private class OnRequestPhotosListener implements PhotoService.OnRequestPhotosListener {
        // data
        private Context c;
        private int page;
        private boolean refresh;

        public OnRequestPhotosListener(Context c, int page, boolean refresh) {
            this.c = c;
            this.page = page;
            this.refresh = refresh;
        }

        @Override
        public void onRequestPhotosSuccess(Call<List<Photo>> call, Response<List<Photo>> response) {
            model.setLoading(false);
            if (refresh) {
                model.getAdapter().clearItem();
                model.setOver(false);
                view.setRefreshing(false);
                view.setPermitLoading(true);
            } else {
                view.setLoading(false);
            }
            if (response.isSuccessful()
                    && model.getAdapter().getRealItemCount() + response.body().size() > 0) {
                model.setPhotosPage(page);
                for (int i = 0; i < response.body().size(); i ++) {
                    model.getAdapter().insertItem(response.body().get(i));
                }
                if (response.body().size() < Mysplash.DEFAULT_PER_PAGE) {
                    model.setOver(true);
                    view.setPermitLoading(false);
                    if (response.body().size() == 0) {
                        MaterialToast.makeText(
                                c,
                                c.getString(R.string.feedback_is_over),
                                null,
                                MaterialToast.LENGTH_SHORT).show();
                    }
                }
                view.requestPhotosSuccess();
            } else {
                view.requestPhotosFailed(c.getString(R.string.feedback_load_nothing_tv));
            }
        }

        @Override
        public void onRequestPhotosFailed(Call<List<Photo>> call, Throwable t) {
            model.setLoading(false);
            if (refresh) {
                view.setRefreshing(false);
            } else {
                view.setLoading(false);
            }
            MaterialToast.makeText(
                    c,
                    c.getString(R.string.feedback_load_failed_toast) + " (" + t.getMessage() + ")",
                    null,
                    MaterialToast.LENGTH_SHORT).show();
            view.requestPhotosFailed(c.getString(R.string.feedback_load_failed_tv));
        }
    }
}
