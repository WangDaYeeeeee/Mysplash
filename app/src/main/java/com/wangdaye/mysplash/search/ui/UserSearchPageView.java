package com.wangdaye.mysplash.search.ui;

import android.annotation.SuppressLint;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.ui.adapter.UserAdapter;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.DisplayUtils;

import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("ViewConstructor")
public class UserSearchPageView extends AbstractSearchPageView<User> {

    private UserAdapter userAdapter;

    public UserSearchPageView(SearchActivity a, int id, List<User> itemList,
                              boolean selected, int index, PagerManageView v) {
        super(a, id, itemList, selected, index, v);
    }

    @Override
    protected void bindAdapter(SearchActivity a, List<User> itemList) {
        userAdapter = new UserAdapter(a, itemList);
    }

    @Override
    protected FooterAdapter getAdapter() {
        return userAdapter;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(getContext(), DisplayUtils.getGirdColumnCount(getContext()));
    }

    @Override
    public void updateItem(User user, boolean refreshView) {
        userAdapter.updateUser(user, refreshView, true);
    }

    @Override
    protected String getInitFeedbackText() {
        return getContext().getString(R.string.feedback_search_users_tv);
    }

    // interface.

    // pager view.

    @Override
    public void notifyItemsRefreshed(int count) {
        userAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyItemsLoaded(int count) {
        userAdapter.notifyItemRangeInserted(userAdapter.getRealItemCount() - count, count);
    }

    @Override
    public boolean canSwipeBack(int dir) {
        return stateManagePresenter.getState() != State.NORMAL
                || SwipeBackCoordinatorLayout.canSwipeBack(recyclerView, dir)
                || userAdapter.getRealItemCount() <= 0;
    }

    @Override
    public int getItemCount() {
        if (stateManagePresenter.getState() != State.NORMAL) {
            return 0;
        } else {
            return userAdapter.getRealItemCount();
        }
    }

    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public RecyclerView.Adapter getRecyclerViewAdapter() {
        return userAdapter;
    }
}
