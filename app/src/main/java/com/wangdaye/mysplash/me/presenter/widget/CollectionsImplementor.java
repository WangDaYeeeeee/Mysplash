package com.wangdaye.mysplash.me.presenter.widget;

import android.content.Context;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.data.Collection;
import com.wangdaye.mysplash._common.data.service.CollectionService;
import com.wangdaye.mysplash._common.data.tools.AuthManager;
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
        if (!model.isLoading() && AuthManager.getInstance().getMe() != null) {
            model.setLoading(true);
            page = refresh ? 1 : page + 1;
            model.getService()
                    .requestUserCollections(
                            AuthManager.getInstance().getMe(),
                            page,
                            Mysplash.DEFAULT_PER_PAGE,
                            new OnRequestCollectionsListener(c, page, refresh));
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
        return !model.isLoading();
    }

    @Override
    public boolean canLoadMore() {
        return !model.isLoading() && !model.isOver();
    }

    @Override
    public void setType(String key) {
        // do nothing.
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
            if (response.isSuccessful()) {
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
