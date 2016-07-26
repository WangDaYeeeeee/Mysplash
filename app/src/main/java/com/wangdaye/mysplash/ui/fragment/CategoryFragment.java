package com.wangdaye.mysplash.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.data.constant.Mysplash;
import com.wangdaye.mysplash.ui.activity.MainActivity;
import com.wangdaye.mysplash.ui.widget.StatusBarView;
import com.wangdaye.mysplash.ui.widget.customWidget.CategoryPhotosView;
import com.wangdaye.mysplash.utils.ValueUtils;

/**
 * Category fragment.
 * */

public class CategoryFragment extends Fragment
        implements View.OnClickListener, Toolbar.OnMenuItemClickListener, CategoryPhotosView.OnStartActivityCallback {
    // widget
    private CategoryPhotosView photosView;

    // data
    private boolean normalMode = true;
    private int photoCategoryId = Mysplash.CATEGORY_BUILDINGS_ID;

    /** <br> life cycle. */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        initData();
        initWidget(view);
        photosView.initRefresh();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        photosView.cancelRequest();
    }

    /** <br> UI. */

    private void initWidget(View v) {
        StatusBarView statusBar = (StatusBarView) v.findViewById(R.id.fragment_category_statusBar);
        if (Build.VERSION.SDK_INT <Build.VERSION_CODES.M) {
            statusBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            statusBar.setMask(true);
        }

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.fragment_category_toolbar);
        toolbar.setTitle(ValueUtils.getToolbarTitleByCategory(getActivity(), photoCategoryId));
        if (normalMode) {
            toolbar.inflateMenu(R.menu.menu_fragment_category_normal);
        } else {
            toolbar.inflateMenu(R.menu.menu_fragment_category_random);
        }
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnClickListener(this);

        this.photosView = (CategoryPhotosView) v.findViewById(R.id.fragment_category_categoryPhotosView);
        photosView.setCategory(photoCategoryId);
        photosView.setMode(normalMode);
        photosView.setOnStartActivityCallback(this);
    }

    /** <br> data. */

    private void initData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.normalMode = sharedPreferences.getBoolean(
                getString(R.string.key_normal_mode),
                true);
    }

    public void setCategory(int category) {
        this.photoCategoryId = category;
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.activity_main_drawerLayout);
                drawer.openDrawer(GravityCompat.START);
                break;

            case R.id.fragment_category_toolbar:
                photosView.scrollToTop();
                break;
        }
    }

    // on start activity callback.

    @Override
    public void startActivity(Intent intent, View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeScaleUpAnimation(
                            view,
                            (int) view.getX(), (int) view.getY(),
                            view.getMeasuredWidth(), view.getMeasuredHeight());
            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
        } else {
            View imageView = ((RelativeLayout) ((CardView) view).getChildAt(0)).getChildAt(0);
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                            getActivity(),
                            Pair.create(imageView, getString(R.string.transition_photo_image)));
            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.putBoolean(
                getString(R.string.key_normal_mode),
                item.getItemId() == R.id.action_normal_mode);
        editor.apply();

        CategoryFragment fragment = new CategoryFragment();
        fragment.setCategory(photoCategoryId);
        ((MainActivity) getActivity()).changeFragment(fragment);
        return true;
    }
}
