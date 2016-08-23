package com.wangdaye.mysplash.main.presenter.widget;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.api.PhotoApi;
import com.wangdaye.mysplash._common.data.data.Photo;
import com.wangdaye.mysplash._common.data.service.PhotoService;
import com.wangdaye.mysplash._common.i.model.CategoryModel;
import com.wangdaye.mysplash._common.i.presenter.CategoryPresenter;
import com.wangdaye.mysplash._common.ui.toast.MaterialToast;
import com.wangdaye.mysplash._common.utils.ValueUtils;
import com.wangdaye.mysplash._common.i.view.CategoryView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Category implementor.
 * */

public class CategoryImplementor
        implements CategoryPresenter {
    // model & view.
    private CategoryModel model;
    private CategoryView view;

    /** <br> life cycle. */

    public CategoryImplementor(CategoryModel model, CategoryView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void requestPhotos(int page, boolean refresh) {
        if (!model.isLoading()) {
            model.setLoading(true);
            switch (model.getPhotosOrder()) {
                case PhotoApi.ORDER_BY_LATEST:
                    requestPhotosInCategoryOrders(page, refresh);
                    break;

                default:
                    requestPhotosInCategoryRandom(page, refresh);
                    break;
            }
        }
    }

    @Override
    public void cancelRequest() {
        model.getService().cancel();
        model.getAdapter().cancelService();
    }

    @Override
    public void refreshNew(boolean notify) {
        if (notify) {
            view.setRefreshing(true);
        }
        requestPhotos(model.getPhotosPage(), true);
    }

    @Override
    public void loadMore(boolean notify) {
        if (notify) {
            view.setLoading(true);
        }
        requestPhotos(model.getPhotosPage(), false);
    }

    @Override
    public void initRefresh() {
        model.getService().cancel();
        model.setLoading(false);
        refreshNew(false);
        view.initRefreshStart();
    }

    @Override
    public boolean canLoadMore() {
        return !model.isLoading() && !model.isOver();
    }

    @Override
    public void setCategory(int key) {
        model.setPhotosCategory(key);
    }

    @Override
    public void setOrder(String key) {
        model.setPhotosOrder(key);
    }

    /** <br> utils. */

    private void requestPhotosInCategoryOrders(int page, boolean refresh) {
        page = refresh ? 1: page + 1;
        model.getService()
                .requestPhotosInAGivenCategory(
                        model.getPhotosCategory(),
                        page,
                        Mysplash.DEFAULT_PER_PAGE,
                        new OnRequestPhotosListener(page, model.getPhotosCategory(), refresh, false));
    }

    private void requestPhotosInCategoryRandom(int page, boolean refresh) {
        if (refresh) {
            page = 0;
            model.setPageList(ValueUtils.getPageListByCategory(Mysplash.CATEGORY_TOTAL_NEW));
        }
        model.getService()
                .requestPhotosInAGivenCategory(
                        model.getPhotosCategory(),
                        model.getPageList().get(page),
                        Mysplash.DEFAULT_PER_PAGE,
                        new OnRequestPhotosListener(page, model.getPhotosCategory(), refresh, true));
    }

    /** <br> interface. */

    private class OnRequestPhotosListener implements PhotoService.OnRequestPhotosListener {
        // data
        private int page;
        private int category;
        private boolean refresh;
        private boolean random;

        public OnRequestPhotosListener(int page, int category, boolean refresh, boolean random) {
            this.page = page;
            this.category = category;
            this.refresh = refresh;
            this.random = random;
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
                ValueUtils.writePhotoCount(Mysplash.getInstance(), response, category);
                if (random) {
                    model.setPhotosPage(page + 1);
                } else {
                    model.setPhotosPage(page);
                }
                for (int i = 0; i < response.body().size(); i ++) {
                    model.getAdapter().insertItem(response.body().get(i));
                }
                if (response.body().size() < Mysplash.DEFAULT_PER_PAGE) {
                    model.setOver(true);
                    view.setPermitLoading(false);
                    if (response.body().size() == 0) {
                        MaterialToast.makeText(
                                Mysplash.getInstance(),
                                Mysplash.getInstance().getString(R.string.feedback_is_over),
                                null,
                                MaterialToast.LENGTH_SHORT).show();
                    }
                }
                view.requestPhotosSuccess();
            } else {
                view.requestPhotosFailed(Mysplash.getInstance().getString(R.string.feedback_load_nothing_tv));
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
            MaterialToast.makeText(
                    Mysplash.getInstance(),
                    Mysplash.getInstance().getString(R.string.feedback_load_failed_toast) + " (" + t.getMessage() + ")",
                    null,
                    MaterialToast.LENGTH_SHORT).show();
            view.requestPhotosFailed(Mysplash.getInstance().getString(R.string.feedback_load_failed_tv));
        }
    }
}
