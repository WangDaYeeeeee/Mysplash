package com.wangdaye.mysplash.main.presenter.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.api.PhotoApi;
import com.wangdaye.mysplash.common.data.data.Photo;
import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.common.utils.MathUtils;
import com.wangdaye.mysplash.common.utils.ValueUtils;
import com.wangdaye.mysplash.main.model.widget.DisplayStateObject;
import com.wangdaye.mysplash.main.model.widget.TypeStateObject;
import com.wangdaye.mysplash.main.model.widget.i.DisplayStateModel;
import com.wangdaye.mysplash.main.model.widget.i.PhotoStateModel;
import com.wangdaye.mysplash.main.model.widget.i.TypeStateModel;
import com.wangdaye.mysplash.main.presenter.widget.i.DisplayStatePresenter;
import com.wangdaye.mysplash.main.presenter.widget.i.RequestDataPresenter;
import com.wangdaye.mysplash.main.view.widget.i.LoadingView;
import com.wangdaye.mysplash.main.view.widget.i.PhotosView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Request data implementor.
 * */

public class RequestDataImp
        implements RequestDataPresenter {
    // model.
    private PhotoStateModel photoStateModel;
    private TypeStateModel typeStateModel;
    private DisplayStateModel displayStateModel;

    // view.
    private LoadingView loadingView;
    private PhotosView photosView;

    // presenter.
    private DisplayStatePresenter displayStatePresenter;

    /** <br> life cycle. */

    public RequestDataImp(PhotoStateModel photoStateModel, @Nullable TypeStateModel typeStateModel, DisplayStateModel displayStateModel,
                          LoadingView loadingView, PhotosView photosView,
                          DisplayStatePresenter displayStatePresenter) {
        this.photoStateModel = photoStateModel;
        this.typeStateModel = typeStateModel;
        this.displayStateModel = displayStateModel;
        this.loadingView = loadingView;
        this.photosView = photosView;
        this.displayStatePresenter = displayStatePresenter;
    }

    /** <br> presenter. */

    // request photos.

    @Override
    public void requestPhotos(Context c, boolean refresh) {
        if (!photoStateModel.isLoadingData()) {
            photoStateModel.setLoadingData(true);
            if (photoStateModel.isNormalMode()) {
                requestPhotosOrder(c, refresh);
            } else {
                requestPhotosRandom(c, refresh);
            }
        }
    }

    private void requestPhotosOrder(Context c, boolean refresh) {
        switch (typeStateModel.getType()) {
            case TypeStateObject.NEW_TYPE:
                photoStateModel.getPhotoService()
                        .requestPhotos(
                                refresh ? 1: photoStateModel.getPage() + 1,
                                PhotoApi.DEFAULT_PER_PAGE,
                                photoStateModel.getOrder(),
                                refresh,
                                new OnRequestPhotosListener(c));
                break;

            case TypeStateObject.FEATURED_TYPE:
                photoStateModel.getPhotoService()
                        .requestCuratePhotos(
                                refresh ? 1: photoStateModel.getPage() + 1,
                                PhotoApi.DEFAULT_PER_PAGE,
                                photoStateModel.getOrder(),
                                refresh,
                                new OnRequestPhotosListener(c));
                break;
        }
    }

    private void requestPhotosRandom(Context c, boolean refresh) {
        if (refresh) {
            switch (typeStateModel.getType()) {
                case TypeStateObject.NEW_TYPE: {
                    List<Integer> list = MathUtils.getPageList(
                            Mysplash.TOTAL_NEW_PHOTOS_COUNT / PhotoApi.DEFAULT_PER_PAGE);
                    photoStateModel.setPageList(list);
                    break;
                }

                case TypeStateObject.FEATURED_TYPE: {
                    List<Integer> list = MathUtils.getPageList(
                            Mysplash.TOTAL_FEATURED_PHOTOS_COUNT / PhotoApi.DEFAULT_PER_PAGE);
                    photoStateModel.setPageList(list);
                    break;
                }
            }
        }
        int page = refresh ? 0 : photoStateModel.getPage();
        switch (typeStateModel.getType()) {
            case TypeStateObject.NEW_TYPE:
                photoStateModel.getPhotoService()
                        .requestPhotos(
                                photoStateModel.getPageList().get(page),
                                PhotoApi.DEFAULT_PER_PAGE,
                                photoStateModel.getOrder(),
                                refresh,
                                new OnRequestPhotosListener(c));
                break;

            case TypeStateObject.FEATURED_TYPE:
                photoStateModel.getPhotoService()
                        .requestPhotos(
                                photoStateModel.getPageList().get(page),
                                PhotoApi.DEFAULT_PER_PAGE,
                                photoStateModel.getOrder(),
                                refresh,
                                new OnRequestPhotosListener(c));
                break;
        }
    }

    // search photos.

    @Override
    public void searchPhotos(Context c, boolean refresh) {
        if (!photoStateModel.isLoadingData()) {
            photoStateModel.setLoadingData(true);
            photoStateModel.getPhotoService()
                    .searchPhotos(
                            photoStateModel.getSearchQuery(),
                            photoStateModel.getOrientation(),
                            refresh ? 1 : photoStateModel.getPage() + 1,
                            PhotoApi.DEFAULT_PER_PAGE,
                            refresh,
                            new OnSearchPhotosListener(c));
        }
    }

    // request photos in category.

    @Override
    public void requestPhotosInCategory(Context c, boolean refresh) {
        if (!photoStateModel.isLoadingData()) {
            photoStateModel.setLoadingData(true);
            if (photoStateModel.isNormalMode()) {
                requestPhotosInCategoryOrder(c, refresh);
            } else {
                requestPhotosInCategoryRandom(c, refresh);
            }
        }
    }

    private void requestPhotosInCategoryOrder(Context c, boolean refresh) {
        photoStateModel.getPhotoService()
                .requestPhotosInAGivenCategory(
                        photoStateModel.getCategoryId(),
                        refresh ? 1 : photoStateModel.getPage() + 1,
                        PhotoApi.DEFAULT_PER_PAGE,
                        refresh,
                        new OnRequestPhotosListener(c));
    }

    private void requestPhotosInCategoryRandom(Context c, boolean refresh) {
        if (refresh) {
            List<Integer> list  = ValueUtils.getPageListByCategory(photoStateModel.getCategoryId());
            photoStateModel.setPageList(list);
        }
        int page = refresh ? 0 : photoStateModel.getPage();
        photoStateModel.getPhotoService()
                .requestPhotosInAGivenCategory(
                        photoStateModel.getCategoryId(),
                        photoStateModel.getPageList().get(page),
                        PhotoApi.DEFAULT_PER_PAGE,
                        refresh,
                        new OnRequestPhotosListener(c));
    }

    // cancel request.

    @Override
    public void cancelRequest() {
        photoStateModel.getPhotoService().cancel();
    }

    /** <br> interface. */

    // on request photos listener.

    private class OnRequestPhotosListener implements PhotoService.OnRequestPhotosListener {
        // widget
        Context c;

        public OnRequestPhotosListener(Context c) {
            this.c = c;
        }

        @Override
        public void onRequestPhotosSuccess(Call<List<Photo>> call, Response<List<Photo>> response,
                                           int page, boolean refresh) {
            if (photoStateModel.isNormalMode()) {
                onRequestPhotosOrderSuccess(response, page, refresh);
            } else {
                onRequestPhotosRandomSuccess(response, refresh);
            }
        }

        private void onRequestPhotosOrderSuccess(Response<List<Photo>> response, int page, boolean refresh) {
            photoStateModel.setLoadingData(false);
            if (refresh) {
                photosView.setRefreshing(false);
                photosView.setPermitLoad(true);
                photoStateModel.getAdapter().clearItem();
                photoStateModel.setLoadFinish(false);
            } else {
                photosView.setLoading(false);
            }
            if (response.isSuccessful()
                    && photoStateModel.getAdapter().getItemCount() + response.body().size() > 0) {
                photoStateModel.setPage(page);
                for (int i = 0; i < response.body().size(); i ++) {
                    photoStateModel.getAdapter().insertItem(response.body().get(i));
                }
                if (response.body().size() < PhotoApi.DEFAULT_PER_PAGE) {
                    photoStateModel.setLoadFinish(true);
                    photosView.setPermitLoad(false);
                    if (response.body().size() == 0) {
                        Toast.makeText(
                                c,
                                c.getString(R.string.feedback_is_over) + "\n" + response.message(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                displayStatePresenter.setNormalState();
            } else {
                loadingView.setFeedbackText(c.getString(R.string.feedback_load_nothing_tv));
                displayStatePresenter.setFailedState();
            }
        }

        private void onRequestPhotosRandomSuccess(Response<List<Photo>> response, boolean refresh) {
            photoStateModel.setLoadingData(false);
            if (refresh) {
                photosView.setRefreshing(false);
                photosView.setPermitLoad(true);
                photoStateModel.getAdapter().clearItem();
                photoStateModel.setLoadFinish(false);
            } else {
                photosView.setLoading(false);
            }
            if (response.isSuccessful()
                    && photoStateModel.getAdapter().getItemCount() + response.body().size() > 0) {
                if (refresh) {
                    photoStateModel.setPage(1);
                } else {
                    photoStateModel.setPage(photoStateModel.getPage() + 1);
                }
                for (int i = 0; i < response.body().size(); i ++) {
                    photoStateModel.getAdapter().insertItem(response.body().get(i));
                }
                if (photoStateModel.getPage() >= photoStateModel.getPageList().size()) {
                    photoStateModel.setLoadFinish(true);
                    photosView.setPermitLoad(false);
                    if (response.body().size() == 0) {
                        Toast.makeText(
                                c,
                                c.getString(R.string.feedback_is_over) + "\n" + response.message(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                loadingView.setFeedbackText(c.getString(R.string.feedback_load_nothing_tv));
                displayStatePresenter.setFailedState();
            }
        }

        @Override
        public void onRequestPhotosFailed(Call<List<Photo>> call, Throwable t, boolean refresh) {
            photoStateModel.setLoadingData(false);
            loadingView.setFeedbackText(c.getString(R.string.feedback_load_failed_tv));
            if (refresh) {
                photosView.setRefreshing(false);
            } else {
                photosView.setLoading(false);
            }
            if (displayStateModel.getState() == DisplayStateObject.NORMAL_DISPLAY_STATE) {
                Toast.makeText(
                        c,
                        c.getString(R.string.feedback_load_failed_toast) + "\n" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // on search photos listener.

    private class OnSearchPhotosListener implements PhotoService.OnRequestPhotosListener {
        // widget
        private Context c;

        public OnSearchPhotosListener(Context c) {
            this.c = c;
        }

        @Override
        public void onRequestPhotosSuccess(Call<List<Photo>> call, Response<List<Photo>> response, int page, boolean refresh) {
            photoStateModel.setLoadingData(false);
            photoStateModel.setPage(page);
            if (refresh) {
                photosView.setRefreshing(false);
                photosView.setPermitLoad(true);
                photoStateModel.getAdapter().clearItem();
                photoStateModel.setLoadFinish(false);
            } else {
                photosView.setLoading(false);
            }
            if (response.isSuccessful()
                    && photoStateModel.getAdapter().getItemCount() + response.body().size() > 0) {
                for (int i = 0; i < response.body().size(); i ++) {
                    photoStateModel.getAdapter().insertItem(response.body().get(i));
                }
                if (response.body().size() < PhotoApi.DEFAULT_PER_PAGE) {
                    photoStateModel.setLoadFinish(true);
                    photosView.setPermitLoad(false);
                    if (response.body().size() == 0) {
                        Toast.makeText(
                                c,
                                c.getString(R.string.feedback_is_over) + "\n" + response.message(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                displayStatePresenter.setNormalState();
            } else {
                loadingView.setFeedbackText(c.getString(R.string.feedback_search_failed_tv));
                displayStatePresenter.setFailedState();
            }
        }

        @Override
        public void onRequestPhotosFailed(Call<List<Photo>> call, Throwable t, boolean refresh) {
            photoStateModel.setLoadingData(false);
            loadingView.setFeedbackText(c.getString(R.string.feedback_search_failed_tv));
            displayStatePresenter.setFailedState();
            if (refresh) {
                photosView.setRefreshing(false);
            } else {
                photosView.setLoading(false);
            }
            if (displayStateModel.getState() == DisplayStateObject.NORMAL_DISPLAY_STATE) {
                Toast.makeText(
                        c,
                        c.getString(R.string.feedback_search_failed_toast) + "\n" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
