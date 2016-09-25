package com.wangdaye.mysplash.main.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.MultiFilterBarModel;
import com.wangdaye.mysplash._common.i.presenter.MultiFilterBarPresenter;
import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.view.MultiFilterBarView;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.ui.popup.SearchCategoryPopupWindow;
import com.wangdaye.mysplash._common.ui.popup.SearchFeaturedPopupWindow;
import com.wangdaye.mysplash._common.ui.popup.SearchOrientationPopupWindow;
import com.wangdaye.mysplash._common.ui.widget.StatusBarView;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash._common.utils.ValueUtils;
import com.wangdaye.mysplash.main.model.fragment.MultiFilterBarObject;
import com.wangdaye.mysplash.main.presenter.fragment.MultiFilterBarImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.MultiFilterFragmentPopupManageImplementor;
import com.wangdaye.mysplash.main.view.widget.MultiFilterPhotosView;

/**
 * Multi filter fragment.
 * */

public class MultiFilterFragment extends Fragment
        implements MultiFilterBarView, PopupManageView,
        View.OnClickListener,  NotificationUtils.SnackbarContainer {
    // model.
    private MultiFilterBarModel multiFilterBarModel;

    // view.
    private CoordinatorLayout container;
    private EditText[] editTexts;
    private TextView[] menuTexts;
    private ImageView[] menuIcons;
    private MultiFilterPhotosView photosView;

    // presenter.
    private MultiFilterBarPresenter multiFilterBarPresenter;
    private PopupManagePresenter popupManagePresenter;

    /** <br> life cycle. */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multi_filter, container, false);
        initModel();
        initView(view);
        initPresenter();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        photosView.cancelRequest();
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.multiFilterBarPresenter = new MultiFilterBarImplementor(multiFilterBarModel, this);
        this.popupManagePresenter = new MultiFilterFragmentPopupManageImplementor(this);
    }

    /** <br> view. */

    // init.

    private void initView(View v) {
        StatusBarView statusBar = (StatusBarView) v.findViewById(R.id.fragment_multi_filter_statusBar);
        if (ThemeUtils.getInstance(getActivity()).isNeedSetStatusBarMask()) {
            statusBar.setMask(true);
        }

        this.container = (CoordinatorLayout) v.findViewById(R.id.fragment_multi_filter_container);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.fragment_multi_filter_toolbar);
        toolbar.setTitle(getString(R.string.action_multi_filter));
        if (ThemeUtils.getInstance(getActivity()).isLightTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_menu_light);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_menu_dark);
        }
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnClickListener(this);

        this.editTexts = new EditText[] {
                (EditText) v.findViewById(R.id.fragment_multi_filter_photos_editText),
                (EditText) v.findViewById(R.id.fragment_multi_filter_users_editText)};

        v.findViewById(R.id.fragment_multi_filter_categoryContainer).setOnClickListener(this);
        v.findViewById(R.id.fragment_multi_filter_orientationContainer).setOnClickListener(this);
        v.findViewById(R.id.fragment_multi_filter_featuredContainer).setOnClickListener(this);

        this.menuTexts = new TextView[] {
                (TextView) v.findViewById(R.id.fragment_multi_filter_categoryTxt),
                (TextView) v.findViewById(R.id.fragment_multi_filter_orientationTxt),
                (TextView) v.findViewById(R.id.fragment_multi_filter_featuredTxt)};
        for (TextView t : menuTexts) {
            t.setText(R.string.all);
        }

        this.menuIcons = new ImageView[] {
                (ImageView) v.findViewById(R.id.fragment_multi_filter_categoryBtn),
                (ImageView) v.findViewById(R.id.fragment_multi_filter_orientationBtn),
                (ImageView) v.findViewById(R.id.fragment_multi_filter_featuredBtn)};
        for (ImageView i : menuIcons) {
            if (ThemeUtils.getInstance(getActivity()).isLightTheme()) {
                i.setImageResource(R.drawable.ic_menu_down_light);
            } else {
                i.setImageResource(R.drawable.ic_menu_down_dark);
            }
        }

        this.photosView = (MultiFilterPhotosView) v.findViewById(R.id.fragment_multi_filter_photosView);
        photosView.setActivity(getActivity());
    }

    // interface.

    public void pagerBackToTop() {
        photosView.pagerScrollToTop();
    }

    public void showPopup(int position) {
        switch (position) {
            case 0:
                popupManagePresenter.showPopup(
                        getActivity(),
                        menuIcons[0],
                        String.valueOf(multiFilterBarPresenter.getCategory()),
                        0);
                break;

            case 1:
                popupManagePresenter.showPopup(
                        getActivity(),
                        menuIcons[1],
                        multiFilterBarPresenter.getOrientation(),
                        1);
                break;

            case 2:
                popupManagePresenter.showPopup(
                        getActivity(),
                        menuIcons[2],
                        String.valueOf(multiFilterBarPresenter.isFeatured()),
                        2);
                break;
        }
    }

    /** <br> model. */

    // init.

    private void initModel() {
        this.multiFilterBarModel = new MultiFilterBarObject();
    }

    // interface.

    public boolean needPagerBackToTop() {
        return photosView.needPagerBackToTop();
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                multiFilterBarPresenter.touchNavigatorIcon(getActivity());
                break;
        }
    }

    // snackbar container.

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    // view.

    // multi-filter view.

    @Override
    public void touchMenuContainer(int position) {
        showPopup(position);
    }

    @Override
    public void showKeyboard() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(editTexts[0], 0);
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editTexts[0].getWindowToken(), 0);
        manager.hideSoftInputFromWindow(editTexts[1].getWindowToken(), 0);
    }

    @Override
    public void submitSearchInfo(int categoryId, boolean featured,
                                 String username, String query, String orientation) {
        photosView.doSearch(categoryId, featured, username, query, orientation);
    }

    // popup manage view.

    @Override
    public void responsePopup(String value, int position) {
        switch (position) {
            case 0:
                multiFilterBarPresenter.setCategory(Integer.parseInt(value));
                break;

            case 1:
                multiFilterBarPresenter.setOrientation(value);
                break;

            case 2:
                multiFilterBarPresenter.setFeatured(Boolean.parseBoolean(value));
                break;
        }
    }
}
