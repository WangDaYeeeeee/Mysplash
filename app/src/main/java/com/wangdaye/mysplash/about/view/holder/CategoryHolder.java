package com.wangdaye.mysplash.about.view.holder;

import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.AboutModel;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.AboutAdapter;
import com.wangdaye.mysplash.about.model.CategoryAboutObject;

/**
 * Category holder.
 * */

public class CategoryHolder extends AboutAdapter.ViewHolder {
    // widget
    private TextView text;

    /** <br> life cycle. */

    public CategoryHolder(View itemView) {
        super(itemView);
        this.text = (TextView) itemView.findViewById(R.id.item_about_category_title);
    }

    /** <br> UI. */

    @Override
    protected void onBindView(MysplashActivity a, AboutModel model) {
        text.setText(((CategoryAboutObject) model).category);
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }
}
