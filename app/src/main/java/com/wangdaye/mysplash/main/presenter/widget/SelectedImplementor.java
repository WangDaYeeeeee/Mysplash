package com.wangdaye.mysplash.main.presenter.widget;

import android.content.Context;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.service.CollectionService;
import com.wangdaye.mysplash.common.i.model.SelectedModel;
import com.wangdaye.mysplash.common.i.presenter.SelectedPresenter;
import com.wangdaye.mysplash.common.i.view.SelectedView;
import com.wangdaye.mysplash.common.ui.adapter.SelectedAdapter;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Collections implementor.
 *
 * */

public class SelectedImplementor
        implements SelectedPresenter {

    private SelectedModel model;
    private SelectedView view;

    private OnRequestCollectionsListener listener;

    public SelectedImplementor(SelectedModel model, SelectedView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void requestCollections(Context c, int page, boolean refresh) {
        if (!model.isRefreshing() && !model.isLoading()) {
            if (refresh) {
                model.setRefreshing(true);
            } else {
                model.setLoading(true);
            }
            page = Math.max(1, refresh ? 1 : page + 1);
            listener = new OnRequestCollectionsListener(c, page, refresh);
            model.getService()
                    .requestUserCollections(
                            "unsplash",
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
            view.setRefreshingCollection(true);
        }
        requestCollections(c, model.getCollectionsPage(), true);
    }

    @Override
    public void loadMore(Context c, boolean notify) {
        if (notify) {
            view.setLoadingCollection(true);
        }
        requestCollections(c, model.getCollectionsPage(), false);
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
        model.setCollectionsPage(page);
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
    public SelectedAdapter getAdapter() {
        return model.getAdapter();
    }

    // interface.

    private class OnRequestCollectionsListener implements CollectionService.OnRequestCollectionsListener {

        private Context c;
        private int page;
        private boolean refresh;
        private boolean canceled;

        OnRequestCollectionsListener(Context c, int page, boolean refresh) {
            this.c = c;
            this.page = page;
            this.refresh = refresh;
            this.canceled = false;
        }

        public void cancel() {
            canceled = true;
        }

        @Override
        public void onRequestCollectionsSuccess(Call<List<Collection>> call, Response<List<Collection>> response) {
            if (canceled) {
                return;
            }

            model.setRefreshing(false);
            model.setLoading(false);
            if (refresh) {
                view.setRefreshingCollection(false);
            } else {
                view.setLoadingCollection(false);
            }
            if (response.isSuccessful()
                    && model.getAdapter().getRealItemCount() + response.body().size() > 0) {
                model.setCollectionsPage(page);
                if (refresh) {
                    model.getAdapter().clearItem();
                    setOver(false);
                }
                for (int i = 0; i < response.body().size(); i ++) {
                    model.getAdapter().insertItem(response.body().get(i), model.getAdapter().getRealItemCount());
                }
                if (response.body().size() < Mysplash.DEFAULT_PER_PAGE) {
                    setOver(true);
                }
                view.requestCollectionsSuccess();
            } else {
                view.requestCollectionsFailed(c.getString(R.string.feedback_load_nothing_tv));
            }
        }

        @Override
        public void onRequestCollectionsFailed(Call<List<Collection>> call, Throwable t) {
            if (canceled) {
                return;
            }
            model.setRefreshing(false);
            model.setLoading(false);
            if (refresh) {
                view.setRefreshingCollection(false);
            } else {
                view.setLoadingCollection(false);
            }
            NotificationHelper.showSnackbar(
                    c.getString(R.string.feedback_load_failed_toast)
                            + " (" + t.getMessage() + ")");
            view.requestCollectionsFailed(c.getString(R.string.feedback_load_failed_tv));
        }
    }
}
