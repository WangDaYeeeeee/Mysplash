package com.wangdaye.mysplash._common.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.PhotoDetails;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.List;

/**
 * Tag adapter.
 * */

public class TagAdapter extends com.zhy.view.flowlayout.TagAdapter<PhotoDetails.Categories> {

    /** <br> life cycle. */

    public TagAdapter(List<PhotoDetails.Categories> datas) {
        super(datas);
    }

    /** <br> UI. */

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(FlowLayout parent, int position, PhotoDetails.Categories categories) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        Button b = (Button) v.findViewById(R.id.item_tag);
        b.setText("#" + categories.title.toUpperCase());
        return v;
    }
}
