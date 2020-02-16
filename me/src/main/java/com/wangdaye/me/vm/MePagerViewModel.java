package com.wangdaye.me.vm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.base.resource.ListResource;

public interface MePagerViewModel<T> {

    boolean init(@NonNull ListResource<T> resource);

    @Nullable
    String getUsername();

    void setUsername(@Nullable String username);
}
