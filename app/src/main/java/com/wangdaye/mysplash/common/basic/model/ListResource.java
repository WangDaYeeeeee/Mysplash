package com.wangdaye.mysplash.common.basic.model;

import java.util.List;

import androidx.annotation.NonNull;

public class ListResource<T> {

    @NonNull public final List<T> dataList;
    @NonNull public final Status status;
    public final int dataPage;
    public final int perPage;
    public final int increase;

    public enum Status {
        REFRESH_SUCCESS, LOAD_SUCCESS, ALL_LOADED,
        REFRESH_ERROR, LOAD_ERROR,
        REFRESHING, LOADING
    }

    private ListResource(@NonNull List<T> dataList, @NonNull Status status,
                         int dataPage, int perPage, int increase) {
        this.dataList = dataList;
        this.status = status;
        this.dataPage = dataPage;
        this.perPage = perPage;
        this.increase = increase;
    }

    // refresh success.

    public static <T> ListResource<T> refreshSuccess(@NonNull ListResource<T> current, @NonNull List<T> newList) {
        List<T> list = current.dataList;
        list.addAll(newList);
        return refreshSuccess(list, 1, current.perPage, newList.size());
    }

    public static <T> ListResource<T> refreshSuccess(@NonNull List<T> dataList,
                                                     int dataPage, int perPage, int increase) {
        return new ListResource<>(dataList, Status.REFRESH_SUCCESS, dataPage, perPage, increase);
    }

    // load success.

    public static <T> ListResource<T> loadSuccess(@NonNull ListResource<T> current, @NonNull List<T> newList) {
        List<T> list = current.dataList;
        list.addAll(newList);
        return loadSuccess(list, current.dataPage + 1, current.perPage, newList.size());
    }

    public static <T> ListResource<T> loadSuccess(@NonNull List<T> dataList,
                                                  int dataPage, int perPage, int increase) {
        return new ListResource<>(dataList, Status.LOAD_SUCCESS, dataPage, perPage, increase);
    }

    // all loaded.

    public static <T> ListResource<T> allLoaded(@NonNull ListResource<T> current, @NonNull List<T> newList) {
        List<T> list = current.dataList;
        list.addAll(newList);
        return allLoaded(list, current.dataPage + 1, current.perPage, newList.size());
    }

    public static <T> ListResource<T> allLoaded(@NonNull List<T> dataList,
                                                int dataPage, int perPage, int increase) {
        return new ListResource<>(dataList, Status.ALL_LOADED, dataPage, perPage, increase);
    }

    // refresh error.

    public static <T> ListResource<T> refreshError(@NonNull ListResource<T> current) {
        return refreshError(current.dataList, current.dataPage, current.perPage);
    }

    public static <T> ListResource<T> refreshError(@NonNull List<T> dataList,
                                                   int dataPage, int perPage) {
        return new ListResource<>(dataList, Status.REFRESH_ERROR, dataPage, perPage, 0);
    }

    // load error.

    public static <T> ListResource<T> loadError(@NonNull ListResource<T> current) {
        return loadError(current.dataList, current.dataPage, current.perPage);
    }

    public static <T> ListResource<T> loadError(@NonNull List<T> dataList,
                                                int dataPage, int perPage) {
        return new ListResource<>(dataList, Status.LOAD_ERROR, dataPage, perPage, 0);
    }

    // refreshing.

    public static <T> ListResource<T> refreshing(@NonNull ListResource<T> current) {
        return refreshing(current.dataList, current.dataPage, current.perPage);
    }

    public static <T> ListResource<T> refreshing(@NonNull List<T> dataList,
                                                 int dataPage, int perPage) {
        return new ListResource<>(dataList, Status.REFRESHING, dataPage, perPage, 0);
    }

    // loading.

    public static <T> ListResource<T> loading(@NonNull ListResource<T> current) {
        return loading(current.dataList, current.dataPage, current.perPage);
    }

    public static <T> ListResource<T> loading(@NonNull List<T> dataList,
                                              int dataPage, int perPage) {
        return new ListResource<>(dataList, Status.LOADING, dataPage, perPage, 0);
    }
}
