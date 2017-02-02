package com.wangdaye.mysplash.main.presenter.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.SearchCollectionsResult;
import com.wangdaye.mysplash._common.data.service.SearchService;
import com.wangdaye.mysplash._common.i.model.SearchModel;
import com.wangdaye.mysplash._common.i.presenter.SearchPresenter;
import com.wangdaye.mysplash._common.i.view.SearchView;
import com.wangdaye.mysplash._common.ui.adapter.CollectionAdapter;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Search collections implementor.
 * */

public class SearchCollectionsImplementor
        implements SearchPresenter {
    // model & view.
    private SearchModel model;
    private SearchView view;

    // data
    private OnRequestCollectionsListener listener;

    /** <br> life cycle. */

    public SearchCollectionsImplementor(SearchModel model, SearchView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void requestPhotos(Context c, int page, boolean refresh) {
        if (!model.isRefreshing() && !model.isLoading()) {
            if (refresh) {
                model.setRefreshing(true);
            } else {
                model.setLoading(true);
            }
            page = refresh ? 1 : page + 1;
            listener = new OnRequestCollectionsListener(c, page, refresh);
            model.getService()
                    .searchCollections(
                            model.getSearchQuery(),
                            page,
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
    public void setQuery(String key) {
        model.setSearchQuery(key);
    }

    @Override
    public String getQuery() {
        return model.getSearchQuery();
    }

    @Override
    public int getAdapterItemCount() {
        return ((CollectionAdapter) model.getAdapter()).getRealItemCount();
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return model.getAdapter();
    }

    /** >br> interface. */

    private class OnRequestCollectionsListener implements SearchService.OnRequestCollectionsListener {
        // data
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
        public void onRequestCollectionsSuccess(Call<SearchCollectionsResult> call, Response<SearchCollectionsResult> response) {
            if (canceled) {
                return;
            }

            CollectionAdapter adapter = (CollectionAdapter) model.getAdapter();

            model.setRefreshing(false);
            model.setLoading(false);
            if (refresh) {
                adapter.clearItem();
                model.setOver(false);
                view.setRefreshing(false);
                view.setPermitLoading(true);
            } else {
                view.setLoading(false);
            }
            if (response.isSuccessful()
                    && response.body() != null
                    && response.body().results != null
                    && adapter.getRealItemCount() + response.body().results.size() > 0) {
                model.setPhotosPage(page);
                for (int i = 0; i < response.body().results.size(); i ++) {
                    adapter.insertItem(
                            response.body().results.get(i),
                            adapter.getRealItemCount());
                }
                if (response.body().results.size() < Mysplash.DEFAULT_PER_PAGE) {
                    model.setOver(true);
                    view.setPermitLoading(false);
                }
                view.requestPhotosSuccess();
            } else {
                view.requestPhotosFailed(c.getString(R.string.feedback_search_nothing_tv));
            }
        }

        @Override
        public void onRequestCollectionsFailed(Call<SearchCollectionsResult> call, Throwable t) {
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
            Toast.makeText(
                    c,
                    c.getString(R.string.feedback_search_failed_toast) + "\n" + t.getMessage(),
                    Toast.LENGTH_SHORT).show();
            view.requestPhotosFailed(c.getString(R.string.feedback_search_failed_tv));
        }
    }
}
