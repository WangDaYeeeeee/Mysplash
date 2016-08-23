package com.wangdaye.mysplash._common.data.data;

import com.google.gson.annotations.SerializedName;

/**
 * Add photo to collection result.
 * */

public class AddPhotoToCollectionResult {
    public Photo photo;
    public Collection collection;
    @SerializedName("user")
    public Me me;
}
