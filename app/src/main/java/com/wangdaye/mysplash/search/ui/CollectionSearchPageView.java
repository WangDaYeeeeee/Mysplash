package com.wangdaye.mysplash.search.ui;

import android.annotation.SuppressLint;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.utils.helper.RecyclerViewHelper;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

@SuppressLint("ViewConstructor")
public class CollectionSearchPageView extends AbstractSearchPageView {

    public CollectionSearchPageView(SearchActivity a, int id, FooterAdapter adapter,
                                    boolean selected, int index, PagerManageView v) {
        super(a, id, adapter, selected, index, v);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new StaggeredGridLayoutManager(
                RecyclerViewHelper.getGirdColumnCount(getContext()),
                StaggeredGridLayoutManager.VERTICAL
        );
    }

    @Override
    protected String getInitFeedbackText() {
        return getContext().getString(R.string.feedback_search_collections_tv);
    }
}
