package com.wangdaye.mysplash.me.presenter.widget;

import android.content.Context;
import android.support.design.widget.Snackbar;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.i.model.PhotosModel;
import com.wangdaye.mysplash.common.i.presenter.PhotosPresenter;
import com.wangdaye.mysplash.common.i.view.PhotosView;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.user.model.widget.PhotosObject;

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
    private OnRequestPhotosListener listener;

    /** <br> life cycle. */

    public PhotosImplementor(PhotosModel model, PhotosView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void requestPhotos(Context c, int page, boolean refresh) {
        if (!model.isRefreshing() && !model.isLoading()
                && AuthManager.getInstance().getMe() != null) {
            if (refresh) {
                model.setRefreshing(true);
            } else {
                model.setLoading(true);
            }
            switch (model.getPhotosType()) {
                case PhotosObject.PHOTOS_TYPE_PHOTOS:
                    requestUserPhotos(c, page, refresh, model.getPhotosOrder());
                    break;

                case PhotosObject.PHOTOS_TYPE_LIKES:
                    requestUserLikes(c, page, refresh, model.getPhotosOrder());
                    break;
            }
        }
    }

    @Override
    public void cancelRequest() {
        if (listener != null) {
            listener.cancel();
        }
        model.getService().cancel();
        model.setRefreshing(false);
        model.setLoading(false);
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
        cancelRequest();
        refreshNew(c, false);
        view.initRefreshStart();
    }

    @Override
    public boolean canLoadMore() {
        return !model.isRefreshing() && !model.isLoading() && !model.isOver();
    }

    @Override
    public boolean isRefreshing() {
        return model.isRefreshing();
    }

    @Override
    public boolean isLoading() {
        return model.isLoading();
    }

    @Override
    public Object getRequestKey() {
        return null;
    }

    @Override
    public void setRequestKey(Object k) {
        // do nothing.
    }

    @Override
    public int getPhotosType() {
        return model.getPhotosType();
    }

    @Override
    public String getPhotosOrder() {
        return model.getPhotosOrder();
    }

    @Override
    public void setOrder(String key) {
        model.setPhotosOrder(key);
    }

    @Override
    public String getOrder() {
        return model.getPhotosOrder();
    }

    @Override
    public void setPage(int page) {
        model.setPhotosPage(page);
    }

    @Override
    public void setPageList(List<Integer> pageList) {
        model.setPageList(pageList);
    }

    @Override
    public void setOver(boolean over) {
        model.setOver(over);
        view.setPermitLoading(!over);
    }

    @Override
    public void setActivityForAdapter(MysplashActivity a) {
        model.getAdapter().setActivity(a);
    }

    @Override
    public PhotoAdapter getAdapter() {
        return model.getAdapter();
    }

    /** <br> utils. */

    private void requestUserPhotos(Context c, int page, boolean refresh, String order) {
        page = Math.max(1, refresh ? 1: page + 1);
        listener = new OnRequestPhotosListener(c, page, refresh);
        model.getService()
                .requestUserPhotos(
                        AuthManager.getInstance().getMe().username,
                        page,
                        Mysplash.DEFAULT_PER_PAGE,
                        order,
                        listener);
    }

    private void requestUserLikes(Context c, int page, boolean refresh, String order) {
        page = Math.max(1, refresh ? 1: page + 1);
        listener = new OnRequestPhotosListener(c, page, refresh);
        model.getService()
                .requestUserLikes(
                        AuthManager.getInstance().getMe().username,
                        page,
                        Mysplash.DEFAULT_PER_PAGE,
                        order,
                        listener);
    }

    /** <br> interface. */

    private class OnRequestPhotosListener implements PhotoService.OnRequestPhotosListener {
        // data
        private Context c;
        private int page;
        private boolean refresh;
        private boolean canceled;

        OnRequestPhotosListener(Context c, int page, boolean refresh) {
            this.c = c;
            this.page = page;
            this.refresh = refresh;
            this.canceled = false;
        }

        public void cancel() {
            canceled = true;
        }

        @Override
        public void onRequestPhotosSuccess(Call<List<Photo>> call, Response<List<Photo>> response) {
            if (canceled) {
                return;
            }
            model.setRefreshing(false);
            model.setLoading(false);
            if (refresh) {
                model.getAdapter().clearItem();
                setOver(false);
                view.setRefreshing(false);
            } else {
                view.setLoading(false);
            }
            if (response.isSuccessful()) {
                model.setPhotosPage(page);
                for (int i = 0; i < response.body().size(); i ++) {
                    model.getAdapter().insertItem(response.body().get(i));
                }
                if (response.body().size() < Mysplash.DEFAULT_PER_PAGE) {
                    setOver(true);
                }
                view.requestPhotosSuccess();
            } else {
                view.requestPhotosFailed(c.getString(R.string.feedback_load_nothing_tv));
            }
        }

        @Override
        public void onRequestPhotosFailed(Call<List<Photo>> call, Throwable t) {
            if (canceled) {
                return;
            }
            model.setRefreshing(false);
            model.setLoading(false);
            if (refresh) {
                view.setRefreshing(false);
            } else {
                view.setLoading(false);
            }
            NotificationHelper.showSnackbar(
                    c.getString(R.string.feedback_load_failed_toast) + " (" + t.getMessage() + ")",
                    Snackbar.LENGTH_SHORT);
            view.requestPhotosFailed(c.getString(R.string.feedback_load_failed_tv));
        }
    }
}
