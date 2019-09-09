package com.wangdaye.base.resource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Resource<T> implements Cloneable {

    @Nullable public final T data;
    @NonNull public final Status status;

    public enum Status {
        SUCCESS, ERROR, LOADING
    }

    private Resource(@Nullable T data, @NonNull Status status) {
        this.data = data;
        this.status = status;
    }

    public Resource(@NonNull Resource<T> resource) {
        this.data = resource.data;
        this.status = resource.status;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(data, Status.SUCCESS);
    }

    public static <T> Resource<T> error(@Nullable T data) {
        return new Resource<>(data, Status.ERROR);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(data, Status.LOADING);
    }
}
