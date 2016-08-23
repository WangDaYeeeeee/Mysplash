package com.wangdaye.mysplash.main.presenter.widget;

import android.content.Context;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.data.Collection;
import com.wangdaye.mysplash._common.data.service.CollectionService;
import com.wangdaye.mysplash._common.i.model.CollectionsModel;
import com.wangdaye.mysplash._common.i.presenter.CollectionsPresenter;
import com.wangdaye.mysplash._common.i.view.CollectionsView;
import com.wangdaye.mysplash._common.ui.toast.MaterialToast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Collections implementor.
 * */

public class CollectionsImplementor
        implements CollectionsPresenter {
    // model & view.
    private CollectionsModel model;
    private CollectionsView view;

    /** <br> life cycle. */

    public CollectionsImplementor(CollectionsModel model, CollectionsView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void requestCollections(Context c, int page, boolean refresh) {
        if (!model.isLoading()) {
            model.setLoading(true);
            final String[] types = c
                    .getResources()
                    .getStringArray(R.array.collection_type_values);
            if (model.getCollectionsType().equals(types[0])) {
                requestAllCollections(c, page, refresh);
            } else if (model.getCollectionsType().equals(types[1])) {
                requestCuratedCollections(c, page, refresh);
            } else {
                requestFeaturedCollections(c, page, refresh);
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
    public void setType(String key) {
        model.setCollectionsType(key);
    }

    /** <br> utils. */

    private void requestAllCollections(Context c, int page, boolean refresh) {
        page = refresh ? 1 : page + 1;
        model.getService()
                .requestAllCollections(
                        page,
                        Mysplash.DEFAULT_PER_PAGE,
                        new OnRequestCollectionsListener(c, page, refresh));
    }

    private void requestCuratedCollections(Context c, int page, boolean refresh) {
        page = refresh ? 1 : page + 1;
        model.getService()
                .requestCuratedCollections(
                        page,
                        Mysplash.DEFAULT_PER_PAGE,
                        new OnRequestCollectionsListener(c, page, refresh));
    }

    private void requestFeaturedCollections(Context c, int page, boolean refresh) {
        page = refresh ? 1 : page + 1;
        model.getService()
                .requestFeaturedCollections(
                        page,
                        Mysplash.DEFAULT_PER_PAGE,
                        new OnRequestCollectionsListener(c, page, refresh));
    }

    /** <br> interface. */

    private class OnRequestCollectionsListener implements CollectionService.OnRequestCollectionsListener {
        // data
        private Context c;
        private int page;
        private boolean refresh;

        public OnRequestCollectionsListener(Context c, int page, boolean refresh) {
            this.c = c;
            this.page = page;
            this.refresh = refresh;
        }

        @Override
        public void onRequestCollectionsSuccess(Call<List<Collection>> call, Response<List<Collection>> response) {
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
                model.setCollectionsPage(page);
                for (int i = 0; i < response.body().size(); i ++) {
                    model.getAdapter().insertItem(response.body().get(i), model.getAdapter().getRealItemCount());
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
                view.requestCollectionsSuccess();
            } else {
                view.requestCollectionsFailed(c.getString(R.string.feedback_load_nothing_tv));
            }
        }

        @Override
        public void onRequestCollectionsFailed(Call<List<Collection>> call, Throwable t) {
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
            view.requestCollectionsFailed(c.getString(R.string.feedback_load_failed_tv));
        }
    }
}
