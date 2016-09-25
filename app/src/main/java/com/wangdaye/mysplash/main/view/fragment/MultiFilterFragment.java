package com.wangdaye.mysplash.main.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.MultiFilterBarModel;
import com.wangdaye.mysplash._common.i.presenter.MultiFilterBarPresenter;
import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.view.MultiFilterBarView;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.ui.widget.StatusBarView;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;
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
        View.OnClickListener, NotificationUtils.SnackbarContainer {
    // model.
    private MultiFilterBarModel multiFilterBarModel;

    // view.
    private CoordinatorLayout container;
    private EditText[] editTexts;
    private TextView[] menuTexts;
    private ImageButton[] menuIcons;
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
        multiFilterBarPresenter.hideKeyboard();
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
        editTexts[0].setOnEditorActionListener(queryEditorActionListener);
        editTexts[1].setOnEditorActionListener(userEditorActionListener);
        TypefaceUtils.setTypeface(getActivity(), editTexts[0]);
        TypefaceUtils.setTypeface(getActivity(), editTexts[1]);

        v.findViewById(R.id.fragment_multi_filter_categoryContainer).setOnClickListener(this);
        v.findViewById(R.id.fragment_multi_filter_orientationContainer).setOnClickListener(this);
        v.findViewById(R.id.fragment_multi_filter_featuredContainer).setOnClickListener(this);

        this.menuTexts = new TextView[] {
                (TextView) v.findViewById(R.id.fragment_multi_filter_categoryTxt),
                (TextView) v.findViewById(R.id.fragment_multi_filter_orientationTxt),
                (TextView) v.findViewById(R.id.fragment_multi_filter_featuredTxt)};
        for (TextView t : menuTexts) {
            t.setText(R.string.all);
            TypefaceUtils.setTypeface(getActivity(), t);
        }

        this.menuIcons = new ImageButton[] {
                (ImageButton) v.findViewById(R.id.fragment_multi_filter_categoryBtn),
                (ImageButton) v.findViewById(R.id.fragment_multi_filter_orientationBtn),
                (ImageButton) v.findViewById(R.id.fragment_multi_filter_featuredBtn)};
        for (ImageButton b : menuIcons) {
            if (ThemeUtils.getInstance(getActivity()).isLightTheme()) {
                b.setImageResource(R.drawable.ic_menu_down_light);
            } else {
                b.setImageResource(R.drawable.ic_menu_down_dark);
            }
            b.setOnClickListener(this);
        }

        this.photosView = (MultiFilterPhotosView) v.findViewById(R.id.fragment_multi_filter_photosView);
        photosView.setActivity(getActivity());
        photosView.setOnClickListener(this);
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

            case R.id.fragment_multi_filter_toolbar:
                multiFilterBarPresenter.touchToolbar(getActivity());
                break;

            case R.id.fragment_multi_filter_photosView:
                multiFilterBarPresenter.hideKeyboard();
                break;

            case R.id.fragment_multi_filter_categoryBtn:
            case R.id.fragment_multi_filter_categoryContainer:
                showPopup(0);
                break;

            case R.id.fragment_multi_filter_orientationBtn:
            case R.id.fragment_multi_filter_orientationContainer:
                showPopup(1);
                break;

            case R.id.fragment_multi_filter_featuredBtn:
            case R.id.fragment_multi_filter_featuredContainer:
                showPopup(2);
                break;
        }
    }

    // on editor action listener.

    private EditText.OnEditorActionListener queryEditorActionListener
            = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            multiFilterBarPresenter.setQuery(v.getText().toString());
            multiFilterBarPresenter.hideKeyboard();
            multiFilterBarPresenter.submitSearchInfo();
            return true;
        }
    };

    private EditText.OnEditorActionListener userEditorActionListener
            = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            multiFilterBarPresenter.setUsername(v.getText().toString());
            multiFilterBarPresenter.hideKeyboard();
            multiFilterBarPresenter.submitSearchInfo();
            return true;
        }
    };

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
    public void submitSearchInfo() {
        photosView.doSearch(
                multiFilterBarPresenter.getCategory(),
                multiFilterBarModel.isFeatured(),
                multiFilterBarPresenter.getUsername(),
                multiFilterBarPresenter.getQuery(),
                multiFilterBarPresenter.getOrientation());
    }

    // popup manage view.

    @Override
    public void responsePopup(String value, int position) {
        switch (position) {
            case 0:
                multiFilterBarPresenter.setCategory(Integer.parseInt(value));
                if (Integer.parseInt(value) == 0) {
                    menuTexts[0].setText(R.string.all);
                } else {
                    menuTexts[0].setText(
                            ValueUtils.getToolbarTitleByCategory(
                                    getContext(),
                                    Integer.parseInt(value)));
                }
                break;

            case 1:
                multiFilterBarPresenter.setOrientation(value);
                if (TextUtils.isEmpty(value)) {
                    menuTexts[1].setText(R.string.all);
                } else {
                    menuTexts[1].setText(value);
                }
                break;

            case 2:
                multiFilterBarPresenter.setFeatured(Boolean.parseBoolean(value));
                if (Boolean.parseBoolean(value)) {
                    menuTexts[2].setText(getResources().getStringArray(R.array.collection_types)[2]);
                } else {
                    menuTexts[2].setText(R.string.all);
                }
                break;
        }
    }
}
