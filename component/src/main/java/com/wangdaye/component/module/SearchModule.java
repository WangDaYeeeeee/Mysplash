package com.wangdaye.component.module;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;


public interface SearchModule {

    void startSearchActivity(Activity a, View background, @Nullable String query);

    void startSearchActivity(Activity a, @Nullable String query);

    Intent getSearchActivityIntentForShortcut();
}
