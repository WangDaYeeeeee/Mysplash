package com.wangdaye.mysplash.main.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.ModeUtils;
import com.wangdaye.mysplash.main.model.fragment.CategoryObject;
import com.wangdaye.mysplash.main.model.fragment.i.CategoryModel;
import com.wangdaye.mysplash.main.presenter.fragment.CategoryMenuImp;
import com.wangdaye.mysplash.main.presenter.fragment.ToolbarImp;
import com.wangdaye.mysplash.main.presenter.fragment.i.CategoryMenuPresenter;
import com.wangdaye.mysplash.main.presenter.fragment.i.ToolbarPresenter;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.common.widget.StatusBarView;
import com.wangdaye.mysplash.main.view.fragment.i.FragmentView;
import com.wangdaye.mysplash.main.view.fragment.i.ToolbarView;
import com.wangdaye.mysplash.main.view.widget.CategoryPhotosView;
import com.wangdaye.mysplash.common.utils.ValueUtils;

/**
 * Category fragment.
 * */

public class CategoryFragment extends Fragment
        implements ToolbarView, FragmentView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener {
    // model.
    private CategoryModel categoryModel;

    // view.
    private CategoryPhotosView photosView;

    // presenter.
    private ToolbarPresenter toolbarPresenter;
    private CategoryMenuPresenter categoryMenuPresenter;

    /** <br> life cycle. */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        initView(view);
        initPresenter();
        photosView.initRefresh();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        photosView.cancelRequest();
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImp(null, this);
        this.categoryMenuPresenter = new CategoryMenuImp(categoryModel, this);
    }

    /** <br> view. */

    private void initView(View v) {
        StatusBarView statusBar = (StatusBarView) v.findViewById(R.id.fragment_category_statusBar);
        if (ModeUtils.getInstance(getActivity()).isNeedSetStatusBarMask()) {
            statusBar.setMask(true);
        }

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.fragment_category_toolbar);
        toolbar.setTitle(ValueUtils.getToolbarTitleByCategory(getActivity(), categoryModel.getCategoryId()));
        if (ModeUtils.getInstance(getActivity()).isNormalMode()) {
            toolbar.inflateMenu(R.menu.menu_fragment_category_normal);
        } else {
            toolbar.inflateMenu(R.menu.menu_fragment_category_random);
        }
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnClickListener(this);

        this.photosView = (CategoryPhotosView) v.findViewById(R.id.fragment_category_categoryPhotosView);
        photosView.setActivity(getActivity());
        photosView.setCategoryId(categoryModel.getCategoryId());
        photosView.setNormalMode(ModeUtils.getInstance(getActivity()).isNormalMode());
    }

    /** <br> model. */

    public void initModel(int categoryId) {
        this.categoryModel = new CategoryObject(categoryId);
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                toolbarPresenter.clickNavigationIcon();
                break;

            case R.id.fragment_category_toolbar:
                toolbarPresenter.clickToolbar();
                break;
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_random_mode:
                categoryMenuPresenter.clickRandomItem(getActivity());
                break;

            case R.id.action_normal_mode:
                categoryMenuPresenter.clickNormalItem(getActivity());
                break;
        }
        return true;
    }

    // view.

    // toolbar view.

    @Override
    public void clickNavigationIcon() {
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.activity_main_drawerLayout);
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void scrollToTop(int i) {
        photosView.scrollToTop();
    }

    // fragment view.

    @Override
    public void addFragment(Fragment f) {
        // do nothing.
    }

    @Override
    public void changeFragment(Fragment f) {
        ((MainActivity) getActivity()).changeFragment(f);
    }
}