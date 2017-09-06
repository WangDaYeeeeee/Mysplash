package com.wangdaye.mysplash.main.presenter.widget;

import android.content.Context;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.service.CollectionService;
import com.wangdaye.mysplash.common.i.model.CollectionsModel;
import com.wangdaye.mysplash.common.i.presenter.CollectionsPresenter;
import com.wangdaye.mysplash.common.i.view.CollectionsView;
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.CollectionAdapter;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Collections implementor.
 *
 * */

public class CollectionsImplementor
        implements CollectionsPresenter {

    private CollectionsModel model;
    private CollectionsView view;

    private OnRequestCollectionsListener listener;

    public CollectionsImplementor(CollectionsModel model, CollectionsView view) {
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
            switch (model.getCollectionsType()) {
                case Mysplash.COLLECTION_TYPE_FEATURED:
                    requestFeaturedCollections(c, page, refresh);
                    break;

                case Mysplash.COLLECTION_TYPE_ALL:
                    requestAllCollections(c, page, refresh);
                    break;

                case Mysplash.COLLECTION_TYPE_CURATED:
                    requestCuratedCollections(c, page, refresh);
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
        requestCollections(c, model.getCollectionsPage(), true);
    }

    @Override
    public void loadMore(Context c, boolean notify) {
        if (notify) {
            view.setLoading(true);
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
    public Object getRequestKey() {
        return null;
    }

    @Override
    public void setRequestKey(Object k) {
        // do nothing.
    }

    @Override
    public void setType(int key) {
        model.setCollectionsType(key);
    }

    @Override
    public int getType() {
        return model.getCollectionsType();
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
    public CollectionAdapter getAdapter() {
        return model.getAdapter();
    }

    private void requestAllCollections(Context c, int page, boolean refresh) {
        listener = new OnRequestCollectionsListener(c, page, refresh);
        model.getService()
                .requestAllCollections(
                        Math.max(1, refresh ? 1 : page + 1),
                        Mysplash.DEFAULT_PER_PAGE,
                        listener);
    }

    private void requestCuratedCollections(Context c, int page, boolean refresh) {
        page = Math.max(1, refresh ? 1 : page + 1);
        listener = new OnRequestCollectionsListener(c, page, refresh);
        model.getService()
                .requestCuratedCollections(
                        page,
                        Mysplash.DEFAULT_PER_PAGE,
                        listener);
    }

    private void requestFeaturedCollections(Context c, int page, boolean refresh) {
        page = Math.max(1, refresh ? 1 : page + 1);
        listener = new OnRequestCollectionsListener(c, page, refresh);
        model.getService()
                .requestFeaturedCollections(
                        page,
                        Mysplash.DEFAULT_PER_PAGE,
                        listener);
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
                view.setRefreshing(false);
            } else {
                view.setLoading(false);
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
                view.setRefreshing(false);
            } else {
                view.setLoading(false);
            }
            NotificationHelper.showSnackbar(
                    c.getString(R.string.feedback_load_failed_toast)
                            + " (" + t.getMessage() + ")");
            view.requestCollectionsFailed(c.getString(R.string.feedback_load_failed_tv));
        }
    }
}
