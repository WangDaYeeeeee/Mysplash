package com.wangdaye.mysplash.main.presenter.widget;

import android.content.Context;
import android.widget.Toast;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.data.Photo;
import com.wangdaye.mysplash._common.data.service.PhotoService;
import com.wangdaye.mysplash._common.i.model.SearchModel;
import com.wangdaye.mysplash._common.i.presenter.SearchPresenter;
import com.wangdaye.mysplash._common.i.view.SearchView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Search implementor.
 * */

public class SearchImplementor implements SearchPresenter {
    // model & view.
    private SearchModel model;
    private SearchView view;

    /** <br> life cycle. */

    public SearchImplementor(SearchModel model, SearchView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void requestPhotos(Context c, int page, boolean refresh) {
        if (!model.isLoading()) {
            model.setLoading(true);
            page = refresh ? 1 : page + 1;
            model.getService()
                    .searchPhotos(
                            model.getSearchQuery(),
                            model.getSearchOrientation(),
                            page,
                            Mysplash.DEFAULT_PER_PAGE,
                            new OnRequestPhotosListener(c, page, refresh));
        }
    }

    @Override
    public void cancelRequest() {
        model.getService().cancel();
        model.getAdapter().cancelService();
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
        model.getService().cancel();
        model.setLoading(false);
        refreshNew(c, false);
        view.setBackgroundOpacity();
        view.initRefreshStart();
    }

    @Override
    public boolean canLoadMore() {
        return !model.isLoading() && !model.isOver();
    }

    @Override
    public void setQuery(String key) {
        model.setSearchQuery(key);
    }

    @Override
    public void setOrientation(String key) {
        model.setSearchOrientation(key);
    }

    /** >br> interface. */

    private class OnRequestPhotosListener implements PhotoService.OnRequestPhotosListener {
        // data
        private Context c;
        private int page;
        private boolean refresh;

        public OnRequestPhotosListener(Context c, int page, boolean refresh) {
            this.c = c;
            this.page = page;
            this.refresh = refresh;
        }

        @Override
        public void onRequestPhotosSuccess(Call<List<Photo>> call, Response<List<Photo>> response) {
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
                model.setPhotosPage(page);
                for (int i = 0; i < response.body().size(); i ++) {
                    model.getAdapter().insertItem(response.body().get(i));
                }
                if (response.body().size() < Mysplash.DEFAULT_PER_PAGE) {
                    model.setOver(true);
                    view.setPermitLoading(false);
                    if (response.body().size() == 0) {
                        Toast.makeText(
                                c,
                                c.getString(R.string.feedback_is_over) + "\n" + response.message(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                view.requestPhotosSuccess();
            } else {
                view.showButton();
                view.requestPhotosFailed(c.getString(R.string.feedback_search_failed_tv));
            }
        }

        @Override
        public void onRequestPhotosFailed(Call<List<Photo>> call, Throwable t) {
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
            view.showButton();
            view.requestPhotosFailed(c.getString(R.string.feedback_load_failed_tv));
        }
    }
}
