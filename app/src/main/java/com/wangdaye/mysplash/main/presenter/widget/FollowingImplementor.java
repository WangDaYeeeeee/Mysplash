package com.wangdaye.mysplash.main.presenter.widget;

import android.content.Context;
import android.text.TextUtils;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.FollowingFeed;
import com.wangdaye.mysplash.common.data.service.FeedService;
import com.wangdaye.mysplash.common.i.model.FollowingModel;
import com.wangdaye.mysplash.common.i.presenter.FollowingPresenter;
import com.wangdaye.mysplash.common.i.view.FollowingView;
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.FollowingAdapter;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;

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
    public void requestFollowingFeed(Context c, boolean refresh) {
        if (!model.isRefreshing() && !model.isLoading()) {
            if (refresh) {
                model.setRefreshing(true);
            } else {
                model.setLoading(true);
            }
            requestFollowingFeed(c, model.getNextPage(), refresh);
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
        requestFollowingFeed(c, true);
    }

    @Override
    public void loadMore(Context c, boolean notify) {
        if (notify) {
            view.setLoading(true);
        }
        requestFollowingFeed(c, false);
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
    public void setNextPage(String nextPage) {
        model.setNextPage(nextPage);
    }

    @Override
    public String getNextPage() {
        return model.getNextPage();
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

    private void requestFollowingFeed(Context c, String nextPage, boolean refresh) {
        listener = new OnRequestFollowingFeedListener(c, refresh);
        model.getService()
                .requestFollowingFeed(
                        refresh ? model.getFirstPage() : nextPage,
                        listener);
    }

    // interface.

    private class OnRequestFollowingFeedListener implements FeedService.OnRequestFollowingFeedListener {

        private Context c;
        private boolean refresh;
        private boolean canceled;

        OnRequestFollowingFeedListener(Context c, boolean refresh) {
            this.c = c;
            this.refresh = refresh;
            this.canceled = false;
        }

        public void cancel() {
            canceled = true;
        }

        @Override
        public void onRequestFollowingFeedSuccess(Call<FollowingFeed> call, retrofit2.Response<FollowingFeed> response) {
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
                    && model.getAdapter().getRealItemCount() + response.body().results.size() > 0) {
                model.setNextPage(response.body().next_page);
                if (refresh) {
                    model.getAdapter().clearItem();
                    setOver(false);
                }
                for (int i = 0; i < response.body().results.size(); i ++) {
                    model.getAdapter().insertItem(response.body().results.get(i));
                }
                if (TextUtils.isEmpty(response.body().next_page)) {
                    setOver(true);
                }
                view.requestFollowingFeedSuccess();
            } else {
                view.requestFollowingFeedFailed(c.getString(R.string.feedback_load_nothing_tv));
            }
        }

        @Override
        public void onRequestFollowingFeedFailed(Call<FollowingFeed> call, Throwable t) {
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
