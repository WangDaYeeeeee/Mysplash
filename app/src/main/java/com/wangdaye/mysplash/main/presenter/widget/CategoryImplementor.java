package com.wangdaye.mysplash.main.presenter.widget;

import android.content.Context;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.api.PhotoApi;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.common.i.model.CategoryModel;
import com.wangdaye.mysplash.common.i.presenter.CategoryPresenter;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.ValueUtils;
import com.wangdaye.mysplash.common.i.view.CategoryView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Category implementor.
 *
 * */

public class CategoryImplementor
        implements CategoryPresenter {

    private CategoryModel model;
    private CategoryView view;

    private OnRequestPhotosListener listener;

    public CategoryImplementor(CategoryModel model, CategoryView view) {
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
            switch (model.getPhotosOrder()) {
                case PhotoApi.ORDER_BY_LATEST:
                    requestPhotosInCategoryOrders(c, page, refresh);
                    break;

                default:
                    requestPhotosInCategoryRandom(c, page, refresh);
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
            view.setRefreshingCategory(true);
        }
        requestPhotos(c, model.getPhotosPage(), true);
    }

    @Override
    public void loadMore(Context c, boolean notify) {
        if (notify) {
            view.setLoadingCategory(true);
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
    public void setCategory(int key) {
        model.setPhotosCategory(key);
    }

    @Override
    public void setOrder(String key) {
        model.setPhotosOrder(key);
    }

    @Override
    public String getOrder() {
        return model.getPhotosOrder();
    }

    @Override
    public void setPage(int page) {
        model.setPhotosPage(page);
    }

    @Override
    public void setPageList(List<Integer> pageList) {
        model.setPageList(pageList);
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
    public PhotoAdapter getAdapter() {
        return model.getAdapter();
    }

    private void requestPhotosInCategoryOrders(Context c, int page, boolean refresh) {
        page = Math.max(1, refresh ? 1 : page + 1);
        listener = new OnRequestPhotosListener(c, page, refresh, false);
        model.getService()
                .requestPhotosInAGivenCategory(
                        model.getPhotosCategory(),
                        page,
                        Mysplash.DEFAULT_PER_PAGE,
                        listener);
    }

    private void requestPhotosInCategoryRandom(Context c, int page, boolean refresh) {
        if (refresh) {
            page = 0;
            model.setPageList(ValueUtils.getPageListByCategory(Mysplash.CATEGORY_TOTAL_NEW));
        }
        listener = new OnRequestPhotosListener(c, page, refresh, true);
        model.getService()
                .requestPhotosInAGivenCategory(
                        model.getPhotosCategory(),
                        model.getPageList().get(page),
                        Mysplash.DEFAULT_PER_PAGE,
                        listener);
    }

    // interface.

    private class OnRequestPhotosListener implements PhotoService.OnRequestPhotosListener {

        private Context c;
        private int page;
        private boolean refresh;
        private boolean random;
        private boolean canceled;

        OnRequestPhotosListener(Context c, int page, boolean refresh, boolean random) {
            this.c = c;
            this.page = page;
            this.refresh = refresh;
            this.random = random;
            this.canceled = false;
        }

        public void cancel() {
            canceled = true;
        }

        @Override
        public void onRequestPhotosSuccess(Call<List<Photo>> call, Response<List<Photo>> response) {
            if (canceled) {
                return;
            }

            model.setRefreshing(false);
            model.setLoading(false);
            if (refresh) {
                view.setRefreshingCategory(false);
            } else {
                view.setLoadingCategory(false);
            }
            if (response.isSuccessful()
                    && model.getAdapter().getRealItemCount() + response.body().size() > 0) {
                if (random) {
                    model.setPhotosPage(page + 1);
                } else {
                    model.setPhotosPage(page);
                }
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
                view.requestPhotosFailed(c.getString(R.string.feedback_load_nothing_tv));
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
                view.setRefreshingCategory(false);
            } else {
                view.setLoadingCategory(false);
            }
            NotificationHelper.showSnackbar(
                    c.getString(R.string.feedback_load_failed_toast)
                            + " (" + t.getMessage() + ")");
            view.requestPhotosFailed(c.getString(R.string.feedback_load_failed_tv));
        }
    }
}