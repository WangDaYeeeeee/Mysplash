package com.wangdaye.mysplash.main.presenter.widget;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.FollowingFeedResult;
import com.wangdaye.mysplash._common.data.service.FollowingService;
import com.wangdaye.mysplash._common.i.model.FollowingModel;
import com.wangdaye.mysplash._common.i.presenter.FollowingPresenter;
import com.wangdaye.mysplash._common.i.view.FollowingView;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.FollowingAdapter;
import com.wangdaye.mysplash._common.utils.NotificationUtils;

import retrofit2.Call;

/**
 * Following implementor.
 * */

public class FollowingImplementor implements FollowingPresenter {
    // model & view.
    private FollowingModel model;
    private FollowingView view;

    // data
    private OnRequestFollowingFeedListener listener;

    /** <br> life cycle. */

    public FollowingImplementor(FollowingModel model, FollowingView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

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
        model.getAdapter().cancelService();
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

    /** <br> utils. */

    private void requestFollowingFeed(Context c, String nextPage, boolean refresh) {
        listener = new OnRequestFollowingFeedListener(c, refresh);
        model.getService()
                .requestFollowingFeed(
                        refresh ? model.getFirstPage() : nextPage,
                        listener);
    }

    /** <br> interface. */

    private class OnRequestFollowingFeedListener implements FollowingService.OnRequestFollowingFeedListener {
        // data
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
        public void onRequestFollowingFeedSuccess(Call<FollowingFeedResult> call, retrofit2.Response<FollowingFeedResult> response) {
            if (canceled) {
                return;
            }

            model.setRefreshing(false);
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
                    && model.getAdapter().getRealItemCount() + response.body().results.size() > 0) {
                for (int i = 0; i < response.body().results.size(); i ++) {
                    model.getAdapter().insertItem(response.body().results.get(i));
                }
                if (TextUtils.isEmpty(response.body().next_page)) {
                    model.setOver(true);
                    view.setPermitLoading(false);
                    if (response.body().results.size() == 0) {
                        NotificationUtils.showSnackbar(
                                c.getString(R.string.feedback_is_over),
                                Snackbar.LENGTH_SHORT);
                    }
                }
                model.setNextPage(response.body().next_page);
                view.requestFollowingFeedSuccess();
            } else {
                view.requestFollowingFeedFailed(c.getString(R.string.feedback_load_nothing_tv));
            }
        }

        @Override
        public void onRequestFollowingFeedFailed(Call<FollowingFeedResult> call, Throwable t) {
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
            NotificationUtils.showSnackbar(
                    c.getString(R.string.feedback_load_failed_toast) + " (" + t.getMessage() + ")",
                    Snackbar.LENGTH_SHORT);
            Log.d("FOLLOWING", t.getMessage());
            view.requestFollowingFeedFailed(c.getString(R.string.feedback_load_failed_tv));
        }
    }
}
