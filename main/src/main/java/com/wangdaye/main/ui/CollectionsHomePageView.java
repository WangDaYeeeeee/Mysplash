package com.wangdaye.main.ui;

import android.annotation.SuppressLint;

import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.main.MainActivity;
import com.wangdaye.main.R;
import com.wangdaye.base.i.PagerManageView;
import com.wangdaye.common.utils.helper.RecyclerViewHelper;

@SuppressLint("ViewConstructor")
public class CollectionsHomePageView extends AbstractHomePageView {

    public CollectionsHomePageView(MainActivity a, BaseAdapter adapter,
                                   boolean selected, int index, PagerManageView v) {
        super(a, adapter, selected, index, v);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return RecyclerViewHelper.getDefaultStaggeredGridLayoutManager(getContext());
    }

    @Override
    protected String getFeedbackText() {
        return getContext().getString(R.string.feedback_load_failed_tv);
    }

    @Override
    protected String getFeedbackButton() {
        return getContext().getString(R.string.feedback_click_retry);
    }
}