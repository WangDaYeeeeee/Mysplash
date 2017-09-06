package com.wangdaye.mysplash.main.presenter.widget;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.common.i.model.MultiFilterModel;
import com.wangdaye.mysplash.common.i.presenter.MultiFilterPresenter;
import com.wangdaye.mysplash.common.i.view.MultiFilterView;
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Multi-filter implementor.
 *
 * */

public class MultiFilterImplementor
        implements MultiFilterPresenter {

    private MultiFilterModel model;
    private MultiFilterView view;

    private OnRequestPhotosListener listener;

    public MultiFilterImplementor(MultiFilterModel model, MultiFilterView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void requestPhotos(Context c, boolean refresh) {
        if (!model.isRefreshing() && !model.isLoading()) {
            if (refresh) {
                model.setRefreshing(true);
            } else {
                model.setLoading(true);
            }
            listener = new OnRequestPhotosListener(c, refresh);
            Integer category = model.getCategory() == 0 ? null : model.getCategory();
            Boolean featured = !model.isFeatured() ? null : true;
            String username = TextUtils.isEmpty(model.getUsername()) ? null : model.getUsername();
            String query = TextUtils.isEmpty(model.getQuery()) ? null : model.getQuery();
            String orientation = TextUtils.isEmpty(model.getOrientation()) ? null : model.getOrientation();

            model.getService()
                    .requestRandomPhotos(
                            category, featured,
                            username, query,
                            orientation, listener);
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
        requestPhotos(c, true);
    }

    @Override
    public void loadMore(Context c, boolean notify) {
        if (notify) {
            view.setLoading(true);
        }
        requestPhotos(c, false);
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
    public void setQuery(String query) {
        model.setQuery(query);
    }

    @Override
    public String getQuery() {
        return model.getQuery();
    }

    @Override
    public void setUsername(String username) {
        model.setUsername(username);
    }

    @Override
    public String getUsername() {
        return model.getUsername();
    }

    @Override
    public void setCategory(int c) {
        model.setCategory(c);
    }

    @Override
    public int getCategory() {
        return model.getCategory();
    }

    @Override
    public void setOrientation(String o) {
        model.setOrientation(o);
    }

    @Override
    public String getOrientation() {
        return model.getOrientation();
    }

    @Override
    public void setFeatured(boolean f) {
        model.setFeatured(f);
    }

    @Override
    public boolean isFeatured() {
        return model.isFeatured();
    }

    @Override
    public void setOver(boolean over) {
        model.setOver(over);
        view.setPermitLoading(!over);
    }

    @Override
    public int getAdapterItemCount() {
        return model.getAdapter().getRealItemCount();
    }

    @Override
    public void setActivityForAdapter(MysplashActivity a) {
        model.getAdapter().setActivity(a);
    }

    @Override
    public PhotoAdapter getAdapter() {
        return model.getAdapter();
    }

    // interface.

    private class OnRequestPhotosListener implements PhotoService.OnRequestPhotosListener {

        private Context c;
        private boolean refresh;
        private boolean canceled;

        OnRequestPhotosListener(Context c, boolean refresh) {
            this.c = c;
            this.refresh = refresh;
            this.canceled = false;
        }

        public void cancel() {
            this.canceled = true;
        }

        @Override
        public void onRequestPhotosSuccess(Call<List<Photo>> call, Response<List<Photo>> response) {
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
                    && response.body() != null
                    && model.getAdapter().getRealItemCount() + response.body().size() > 0) {
                if (refresh) {
                    model.getAdapter().clearItem();
                    setOver(false);
                }
                for (int i = 0; i < response.body().size(); i ++) {
                    model.getAdapter().insertItem(response.body().get(i));
                }
                if (response.body().size() < Mysplash.DEFAULT_PER_PAGE) {
                    setOver(true);
                }
                view.requestPhotosSuccess();
            } else {
                view.requestPhotosFailed(c.getString(R.string.feedback_search_nothing_tv));
            }
        }

        @Override
        public void onRequestPhotosFailed(Call<List<Photo>> call, Throwable t) {
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
