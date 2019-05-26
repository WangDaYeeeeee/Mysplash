package com.wangdaye.mysplash.muzei;

import android.text.TextUtils;

import org.junit.Test;

import java.util.ArrayList;

public class SourceCollectionTest {

    @Test
    public void readCollections() {
        String idText = ",,23548,215488,345878adsd,125456,,,,,";
        ArrayList<Integer> collectionIdList = new ArrayList<>();
        String[] ids = idText.replaceAll("[^0-9,]", "").split(",");
        for (String id : ids) {
            if (!TextUtils.isEmpty(id)) {
                collectionIdList.add(Integer.parseInt(id));
            }
        }
        if (collectionIdList.size() == 0) {
            collectionIdList.add(864380);
        }
        for (int id : collectionIdList) {
            System.out.println(id);
        }
    }
}
