package com.wangdaye.common.ui.adapter.collection.mini;

import androidx.annotation.Nullable;

import com.wangdaye.base.unsplash.Collection;

import java.util.ArrayList;
import java.util.List;

public class ProgressCollection {

    public final @Nullable Collection collection;
    public final boolean progressing;

    public ProgressCollection(@Nullable Collection collection, boolean progressing) {
        this.collection = collection;
        this.progressing = progressing;
    }

    public static List<ProgressCollection> toProgressCollectionList(List<Collection> collectionList,
                                                                    List<Collection> editingList) {
        List<ProgressCollection> results = new ArrayList<>();
        for (Collection c : collectionList) {
            results.add(new ProgressCollection(c, isProgressing(c, editingList)));
        }
        return results;
    }

    private static boolean isProgressing(Collection collection, List<Collection> editingList) {
        for (Collection c : editingList) {
            if (c.id == collection.id) {
                return true;
            }
        }
        return false;
    }
}
