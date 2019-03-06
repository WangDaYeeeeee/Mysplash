package com.wangdaye.mysplash.common.basic;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class DaggerViewModelFactory implements ViewModelProvider.Factory {
    private final Map<Class<? extends ViewModel>, Provider<ViewModel>> creators;

    @Inject
    public DaggerViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators) {
        this.creators = creators;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(@NotNull Class<T> modelClass) {
        Provider<? extends ViewModel> creator = creators.get(modelClass);
        if (creator == null) {
            for (Map.Entry<Class<? extends ViewModel>, Provider<ViewModel>> entry : creators.entrySet()) {
                if (modelClass.isAssignableFrom(entry.getKey())) {
                    creator = entry.getValue();
                    break;
                }
            }
        }
        if (creator == null) {
            throw new IllegalArgumentException("unknown model class " + modelClass);
        }
        try {
            return (T) creator.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
