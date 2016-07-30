package com.wangdaye.mysplash.main.model.widget;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.api.PhotoApi;
import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.main.adapter.PhotosAdapter;
import com.wangdaye.mysplash.main.model.widget.i.PhotoStateModel;

import java.util.List;

/**
 * Photo state object.
 * */

public class PhotoStateObject
        implements PhotoStateModel {
    // data
    private PhotosAdapter adapter;
    private PhotoService service;

    private String order = PhotoApi.ORDER_BY_LATEST;
    private boolean normalMode = true;
    private int categoryId = Mysplash.CATEGORY_BUILDINGS_ID;

    private boolean loadingData = false;
    private boolean loadFinish = false;

    private int page = 0;
    private List<Integer> pageList;

    private String searchQuery = "";
    private String searchOrientation = PhotoApi.LANDSCAPE_ORIENTATION;

    /** <br> life cycle. */

    public PhotoStateObject() {
        this.service = PhotoService.getService().buildClient();
    }

    /** <br> model. */

    // adapter.

    @Override
    public PhotosAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void setAdapter(PhotosAdapter a) {
        this.adapter = a;
    }

    // photo service.

    @Override
    public PhotoService getPhotoService() {
        return service;
    }

    @Override
    public void cancelRequest() {
        service.cancel();
    }

    // order.

    @Override
    public String getOrder() {
        return order;
    }

    @Override
    public void setOrder(String o) {
        this.order = o;
    }

    // mode.

    @Override
    public boolean isNormalMode() {
        return normalMode;
    }

    @Override
    public void setNormalMode(boolean normal) {
        this.normalMode = normal;
    }

    // loading data.

    @Override
    public boolean isLoadingData() {
        return loadingData;
    }

    @Override
    public void setLoadingData(boolean loading) {
        this.loadingData = loading;
    }

    // load finish.

    @Override
    public boolean isLoadFinish() {
        return loadFinish;
    }

    @Override
    public void setLoadFinish(boolean finish) {
        this.loadFinish = finish;
    }

    // page.

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setPage(int p) {
        this.page = p;
    }

    // random page list.

    @Override
    public List<Integer> getPageList() {
        return pageList;
    }

    @Override
    public void setPageList(List<Integer> l) {
        this.pageList = l;
    }

    // category.

    @Override
    public int getCategoryId() {
        return categoryId;
    }

    @Override
    public void setCategoryId(int id) {
        this.categoryId = id;
    }

    // search query.

    @Override
    public String getSearchQuery() {
        return searchQuery;
    }

    @Override
    public void setSearchQuery(String q) {
        this.searchQuery = q;
    }

    // search orientation.

    @Override
    public String getOrientation() {
        return searchOrientation;
    }

    @Override
    public void setOrientation(String o) {
        this.searchOrientation = o;
    }
}
