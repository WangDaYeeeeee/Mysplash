package com.wangdaye.search.ui;

import android.annotation.SuppressLint;

import com.wangdaye.base.i.PagerManageView;
import com.wangdaye.common.base.adapter.footerAdapter.FooterAdapter;
import com.wangdaye.common.utils.helper.RecyclerViewHelper;
import com.wangdaye.search.R;
import com.wangdaye.search.SearchActivity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("ViewConstructor")
public class UserSearchPageView extends AbstractSearchPageView {

    public UserSearchPageView(SearchActivity a, FooterAdapter adapter,
                              boolean selected, int index, PagerManageView v) {
        super(a, adapter, selected, index, v);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(
                getContext(),
                RecyclerViewHelper.getGirdColumnCount(getContext())
        );
    }

    @Override
    protected String getInitFeedbackText() {
        return getContext().getString(R.string.feedback_search_users_tv);
    }
}
