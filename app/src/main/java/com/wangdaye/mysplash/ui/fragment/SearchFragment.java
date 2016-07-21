package com.wangdaye.mysplash.ui.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.data.unslpash.api.PhotoApi;
import com.wangdaye.mysplash.ui.activity.MainActivity;
import com.wangdaye.mysplash.ui.widget.StatusBarView;
import com.wangdaye.mysplash.ui.widget.widgetGroup.MainActivity.SearchContentView;
import com.wangdaye.mysplash.utils.SafeHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Search Fragment.
 * */

public class SearchFragment extends Fragment
        implements View.OnClickListener, EditText.OnEditorActionListener, SafeHandler.HandlerContainer,
        PopupMenu.OnMenuItemClickListener, Toolbar.OnMenuItemClickListener {
    // widget
    private EditText editText;
    private TextView orientationTxt;
    private ImageView menuIcon;
    private SearchContentView contentView;

    private SafeHandler<SearchFragment> handler;

    // data
    private String searchOrientation = PhotoApi.LANDSCAPE_ORIENTATION;
    private final int SHOW_KEYBOARD = 1;

    /** <br> life cycle. */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initWidget(view);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.obtainMessage(SHOW_KEYBOARD).sendToTarget();
            }
        }, 400);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        hideKeyboard();
    }

    /** <br> UI. */

    private void initWidget(View v) {
        this.handler = new SafeHandler<>(this);

        StatusBarView statusBar = (StatusBarView) v.findViewById(R.id.fragment_search_statusBar);
        if (Build.VERSION.SDK_INT <Build.VERSION_CODES.M) {
            statusBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            statusBar.setMask(true);
        }

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.fragment_search_toolbar);
        toolbar.inflateMenu(R.menu.menu_fragment_search_toolbar);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);

        this.editText = (EditText) v.findViewById(R.id.fragment_search_editText);
        editText.setOnEditorActionListener(this);
        editText.setFocusable(true);
        editText.requestFocus();

        RelativeLayout touchLayout = (RelativeLayout) v.findViewById(R.id.fragment_search_touchLayout);
        touchLayout.setOnClickListener(this);

        this.menuIcon = (ImageView) v.findViewById(R.id.fragment_search_menuIcon);

        this.orientationTxt = (TextView) v.findViewById(R.id.fragment_search_nowTxt);
        orientationTxt.setText(searchOrientation.toUpperCase());

        this.contentView = (SearchContentView) v.findViewById(R.id.fragment_search_contentView);
        contentView.setOnClickListener(this);
    }

    private void showKeyboard() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(editText, 0);
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                ((MainActivity) getActivity()).removeFragment();
                break;

            case R.id.fragment_search_touchLayout:
                PopupMenu menu = new PopupMenu(getContext(), menuIcon, Gravity.CENTER);
                menu.inflate(R.menu.menu_fragment_search_orientation);
                menu.setOnMenuItemClickListener(this);
                menu.show();
                break;

            case R.id.fragment_search_contentView:
                hideKeyboard();
                break;
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_orientation_landscape:
                searchOrientation = PhotoApi.LANDSCAPE_ORIENTATION;
                orientationTxt.setText(searchOrientation.toUpperCase());
                return true;

            case R.id.action_orientation_portrait:
                searchOrientation = PhotoApi.PORTRAIT_ORIENTAION;
                orientationTxt.setText(searchOrientation.toUpperCase());
                return true;

            case R.id.action_orientation_square:
                searchOrientation = PhotoApi.SQUARE_ORIENTATION;
                orientationTxt.setText(searchOrientation.toUpperCase());
                return true;

            case R.id.action_clear_text:
                editText.setText("");
                return true;
        }
        return false;
    }

    // on editor action clickListener.

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        String txt = textView.getText().toString();
        if (!txt.equals("")) {
            contentView.doSearch(txt, searchOrientation);
        }
        hideKeyboard();
        return true;
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case SHOW_KEYBOARD:
                showKeyboard();
                break;
        }
    }
}
