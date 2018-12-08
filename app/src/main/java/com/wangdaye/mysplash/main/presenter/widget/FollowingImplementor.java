package com.wangdaye.mysplash.main.presenter.widget;

import android.content.Context;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.service.network.FeedService;
import com.wangdaye.mysplash.common.i.model.FollowingModel;
import com.wangdaye.mysplash.common.i.presenter.FollowingPresenter;
import com.wangdaye.mysplash.common.i.view.FollowingView;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.FollowingAdapter;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;

import java.util.List;

import retrofit2.Call;

/**
 * Following implementor.
 *
 * */

public class FollowingImplementor implements FollowingPresenter {

    private FollowingModel model;
    private FollowingView view;

    private OnRequestFollowingFeedListener listener;

    public FollowingImplementor(FollowingModel model, FollowingView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void requestFollowingFeed(Context c, int page, boolean refresh) {
        if (!model.isRefreshing() && !model.isLoading()) {
            if (refresh) {
                model.setRefreshing(true);
            } else {
                model.setLoading(true);
            }
            page = Math.max(1, refresh ? 1 : page + 1);
            listener = new OnRequestFollowingFeedListener(c, page, refresh);
            model.getService()
                    .requestFollowingFeed(
                            page,
                            Mysplash.DEFAULT_PER_PAGE,
                            listener);
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
        requestFollowingFeed(c, model.getPhotosPage(), true);
    }

    @Override
    public void loadMore(Context c, boolean notify) {
        if (notify) {
            view.setLoading(true);
        }
        requestFollowingFeed(c, model.getPhotosPage(), false);
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
    public void setPage(int page) {
        model.setPhotosPage(page);
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
    public int getAdapterItemCount() {
        return model.getAdapter().getRealItemCount();
    }

    @Override
    public FollowingAdapter getAdapter() {
        return model.getAdapter();
    }

    // interface.

    private class OnRequestFollowingFeedListener implements FeedService.OnRequestFeedPhotoListener {

        private Context c;
        private int page;
        private boolean refresh;
        private boolean canceled;

        OnRequestFollowingFeedListener(Context c, int page, boolean refresh) {
            this.c = c;
            this.page = page;
            this.refresh = refresh;
            this.canceled = false;
        }

        public void cancel() {
            canceled = true;
        }

        @Override
        public void onRequestFeedPhotoSuccess(Call<List<Photo>> call, retrofit2.Response<List<Photo>> response) {
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

            if (response.isSuccessful()
                    && model.getAdapter().getRealItemCount() + response.body().size() > 0) {
                model.setPhotosPage(page);
                if (refresh) {
                    model.getAdapter().clearItem();
                    setOver(false);
                }
                model.getAdapter().insertItems(response.body());
                if (response.body().size() < Mysplash.DEFAULT_PER_PAGE) {
                    setOver(true);
                }
                view.requestFollowingFeedSuccess();
            } else {
                view.requestFollowingFeedFailed(c.getString(R.string.feedback_load_nothing_tv));
            }
        }

        @Override
        public void onRequestFeedPhotoFailed(Call<List<Photo>> call, Throwable t) {
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
                    c.getString(R.string.feedback_load_failed_toast)
                            + " (" + t.getMessage() + ")");
            view.requestFollowingFeedFailed(c.getString(R.string.feedback_load_failed_tv));
        }
    }
}
