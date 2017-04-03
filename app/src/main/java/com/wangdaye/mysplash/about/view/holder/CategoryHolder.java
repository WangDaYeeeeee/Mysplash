package com.wangdaye.mysplash.about.view.holder;

import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.i.model.AboutModel;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.AboutAdapter;
import com.wangdaye.mysplash.about.model.CategoryObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Category holder.
 *
 * This ViewHolder is used to show category for {@link AboutAdapter}.
 *
 * */

public class CategoryHolder extends AboutAdapter.ViewHolder {
    // widget
    @BindView(R.id.item_about_category_title) TextView text;

    /** <br> life cycle. */

    public CategoryHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /** <br> UI. */

    @Override
    protected void onBindView(MysplashActivity a, AboutModel model) {
        text.setText(((CategoryObject) model).category);
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }
}
