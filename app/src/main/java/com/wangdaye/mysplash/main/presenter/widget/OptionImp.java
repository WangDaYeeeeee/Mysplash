package com.wangdaye.mysplash.main.presenter.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wangdaye.mysplash.main.model.widget.i.PhotoStateModel;
import com.wangdaye.mysplash.main.presenter.widget.i.DisplayStatePresenter;
import com.wangdaye.mysplash.main.presenter.widget.i.OptionPresenter;
import com.wangdaye.mysplash.main.presenter.widget.i.RequestDataPresenter;
import com.wangdaye.mysplash.main.view.widget.i.ContentView;
import com.wangdaye.mysplash.main.view.widget.i.LoadingView;
import com.wangdaye.mysplash.main.view.widget.i.PhotosView;

/**
 * Option feedback implementor.
 * */

public class OptionImp
        implements OptionPresenter {
    // data
    private int loadType;
    public static final int NORMAL_LOAD_TYPE = 0;
    public static final int CATEGORY_LOAD_TYPE = 1;
    public static final int SEARCH_LOAD_TYPE = -1;

    // model.
    private PhotoStateModel photoStateModel;

    // view.
    private ContentView contentView;
    private LoadingView loadingView;
    private PhotosView photosView;

    // presenter.
    private RequestDataPresenter requestDataPresenter;
    private DisplayStatePresenter displayStatePresenter;

    /** <br> life cycle. */

    public OptionImp(PhotoStateModel photoStateModel,
                     ContentView contentView, LoadingView loadingView, PhotosView photosView,
                     RequestDataPresenter requestDataPresenter, DisplayStatePresenter displayStatePresenter,
                     int loadType) {
        this.photoStateModel = photoStateModel;
        this.contentView = contentView;
        this.loadingView = loadingView;
        this.photosView = photosView;
        this.requestDataPresenter = requestDataPresenter;
        this.displayStatePresenter = displayStatePresenter;
        this.loadType = loadType;
    }

    /** <br> presenter. */

    // check.

    @Override
    public boolean checkNeedRefresh() {
        return photosView.checkNeedRefresh();
    }

    @Override
    public boolean checkNeedChangOrder(String order) {
        return photosView.checkNeedChangOrder(order);
    }

    // request data.

    @Override
    public void refreshNew(Context c) {
        photosView.setRefreshing(true);
        requestDataByType(c, true);
    }

    @Override
    public void loadMore(Context c) {
        photosView.setLoading(true);
        requestDataByType(c, false);
    }

    @Override
    public void initRefresh(Context c) {
        displayStatePresenter.setLoadingState();
        requestDataByType(c, true);
    }

    @Override
    public void doSearch(Context c, String query, String orientation) {
        requestDataPresenter.cancelRequest();
        photoStateModel.setSearchQuery(query);
        photoStateModel.setOrientation(orientation);
        photoStateModel.setLoadFinish(false);
        photosView.resetRefreshLayout();
        loadingView.showButton();
        contentView.setBackgroundOpacity();
        initRefresh(c);
    }

    @Override
    public void autoLoad(RecyclerView recyclerView, int dy) {
        int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        int totalItemCount = recyclerView.getAdapter().getItemCount();
        if (!photoStateModel.isLoadingData() && !photoStateModel.isLoadFinish()
                && lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0 && dy > 0) {
            requestDataByType(recyclerView.getContext(), false);
        }

        if (photoStateModel.isLoadingData() && totalItemCount > 0
                && ViewCompat.canScrollVertically(recyclerView, 1)) {
            photosView.setLoading(true);
        }
    }

    @Override
    public void cancelRequest() {
        photoStateModel.getAdapter().cancelService();
    }

    private void requestDataByType(Context c, boolean refresh) {
        switch (loadType) {
            case NORMAL_LOAD_TYPE:
                requestDataPresenter.requestPhotos(c, refresh);
                break;

            case CATEGORY_LOAD_TYPE:
                requestDataPresenter.requestPhotosInCategory(c, refresh);
                break;

            case SEARCH_LOAD_TYPE:
                requestDataPresenter.searchPhotos(c, refresh);
                break;
        }
    }
}
