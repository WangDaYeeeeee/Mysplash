package com.wangdaye.mysplash.search.ui;

import android.annotation.SuppressLint;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.presenter.PagerLoadablePresenter;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.DisplayUtils;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

@SuppressLint("ViewConstructor")
public class PhotoSearchPageView extends AbstractSearchPageView<Photo>
        implements SelectCollectionDialog.OnCollectionsChangedListener {

    private PhotoAdapter photoAdapter;

    PagerLoadablePresenter loadMorePresenter;

    public PhotoSearchPageView(SearchActivity a, int id, List<Photo> itemList,
                               boolean selected, int index, PagerManageView v) {
        super(a, id, itemList, selected, index, v);

        if (DisplayUtils.getGirdColumnCount(getContext()) > 1) {
            int margin = getResources().getDimensionPixelSize(R.dimen.normal_margin);
            recyclerView.setPadding(margin, margin, 0, 0);
        } else {
            recyclerView.setPadding(0, 0, 0, 0);
        }

        loadMorePresenter = new PagerLoadablePresenter(
                this, recyclerView, photoAdapter, pagerManageView) {
            @Override
            public List<Photo> subList(int fromIndex, int toIndex) {
                return photoAdapter.getPhotoData().subList(fromIndex, toIndex);
            }
        };
    }

    @Override
    protected void bindAdapter(SearchActivity a, List<Photo> itemList) {
        photoAdapter = new PhotoAdapter(a, itemList, DisplayUtils.getGirdColumnCount(a));
        photoAdapter.setItemEventCallback(a);
    }

    @Override
    protected FooterAdapter getAdapter() {
        return photoAdapter;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new StaggeredGridLayoutManager(
                DisplayUtils.getGirdColumnCount(getContext()), StaggeredGridLayoutManager.VERTICAL);
    }

    @Override
    public void updateItem(Photo photo, boolean refreshView) {
        photoAdapter.updatePhoto(photo, refreshView, true);
    }

    @Override
    protected String getInitFeedbackText() {
        return getContext().getString(R.string.feedback_search_photos_tv);
    }

    public List<Photo> loadMore(List<Photo> list, int headIndex, boolean headDirection) {
        return loadMorePresenter.loadMore(list, headIndex, headDirection, index);
    }

    // interface.

    // pager view.

    @Override
    public void notifyItemsRefreshed(int count) {
        photoAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyItemsLoaded(int count) {
        photoAdapter.notifyItemRangeInserted(photoAdapter.getRealItemCount() - count, count);
    }

    @Override
    public boolean canSwipeBack(int dir) {
        return stateManagePresenter.getState() != State.NORMAL
                || SwipeBackCoordinatorLayout.canSwipeBack(recyclerView, dir)
                || photoAdapter.getRealItemCount() <= 0;
    }

    @Override
    public int getItemCount() {
        if (stateManagePresenter.getState() != State.NORMAL) {
            return 0;
        } else {
            return photoAdapter.getRealItemCount();
        }
    }

    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public RecyclerView.Adapter getRecyclerViewAdapter() {
        return photoAdapter;
    }

    // on collections changed listener.

    @Override
    public void onAddCollection(Collection c) {
        // do nothing.
    }

    @Override
    public void onUpdateCollection(Collection c, User u, Photo p) {
        photoAdapter.updatePhoto(p, true, true);
    }
}
