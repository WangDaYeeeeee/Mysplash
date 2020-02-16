package com.wangdaye.user.vm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.base.resource.ListResource;

public interface UserPagerViewModel<T> {

    boolean init(@NonNull ListResource<T> resource, String defaultUsername);

    @Nullable
    String getUsername();

    void setUsername(@Nullable String username);
}
