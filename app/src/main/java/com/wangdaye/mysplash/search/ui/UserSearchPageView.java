package com.wangdaye.mysplash.search.ui;

import android.annotation.SuppressLint;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.utils.helper.RecyclerViewHelper;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("ViewConstructor")
public class UserSearchPageView extends AbstractSearchPageView {

    public UserSearchPageView(SearchActivity a, int id, FooterAdapter adapter,
                              boolean selected, int index, PagerManageView v) {
        super(a, id, adapter, selected, index, v);
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
