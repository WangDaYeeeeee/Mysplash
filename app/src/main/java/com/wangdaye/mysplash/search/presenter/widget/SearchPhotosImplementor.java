package com.wangdaye.mysplash.search.presenter.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.SearchPhotosResult;
import com.wangdaye.mysplash.common.data.service.network.SearchService;
import com.wangdaye.mysplash.common.i.model.SearchModel;
import com.wangdaye.mysplash.common.i.presenter.SearchPresenter;
import com.wangdaye.mysplash.common.i.view.SearchView;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Search photos implementor.
 *
 * */

public class SearchPhotosImplementor
        implements SearchPresenter {

    private SearchModel model;
    private SearchView view;

    private OnRequestPhotosListener listener;

    public SearchPhotosImplementor(SearchModel model, SearchView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void requestPhotos(Context c, int page, boolean refresh) {
        if (!model.isRefreshing() && !model.isLoading()) {
            if (refresh) {
                model.setRefreshing(true);
            } else {
                model.setLoading(true);
            }
            page = Math.max(1, refresh ? 1 : page + 1);
            listener = new OnRequestPhotosListener(c, page, refresh);
            model.getService()
                    .searchPhotos(
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
            view.setRefreshingSearchItem(true);
        }
        requestPhotos(c, model.getPhotosPage(), true);
    }

    @Override
    public void loadMore(Context c, boolean notify) {
        if (notify) {
            view.setLoadingSearchItem(true);
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
    public void setPage(int page) {
        model.setPhotosPage(page);
    }

    @Override
    public void setOver(boolean over) {
        model.setOver(over);
        view.setPermitLoading(!over);
    }

    @Override
    public int getAdapterItemCount() {
        return ((PhotoAdapter) model.getAdapter()).getRealItemCount();
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return model.getAdapter();
    }

    // interface.

    private class OnRequestPhotosListener implements SearchService.OnRequestPhotosListener {

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
        public void onRequestPhotosSuccess(Call<SearchPhotosResult> call, Response<SearchPhotosResult> response) {
            if (canceled) {
                return;
            }

            PhotoAdapter adapter = (PhotoAdapter) model.getAdapter();

            model.setRefreshing(false);
            model.setLoading(false);
            if (refresh) {
                view.setRefreshingSearchItem(false);
            } else {
                view.setLoadingSearchItem(false);
            }
            if (response.isSuccessful()
                    && response.body() != null
                    && response.body().results != null
                    && adapter.getRealItemCount() + response.body().results.size() > 0) {
                model.setPhotosPage(page);
                if (refresh) {
                    adapter.clearItem();
                    setOver(false);
                }
                for (int i = 0; i < response.body().results.size(); i ++) {
                    adapter.insertItem(response.body().results.get(i));
                }
                if (response.body().results.size() < Mysplash.DEFAULT_PER_PAGE) {
                    setOver(true);
                }
                view.searchSuccess();
            } else {
                view.searchFailed(c.getString(R.string.feedback_search_nothing_tv));
            }
        }

        @Override
        public void onRequestPhotosFailed(Call<SearchPhotosResult> call, Throwable t) {
            if (canceled) {
                return;
            }
            model.setRefreshing(false);
            model.setLoading(false);
            if (refresh) {
                view.setRefreshingSearchItem(false);
            } else {
                view.setLoadingSearchItem(false);
            }
            Toast.makeText(
                    c,
                    c.getString(R.string.feedback_search_failed_toast) + "\n" + t.getMessage(),
                    Toast.LENGTH_SHORT).show();
            view.searchFailed(c.getString(R.string.feedback_search_failed_tv));
        }
    }
}
