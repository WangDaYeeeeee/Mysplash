package com.wangdaye.mysplash.search.ui;

import android.annotation.SuppressLint;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.ui.adapter.CollectionAdapter;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.DisplayUtils;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

@SuppressLint("ViewConstructor")
public class CollectionSearchPageView extends AbstractSearchPageView<Collection> {

    private CollectionAdapter collectionAdapter;

    public CollectionSearchPageView(SearchActivity a, int id, List<Collection> itemList,
                                    boolean selected, int index, PagerManageView v) {
        super(a, id, itemList, selected, index, v);

        if (DisplayUtils.getGirdColumnCount(getContext()) > 1) {
            int margin = getResources().getDimensionPixelSize(R.dimen.normal_margin);
            recyclerView.setPadding(margin, margin, 0, 0);
        } else {
            recyclerView.setPadding(0, 0, 0, 0);
        }
    }

    @Override
    protected void bindAdapter(SearchActivity a, List<Collection> itemList) {
        collectionAdapter = new CollectionAdapter(a, itemList, DisplayUtils.getGirdColumnCount(a));
    }

    @Override
    protected FooterAdapter getAdapter() {
        return collectionAdapter;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new StaggeredGridLayoutManager(
                DisplayUtils.getGirdColumnCount(getContext()), StaggeredGridLayoutManager.VERTICAL);
    }

    @Override
    public void updateItem(Collection collection, boolean refreshView) {
        collectionAdapter.updateItem(recyclerView, collection, refreshView, false);
    }

    public void removeCollection(Collection c) {
        collectionAdapter.removeItem(c);
    }

    @Override
    protected String getInitFeedbackText() {
        return getContext().getString(R.string.feedback_search_collections_tv);
    }

    // interface.

    // pager view.

    @Override
    public void notifyItemsRefreshed(int count) {
        collectionAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyItemsLoaded(int count) {
        collectionAdapter.notifyItemRangeInserted(collectionAdapter.getRealItemCount() - count, count);
    }

    @Override
    public boolean canSwipeBack(int dir) {
        return stateManagePresenter.getState() != State.NORMAL
                || SwipeBackCoordinatorLayout.canSwipeBack(recyclerView, dir)
                || collectionAdapter.getRealItemCount() <= 0;
    }

    @Override
    public int getItemCount() {
        if (stateManagePresenter.getState() != State.NORMAL) {
            return 0;
        } else {
            return collectionAdapter.getRealItemCount();
        }
    }

    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public RecyclerView.Adapter getRecyclerViewAdapter() {
        return collectionAdapter;
    }
}
