package com.wangdaye.mysplash.main.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.SearchBarModel;
import com.wangdaye.mysplash._common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.SearchBarPresenter;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;
import com.wangdaye.mysplash._common.utils.ValueUtils;
import com.wangdaye.mysplash._common.i.view.MessageManageView;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.i.view.SearchBarView;
import com.wangdaye.mysplash.main.model.fragment.SearchBarObject;
import com.wangdaye.mysplash.main.presenter.activity.MessageManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.SearchBarImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.SearchFragmentPopupManageImplementor;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash._common.ui.widget.StatusBarView;
import com.wangdaye.mysplash.main.view.widget.SearchPhotosView;
import com.wangdaye.mysplash._common.utils.SafeHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Search Fragment.
 * */

public class SearchFragment extends Fragment
        implements SearchBarView, MessageManageView, PopupManageView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, EditText.OnEditorActionListener,
        SafeHandler.HandlerContainer {
    // model.
    private SearchBarModel searchBarModel;

    // view.
    private EditText editText;
    private TextView orientationTxt;
    private ImageView menuIcon;
    private SearchPhotosView contentView;

    private SafeHandler<SearchFragment> handler;

    // presenter.
    private SearchBarPresenter searchBarPresenter;
    private MessageManagePresenter messageManagePresenter;
    private PopupManagePresenter popupManagePresenter;

    /** <br> life cycle. */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initModel();
        initView(view);
        initPresenter();
        messageManagePresenter.sendMessage(1, null);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchBarPresenter.hideKeyboard();
        handler.removeCallbacksAndMessages(null);
        contentView.cancelRequest();
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.searchBarPresenter = new SearchBarImplementor(searchBarModel, this);
        this.messageManagePresenter = new MessageManageImplementor(this);
        this.popupManagePresenter = new SearchFragmentPopupManageImplementor(this);
    }

    /** <br> view. */

    // init.

    private void initView(View v) {
        this.handler = new SafeHandler<>(this);

        StatusBarView statusBar = (StatusBarView) v.findViewById(R.id.fragment_search_statusBar);
        if (ThemeUtils.getInstance(getActivity()).isNeedSetStatusBarMask()) {
            statusBar.setMask(true);
        }

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.fragment_search_toolbar);
        if (ThemeUtils.getInstance(getActivity()).isLightTheme()) {
            toolbar.inflateMenu(R.menu.fragment_search_toolbar_light);
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
        } else {
            toolbar.inflateMenu(R.menu.fragment_search_toolbar_dark);
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
        }
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);

        this.editText = (EditText) v.findViewById(R.id.fragment_search_editText);
        TypefaceUtils.setTypeface(getActivity(), editText);
        editText.setOnEditorActionListener(this);
        editText.setFocusable(true);
        editText.requestFocus();

        RelativeLayout orientationMenu = (RelativeLayout) v.findViewById(R.id.fragment_search_orientationMenu);
        orientationMenu.setOnClickListener(this);

        this.menuIcon = (ImageView) v.findViewById(R.id.fragment_search_menuIcon);
        if (ThemeUtils.getInstance(getActivity()).isLightTheme()) {
            menuIcon.setImageResource(R.drawable.ic_menu_down_light);
        } else {
            menuIcon.setImageResource(R.drawable.ic_menu_down_dark);
        }

        this.orientationTxt = (TextView) v.findViewById(R.id.fragment_search_nowTxt);
        TypefaceUtils.setTypeface(getActivity(), orientationTxt);
        orientationTxt.setText(ValueUtils.getOrientationName(getActivity(), searchBarModel.getOrientation()));

        this.contentView = (SearchPhotosView) v.findViewById(R.id.fragment_search_contentView);
        contentView.setActivity(getActivity());
        contentView.setOnClickListener(this);
    }

    // interface.

    public void pagerBackToTop() {
        contentView.pagerScrollToTop();
    }

    /** <br> model. */

    // init.

    private void initModel() {
        this.searchBarModel = new SearchBarObject();
    }

    // interface.

    public boolean needPagerBackToTop() {
        return contentView.needPagerBackToTop();
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                searchBarPresenter.touchNavigatorIcon();
                break;

            case R.id.fragment_search_orientationMenu:
                searchBarPresenter.touchOrientationIcon();
                break;

            case R.id.fragment_search_contentView:
                searchBarPresenter.hideKeyboard();
                break;
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        searchBarPresenter.touchMenuItem(item.getItemId());
        return true;
    }

    // on editor action clickListener.

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        String text = textView.getText().toString();
        if (!text.equals("")) {
            searchBarPresenter.submitSearchInfo(text);
        }
        searchBarPresenter.hideKeyboard();
        return true;
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        messageManagePresenter.responseMessage(message.what, message.obj);
    }

    // view.

    // search bar view.

    @Override
    public void touchNavigatorIcon() {
        ((MainActivity) getActivity()).removeFragment();
    }

    @Override
    public void touchMenuItem(int itemId) {
        switch (itemId) {
            case R.id.action_clear_text:
                editText.setText("");
                break;
        }
    }

    @Override
    public void touchOrientationIcon() {
        popupManagePresenter.showPopup(getActivity(), menuIcon, searchBarModel.getOrientation(), 0);
    }

    @Override
    public void touchSearchBar() {
        // do nothing.
    }

    @Override
    public void showKeyboard() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(editText, 0);
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void submitSearchInfo(String text, String orientation) {
        contentView.doSearch(text, orientation);
    }

    // message manage view.

    @Override
    public void sendMessage(final int what, final Object o) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.obtainMessage(what, o).sendToTarget();
            }
        }, 400);
    }

    @Override
    public void responseMessage(int what, Object o) {
        switch (what) {
            case 1:
                showKeyboard();
                break;
        }
    }

    // popup manage view.

    @Override
    public void responsePopup(String value, int position) {
        searchBarPresenter.setOrientation(value);
        orientationTxt.setText(ValueUtils.getOrientationName(getActivity(), value));
    }
}
